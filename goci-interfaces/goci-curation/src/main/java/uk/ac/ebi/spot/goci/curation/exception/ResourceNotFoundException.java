package uk.ac.ebi.spot.goci.curation.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, Long id) {
        super(String.format("%s Id: %s not found",entity, id));
    }

    public ResourceNotFoundException(String entity, String param) {
        super(String.format("%s Id: %s not found",entity, param));
    }
}
