package ocr.nifi.preprocessing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ocr.preprocessing.conversion.CLIUtils;
import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.CommandFailedException;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ProcessorLog;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@SideEffectFree
@Tags({"ocr preprocessing", "image manipulation"})
@CapabilityDescription("Preprocess images of text documents to clean them")
public class Processor extends AbstractProcessor {
  private static PropertyDescriptor DEFINITIONS = new PropertyDescriptor.Builder()
                                                                        .name("definition")
                                                                        .description(CleaningOptions.getUsage())
                                                                        .required(true)
                                                                        .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
                                                                        .addValidator(
                                                                  (subject, value, context) ->  {
                                                                    boolean valid = true;
                                                                    String explanation = "";
                                                                    try {
                                                                      CleaningOptions.parse(new DefaultParser()
                                                                                           , CLIUtils.translateCommandline(value)
                                                                                           );
                                                                    }
                                                                    catch(Throwable t) {
                                                                      valid = false;
                                                                      explanation = t.getMessage();
                                                                    }
                                                                    return
                                                                    new ValidationResult.Builder()
                                                                                        .subject(subject)
                                                                                        .input(value)
                                                                                        .valid(valid)
                                                                                        .explanation(explanation)
                                                                                        .build();
                                                                  }
                                                                                     )
                                                                        .build();
  private static PropertyDescriptor TEMP_DIR = new PropertyDescriptor.Builder()
                                                                     .name("temp_space")
                                                                     .description("Temporary directory to be used.")
                                                                     .required(false)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  private static PropertyDescriptor CONVERT_PATH = new PropertyDescriptor.Builder()
                                                                     .name("convert_bin_path")
                                                                     .description("The path to the convert (imagemagick) utility")
                                                                     .required(true)
                                                                     .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
                                                                     .build();
  private static Relationship SUCCESS  = new Relationship.Builder()
                                                         .name("SUCCESS")
                                                         .description("Success relationship")
                                                         .build();
  private List<PropertyDescriptor> properties = ImmutableList.of( DEFINITIONS ,TEMP_DIR, CONVERT_PATH );

  private Set<Relationship> relationships = ImmutableSet.of( SUCCESS );

  @Override
  protected void init(ProcessorInitializationContext context) {
  }

  @Override
  public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
    final ProcessorLog log = this.getLogger();
    final AtomicReference<byte[]> value = new AtomicReference<>();
    String preprocessingDef = context.getProperty(DEFINITIONS).getValue();
    String tempDir = context.getProperty(TEMP_DIR).getValue();
    String convertPath = context.getProperty(CONVERT_PATH).getValue();
    CommandLine cli = CleaningOptions.parse(new DefaultParser(), CLIUtils.translateCommandline(preprocessingDef) );
    final TextCleaner cleaner = CleaningOptions.createTextCleaner(cli, convertPath, tempDir);
    FlowFile flowfile = session.get();
    session.read(flowfile, in -> {
      try {
        value.set(cleaner.convert(in));
      } catch (Exception e) {
        value.set(IOUtils.toByteArray(in));
        log.error("Unable to execute command: " + e.getMessage(), e);
      }
    });
    flowfile = session.write(flowfile, out -> out.write(value.get()));
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
