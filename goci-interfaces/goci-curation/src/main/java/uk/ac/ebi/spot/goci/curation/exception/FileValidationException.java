package uk.ac.ebi.spot.goci.curation.exception;

import org.springframework.validation.BindingResult;

public class FileValidationException extends RuntimeException {

    private BindingResult bindingResult;

    public FileValidationException(BindingResult bindingResult) {
        super("Validation Errors");
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
