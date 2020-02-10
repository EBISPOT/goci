package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositionSummaryStatsDto {
    @JsonProperty("study_accession")
    private String studyAccession;

    @JsonProperty("trait")
    private String trait;

    @JsonProperty("sample_description")
    private String sampleDescription;

    @JsonProperty("hasSummaryStats")
    private Boolean hasSummaryStats;

    @JsonProperty("study_tag")
    private String studyTag;

}
