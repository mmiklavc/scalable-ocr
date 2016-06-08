package ocr.nifi.extraction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ocr.extraction.tesseract.TesseractUtil;
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

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
@SideEffectFree
@Tags({"ocr"})
@CapabilityDescription("Extracts text from images")
public class ExtractionProcessor extends AbstractProcessor {
  static PropertyDescriptor JNI_PATH = new PropertyDescriptor.Builder()
                                                                     .name("jni_path")
                                                                     .description("JNI Path")
                                                                     .required(true)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  static PropertyDescriptor TESS_DATA = new PropertyDescriptor.Builder()
                                                                     .name("tess_data_dir")
                                                                     .description("Tesseract data directory")
                                                                     .required(true)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  static Relationship SUCCESS  = new Relationship.Builder()
                                                         .name("SUCCESS")
                                                         .description("Success relationship")
                                                         .build();
  private List<PropertyDescriptor> properties = ImmutableList.of(TESS_DATA, JNI_PATH);

  private Set<Relationship> relationships = ImmutableSet.of( SUCCESS );

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    final ProcessorLog log = this.getLogger();
    final AtomicReference<String> value = new AtomicReference<>();
    final File tessDataDir = new File(context.getProperty(TESS_DATA).getValue());
    System.getProperties().setProperty("jna.library.path", context.getProperty(JNI_PATH).getValue());
    FlowFile flowfile = session.get();
    session.read(flowfile, in -> {
      try {
        value.set(TesseractUtil.INSTANCE.ocr(in, tessDataDir));
      } catch (Exception e) {
        log.error("Unable to ocr: " + e.getMessage(), e);
      }
    });

    flowfile = session.write(flowfile, out -> out.write(value.get().getBytes()));
    session.transfer(flowfile, SUCCESS);
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
