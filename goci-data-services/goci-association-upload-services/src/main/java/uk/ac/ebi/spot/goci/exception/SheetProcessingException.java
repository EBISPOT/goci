package uk.ac.ebi.spot.goci.exception;

/**
 * Created by emma on 14/06/2016.
 *
 * @author emma
 *         <p>
 *         Exception used when user attempts to uploadi a file containing association data related to a study but the
 *         file cannot be processed into a sheet
 */
public class SheetProcessingException extends RuntimeException {
    public SheetProcessingException() {
        super();
    }

    public SheetProcessingException(String message) {
        super(message);
    }

    public SheetProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SheetProcessingException(Throwable cause) {
        super(cause);
    }

    protected SheetProcessingException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
