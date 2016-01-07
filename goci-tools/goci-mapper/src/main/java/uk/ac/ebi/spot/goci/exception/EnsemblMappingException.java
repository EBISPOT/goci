package uk.ac.ebi.spot.goci.exception;

/**
 * Created by emma on 07/01/2016.
 *
 * @author emma
 *         <p>
 *         Exception that occurred in when some component of mapping pipeline fails usually due to teh sight being down
 */
public class EnsemblMappingException extends RuntimeException {

    public EnsemblMappingException() {
        super();
    }

    public EnsemblMappingException(String message) {
        super(message);
    }

    public EnsemblMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnsemblMappingException(Throwable cause) {
        super(cause);
    }

    protected EnsemblMappingException(String message,
                                      Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
