package ocr.nifi.conversion;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import ocr.conversion.Converter;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SideEffectFree
@Tags({"ocr preprocessing", "image manipulation"})
@CapabilityDescription("Preprocess images of text documents extract pages and data")
public class Processor extends AbstractProcessor {
  private static PropertyDescriptor JNI_PATH = new PropertyDescriptor.Builder()
                                                                     .name("jni_path")
                                                                     .description("JNI Path")
                                                                     .required(true)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  private static PropertyDescriptor TEMP_DIR = new PropertyDescriptor.Builder()
                                                                     .name("temp_space")
                                                                     .description("Temporary directory to be used.")
                                                                     .required(false)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  private static Relationship SUCCESS  = new Relationship.Builder()
                                                         .name("SUCCESS")
                                                         .description("Success relationship")
                                                         .build();
  private List<PropertyDescriptor> properties = ImmutableList.of(TEMP_DIR, JNI_PATH);

  private Set<Relationship> relationships = ImmutableSet.of( SUCCESS );
  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    final ProcessorLog log = this.getLogger();
    final AtomicReference<List<Map.Entry<File, Boolean>>> value = new AtomicReference<>();
    final File tempDir = new File(context.getProperty(TEMP_DIR).getValue());
    FlowFile flowfile = session.get();
    session.read(flowfile, in -> {
      try {
        value.set(convert(in, tempDir));
      }
      catch(Exception e) {
        log.error("Unable to convert: " + e.getMessage(), e);
      }
    });
    if(value.get() != null) {
      for(Map.Entry<File, Boolean> kv : value.get()) {
        final File convertedFile = kv.getKey();
        try {
          final int pageNumber = getPageNumber(convertedFile.getName());
          if(kv.getValue()) {
            FlowFile ff = session.clone(flowfile);
            ff = session.putAttribute(ff, "pageNumber", "" + pageNumber);
            ff = session.write(ff, out -> IOUtils.copy(new BufferedInputStream(new FileInputStream(convertedFile)), out));
            session.transfer(ff, SUCCESS);
          }
        }
        finally {
          if(convertedFile != null && convertedFile.exists()) {
            convertedFile.delete();
          }
        }
      }

    }
  }

  private int getPageNumber(String fileName) {
    Iterable<String> it = Splitter.on(".tiff").split(fileName);
    String first = Iterables.getFirst(it, null);
    return Integer.parseInt(Iterables.getLast(Splitter.on("-").split(first)));
  }
  private List<Map.Entry<File, Boolean>> convert(InputStream in, File tempDir) {
    Converter converter = new Converter();
    if(!tempDir.exists()) {
      tempDir.mkdirs();
    }
    List<Map.Entry<File, Boolean>> ret = new ArrayList<>();
    for(Map.Entry<File, Boolean> kv : converter.toJava(converter.convert(in, tempDir))) {
      ret.add(new AbstractMap.SimpleEntry<>(kv.getKey(), kv.getValue()));
    }
    return ret;
  }

  @Override
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  @Override
  protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
    return properties;
  }
}
