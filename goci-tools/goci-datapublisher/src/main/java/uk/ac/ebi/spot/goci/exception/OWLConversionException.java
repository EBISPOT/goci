package uk.ac.ebi.spot.goci.exception;

/**
 * An exception thrown whenever converting model objects into OWL fails.
 *
 * @author Tony Burdett Date 24/01/12
 */
public class OWLConversionException extends Exception {
    public OWLConversionException() {
        super();
    }

    public OWLConversionException(String message) {
        super(message);
    }

    public OWLConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OWLConversionException(Throwable cause) {
        super(cause);
    }
}
