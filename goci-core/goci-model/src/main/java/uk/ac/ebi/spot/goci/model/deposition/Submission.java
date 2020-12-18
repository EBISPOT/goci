package uk.ac.ebi.spot.goci.model.deposition;

import lombok.Data;
import org.joda.time.LocalDate;

@Data
public class Submission {

    private String id;
    private String pubMedID;
    private String title;
    private String author;
    private String journal;
    private String status;
    private String curator;
    private String created;
    private String publicationStatus;
    private String doi;
    private LocalDate publicationDate;
    private String correspondingAuthor;

    private SubmissionType submissionType;
    private ImportStatus importStatus;

    public enum SubmissionType {
        METADATA("Metadata"),
        METADATA_AND_SUM_STATS("Metadata and Summary Stats"),
        SUM_STATS("Summary Stats"),
        METADATA_AND_TOP_ASSOCIATIONS("Metadata and Top Associations"),
        METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS("Metadata Summary Stats and Top Associations"),
        PRE_PUBLISHED("Pre-Publication"),
        UNKNOWN("Unknown");
        public final String label;

        private SubmissionType(String label) {
            this.label = label;
        }
    }

    public enum ImportStatus {
        READY("Ready"),
        NOT_READY("Not ready");

        public final String label;

        private ImportStatus(String label) {
            this.label = label;
        }
    }
}
