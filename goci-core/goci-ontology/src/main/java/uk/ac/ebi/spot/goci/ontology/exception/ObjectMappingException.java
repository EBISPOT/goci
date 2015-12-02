package uk.ac.ebi.spot.goci.ontology.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett Date 25/01/12
 */
public class ObjectMappingException extends RuntimeException {
    public ObjectMappingException() {
    }

    public ObjectMappingException(String message) {
        super(message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectMappingException(Throwable cause) {
        super(cause);
    }
}
