package uk.ac.ebi.spot.goci.curation.model.errors;

/**
 * Created by xinhe on 12/04/2017.
 */
public abstract class Error {

    protected String message;

    public Error() {
    }

    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
