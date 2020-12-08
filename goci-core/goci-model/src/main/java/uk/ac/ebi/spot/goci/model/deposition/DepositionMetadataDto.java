package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionMetadataDto {

    @JsonProperty("noStudies")
    private int noStudies;

    @JsonProperty("noAssociations")
    private int noAssociations;

    @JsonProperty("noSamples")
    private int noSamples;

    @JsonProperty("noNotes")
    private int noNotes;


}
