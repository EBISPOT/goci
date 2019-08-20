package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionSampleDto {
    @JsonProperty("study_tag")
    private String studyTag;
    private String stage;
    private Integer size;
    private Integer cases;
    private Integer controls;
    @JsonProperty("sample_description")
    private String sampleDescription;
    @JsonProperty("ancestry_category")
    private String ancestryCategory;
    private String ancestry;
    @JsonProperty("ancestry_description")
    private String ancestryDescription;
    @JsonProperty("country_recruitement")
    private String countryRecruitement;
}
