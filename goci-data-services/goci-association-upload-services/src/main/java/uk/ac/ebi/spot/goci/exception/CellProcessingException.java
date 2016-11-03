package uk.ac.ebi.spot.goci.exception;

/**
 * Created by cinzia on 25/10/2016.
  *   Exception used when XLS convertation failed. Eg. convertion String, Integer, Float.
 *
 */
public class CellProcessingException extends RuntimeException {
    public CellProcessingException() {
        super();
    }

    public CellProcessingException(String message) {
        super(message);
    }

    public CellProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellProcessingException(Throwable cause) {
        super(cause);
    }

    protected CellProcessingException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}