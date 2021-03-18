package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPatchRequest {

    @JsonProperty("GCST")
    private String gcst;

    @JsonProperty("Curated reported trait")
    private String curatedReportedTrait;

}

