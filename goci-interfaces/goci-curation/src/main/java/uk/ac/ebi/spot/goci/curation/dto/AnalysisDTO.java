package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisDTO {

    @JsonProperty("user_term")
    private String userTerm;

    @JsonProperty("similar_term")
    private String similarTerm;

    @JsonProperty("degree")
    private Double degree;
}

