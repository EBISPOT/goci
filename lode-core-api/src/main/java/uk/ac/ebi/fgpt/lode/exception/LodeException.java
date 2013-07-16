package uk.ac.ebi.fgpt.lode.exception;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class LodeException extends Exception {
    public LodeException() {
        super();
    }

    public LodeException(String message) {
        super(message);
    }

    public LodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LodeException(Throwable cause) {
        super(cause);
    }


}
