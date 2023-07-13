package uk.ac.ebi.spot.goci.model.deposition.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "samplesList",
        "page"
})
public class DepositionSampleListWrapper {

    @JsonProperty(value = "_embedded")
    private DepositionSampleList samplesList;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("page")
    private DepositionPageInfo page;


}
