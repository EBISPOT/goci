package uk.ac.ebi.spot.goci.service.exception;

/**
 * Created by emma on 16/01/15.
 */
public class PubmedLookupException extends RuntimeException {

    public PubmedLookupException() {
    }

    public PubmedLookupException(String message) {
        super(message);
    }

    public PubmedLookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubmedLookupException(Throwable cause) {
        super(cause);
    }

    public PubmedLookupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
