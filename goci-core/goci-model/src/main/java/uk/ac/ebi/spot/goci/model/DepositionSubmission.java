package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionSubmission {
    private String submissionId;
    private DepositionPublication publication;
    @JsonProperty("submission_status")
    private String status;
    private DepositionProvenance created;
    private List<DepositionFileUploadDto> files;
    private List<DepositionStudyDto> studies;
    private List<DepositionSampleDto> samples;
    private List<DepositionAssociationDto> associations;
    private List<DepositionNoteDto> notes;
}
