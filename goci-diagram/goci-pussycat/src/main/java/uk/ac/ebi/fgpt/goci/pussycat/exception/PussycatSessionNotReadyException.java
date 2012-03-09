package uk.ac.ebi.fgpt.goci.pussycat.exception;

/**
 * An exception that is thrown whenever a call to a {@link uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession} is made
 * on a session that is not yet ready to be used, due to an initialization delay.
 *
 * @author Tony Burdett
 * @date 09/03/12
 */
public class PussycatSessionNotReadyException extends Exception {
    public PussycatSessionNotReadyException() {
        super();
    }

    public PussycatSessionNotReadyException(String message) {
        super(message);
    }

    public PussycatSessionNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PussycatSessionNotReadyException(Throwable cause) {
        super(cause);
    }
}
