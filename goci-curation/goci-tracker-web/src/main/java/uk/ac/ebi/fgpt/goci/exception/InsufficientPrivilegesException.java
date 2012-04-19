package uk.ac.ebi.fgpt.goci.exception;

import uk.ac.ebi.fgpt.goci.model.GociUser;

/**
 * Checked exception that indicates a user did not have sufficient priviledges to perform the given action
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public class InsufficientPrivilegesException extends Exception {
    private GociUser underprivilegedUser;

    public InsufficientPrivilegesException(GociUser user) {
        super();
        this.underprivilegedUser = user;
    }

    public InsufficientPrivilegesException(GociUser user, String message) {
        super(message);
        this.underprivilegedUser = user;
    }

    public InsufficientPrivilegesException(GociUser user, String message, Throwable cause) {
        super(message, cause);
        this.underprivilegedUser = user;
    }

    public InsufficientPrivilegesException(GociUser user, Throwable cause) {
        super(cause);
        this.underprivilegedUser = user;
    }

    public GociUser getUnderprivilegedUser() {
        return underprivilegedUser;
    }
}
