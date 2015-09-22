package uk.ac.ebi.spot.goci.ontology.exception;

/**
 * Thrown to indicate that an assumption made about the structure or of an ontology was violated.
 * <p/>
 * This can apply, for example,  to axioms with an expected cardinality (e.g. exactly one label annotation), axioms or
 * properties that are expected to be present, and so on
 *
 * @author Tony Burdett
 * @date 09/08/12
 */
public class UnexpectedOntologyStructureException extends RuntimeException {
    public UnexpectedOntologyStructureException() {
    }

    public UnexpectedOntologyStructureException(String message) {
        super(message);
    }

    public UnexpectedOntologyStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedOntologyStructureException(Throwable cause) {
        super(cause);
    }
}
