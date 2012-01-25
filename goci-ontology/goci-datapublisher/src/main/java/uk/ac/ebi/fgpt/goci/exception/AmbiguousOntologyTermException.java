package uk.ac.ebi.fgpt.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 25/01/12
 */
public class AmbiguousOntologyTermException extends OntologyTermException {
    public AmbiguousOntologyTermException() {
    }

    public AmbiguousOntologyTermException(String message) {
        super(message);
    }

    public AmbiguousOntologyTermException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousOntologyTermException(Throwable cause) {
        super(cause);
    }
}
