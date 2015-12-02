package uk.ac.ebi.spot.goci.pussycat.exception;

/**
 * Created by Dani on 06/11/2015.
 */
public class NoRenderableDataException extends Exception {

    public NoRenderableDataException() {
        super();
    }

    public NoRenderableDataException(String message) {
        super(message);
    }

    public NoRenderableDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRenderableDataException(Throwable cause) {
        super(cause);
    }

    protected NoRenderableDataException(String message,
                                        Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
