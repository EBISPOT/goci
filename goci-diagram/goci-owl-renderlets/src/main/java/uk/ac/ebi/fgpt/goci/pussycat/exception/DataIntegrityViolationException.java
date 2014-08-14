package uk.ac.ebi.fgpt.goci.pussycat.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/08/14
 */
public class DataIntegrityViolationException extends Exception {
    public DataIntegrityViolationException() {
    }

    public DataIntegrityViolationException(String message) {
        super(message);
    }

    public DataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIntegrityViolationException(Throwable cause) {
        super(cause);
    }

    public DataIntegrityViolationException(String message,
                                           Throwable cause,
                                           boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
