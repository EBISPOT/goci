package uk.ac.ebi.spot.goci.curation.service.deposition;

import java.util.UUID;

public class ImportLogStep {

    private String step;

    private String contextId;

    private String id;

    public ImportLogStep(String step, String contextId) {
        this.step = step;
        this.contextId = contextId;
        this.id = UUID.randomUUID().toString();
    }

    public String getStep() {
        return step;
    }

    public String getContextId() {
        return contextId;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "[" + step + " | " + contextId + "]";
    }
}
