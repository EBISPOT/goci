package uk.ac.ebi.spot.goci.solr.indexer;

/**
 * Created by Dani on 27/11/2014.
 */
public class OntologyIndexingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OntologyIndexingException() {
    }

    public OntologyIndexingException(String message) {
        super(message);
    }

    public OntologyIndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyIndexingException(Throwable cause) {
        super(cause);
    }
}
