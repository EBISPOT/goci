package uk.ac.ebi.spot.goci.curation.exception;

/**
 * Created by emma on 19/01/15.
 *
 * @author emma A class representing an exception that occurred in import a pubmed id already found in database
 */
public class PubmedImportException extends RuntimeException {
    public PubmedImportException() {
    }

    public PubmedImportException(String message) {
        super(message);
    }

    public PubmedImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubmedImportException(Throwable cause) {
        super(cause);
    }

    public PubmedImportException(String message,
                                 Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
