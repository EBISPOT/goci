package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 04/02/15.
 *
 * @author emma
 *         <p>
 *         Model class that takes text entered REPLICATE_SAMPLE_SIZE and passes it between controller and view
 */
public class ReplicationSampleDescription {

    private String replicationSampleDescription;

    public ReplicationSampleDescription() {
    }

    public ReplicationSampleDescription(String replicationSampleDescription) {
        this.replicationSampleDescription = replicationSampleDescription;
    }

    public String getReplicationSampleDescription() {
        return replicationSampleDescription;
    }

    public void setReplicationSampleDescription(String replicationSampleDescription) {
        this.replicationSampleDescription = replicationSampleDescription;
    }
}
