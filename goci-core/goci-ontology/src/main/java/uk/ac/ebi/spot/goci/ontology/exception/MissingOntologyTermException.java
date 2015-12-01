package uk.ac.ebi.spot.goci.ontology.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * Date 25/01/12
 */
public class MissingOntologyTermException extends OntologyTermException {
    public MissingOntologyTermException() {
    }

    public MissingOntologyTermException(String message) {
        super(message);
    }

    public MissingOntologyTermException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingOntologyTermException(Throwable cause) {
        super(cause);
    }
}
