package uk.ac.ebi.fgpt.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 25/01/12
 */
public class OntologyTermException extends ObjectMappingException {
    public OntologyTermException() {
    }

    public OntologyTermException(String message) {
        super(message);
    }

    public OntologyTermException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyTermException(Throwable cause) {
        super(cause);
    }
}
