package uk.ac.ebi.spot.goci.curation.exception;

/**
 * Created by emma on 15/04/2016.
 *
 * @author emma
 *         <p>
 *         Exception used when creating a directory for study files fails
 */
public class NoStudyDirectoryException extends RuntimeException {
    public NoStudyDirectoryException() {
        super();
    }

    public NoStudyDirectoryException(String message) {
        super(message);
    }

    public NoStudyDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoStudyDirectoryException(Throwable cause) {
        super(cause);
    }

    protected NoStudyDirectoryException(String message,
                                        Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
