package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionSubmission {
    @NotEmpty
    @JsonProperty("submissionId")
    private String submissionId;

    @JsonProperty("publication")
    private DepositionPublication publication;

    @JsonProperty("bodyOfWork")
    private BodyOfWorkDto bodyOfWork;

    @NotEmpty
    @JsonProperty("status")
    private String status;

    @JsonProperty("studies")
    private List<DepositionStudyDto> studies;

    @JsonProperty("samples")
    private List<DepositionSampleDto> samples;

    @JsonProperty("associations")
    private List<DepositionAssociationDto> associations;

    @JsonProperty("notes")
    private List<DepositionNoteDto> notes;

    @JsonProperty("globusFolder")
    private String globusFolder;

    @JsonProperty("globusOriginId")
    private String globusOriginId;

    @JsonProperty("provenanceType")
    private String provenanceType;

    @JsonProperty("date_submitted")
    private LocalDate dateSubmitted;

    @JsonProperty("created")
    private DepositionProvenance created;

    @JsonProperty("agreedToCc0")
    private Boolean agreedToCc0;

    @JsonProperty("opentargets_flag")
    private Boolean opentargetsFlag;

    @JsonProperty("userrequested_flag")
    private Boolean userrequestedFlag;
}
