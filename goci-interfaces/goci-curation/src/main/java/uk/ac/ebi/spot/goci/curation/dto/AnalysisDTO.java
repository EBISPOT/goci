package uk.ac.ebi.spot.goci.curation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "degree",
        "user_term",
        "similar_term"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisDTO {

    @JsonProperty("user_term")
    private String userTerm;

    @JsonProperty("similar_term")
    private String similarTerm;

    @JsonProperty("degree")
    private Double degree;
}

