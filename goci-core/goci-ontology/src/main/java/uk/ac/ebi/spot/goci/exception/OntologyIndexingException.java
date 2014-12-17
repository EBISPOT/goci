package uk.ac.ebi.spot.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * Date 25/01/12
 */
public class OntologyIndexingException extends RuntimeException {
    public OntologyIndexingException() {
    }

    public OntologyIndexingException(String message) {
        super(message);
    }

    public OntologyIndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyIndexingException(Throwable cause) {
        super(cause);
    }
}
