package uk.ac.ebi.fgpt.goci.exception;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * Date 12/12/11
 */
public class UserCreationException extends Exception {
    public UserCreationException() {
    }

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCreationException(Throwable cause) {
        super(cause);
    }

}
