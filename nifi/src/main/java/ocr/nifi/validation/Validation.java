package ocr.nifi.validation;

import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.components.Validator;

public class Validation {
    public enum Validators implements Validator {
        JSON_MAP(new JsonValidator());

        private Validator validator;

        Validators(Validator validator) {
            this.validator = validator;
        }

        @Override
        public ValidationResult validate(String subject, String input, ValidationContext context) {
            return validator.validate(subject, input, context);
        }
    }
}
