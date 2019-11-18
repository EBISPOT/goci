package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class Submission {

    private String id;
    private String pubMedID;
    private String title;
    private String author;
    private String status;
    private String curator;
    private String created;
    private String publicationStatus;

    private SubmissionType submissionType;
    public enum SubmissionType {METADATA, METADATA_AND_SUM_STATS, SUM_STATS, METADATA_AND_TOP_ASSOCIATIONS,
        METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS, UNKNOWN };

}
