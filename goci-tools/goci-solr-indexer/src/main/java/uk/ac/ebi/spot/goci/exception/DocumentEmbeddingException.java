package uk.ac.ebi.spot.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class DocumentEmbeddingException extends RuntimeException {
    public DocumentEmbeddingException() {
    }

    public DocumentEmbeddingException(String message) {
        super(message);
    }

    public DocumentEmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentEmbeddingException(Throwable cause) {
        super(cause);
    }

    public DocumentEmbeddingException(String message,
                                      Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
