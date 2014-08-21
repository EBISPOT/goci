package uk.ac.ebi.fgpt.goci.sparql.exception;

/**
 * Created by dwelter on 19/05/14.
 */
public class SparqlQueryException extends RuntimeException{
    public SparqlQueryException() {
        super();
    }

    public SparqlQueryException(String message) {
        super(message);
    }

    public SparqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SparqlQueryException(Throwable cause) {
        super(cause);
    }

    protected SparqlQueryException(String message,
                                   Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
