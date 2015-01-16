package uk.ac.ebi.spot.goci.service;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
public class SolrIndexingException extends RuntimeException {
    public SolrIndexingException() {
    }

    public SolrIndexingException(String message) {
        super(message);
    }

    public SolrIndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SolrIndexingException(Throwable cause) {
        super(cause);
    }

    public SolrIndexingException(String message,
                                 Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
