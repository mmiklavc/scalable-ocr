package ocr.nifi.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import ocr.nifi.util.JSONUtils;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

import java.io.IOException;
import java.util.Map;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;

public class JsonValidator implements Validator {
    @Override
    public ValidationResult validate(String subject, String input, ValidationContext context) {
        try {
            JSONUtils.INSTANCE.load(input, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            return new ValidationResult.Builder()
                    .subject(subject)
                    .input(value)
                    .valid(false)
                    .explanation("Not a valid JSON map value: " + e.getMessage())
                    .build();
        }
        return new ValidationResult.Builder()
                .valid(true)
                .input(value)
                .subject(subject)
                .build();
    }
}
