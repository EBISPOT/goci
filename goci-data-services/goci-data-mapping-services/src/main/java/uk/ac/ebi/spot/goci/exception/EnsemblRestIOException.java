package uk.ac.ebi.spot.goci.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 07/01/2016.
 *
 * @author emma
 *         <p>
 *         Custom exception applied when some component of mapping pipeline fails usually due API communication failure
 */
public class EnsemblRestIOException extends Exception {

    private List<String>restErrors = new ArrayList<>();

    public EnsemblRestIOException(String message) {
        super(message);
    }

    public EnsemblRestIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnsemblRestIOException(String message, List<String>restErrors){
        super(message);
        this.restErrors = restErrors;
    }

    public EnsemblRestIOException(String message, List<String>restErrors, Throwable cause){
        super(message,cause);
        this.restErrors = restErrors;
    }

    public List<String> getRestErrors() {
        return restErrors;
    }
}
