package uk.ac.ebi.spot.goci.exception;

/**
 * A class representing an exception that occurred in starting, scheduling, or stopping dispatched queries
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public class DispatcherException extends Exception {
    public DispatcherException() {
        super();
    }

    public DispatcherException(String message) {
        super(message);
    }

    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatcherException(Throwable cause) {
        super(cause);
    }
}
