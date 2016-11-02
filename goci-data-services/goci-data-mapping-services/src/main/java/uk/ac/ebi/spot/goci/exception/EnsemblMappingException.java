package uk.ac.ebi.spot.goci.exception;

/**
 * Created by emma on 07/01/2016.
 *
 * @author emma
 *         <p>
 *         Custom exception applied when some component of mapping pipeline fails usually due API communication failure
 */
public class EnsemblMappingException extends Exception {

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
