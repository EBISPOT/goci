package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionAssociationDto {
    @JsonProperty("study_tag")
    private String studyTag;
    @JsonProperty("variant_id")
    private String variantID;
    @JsonProperty("pvalue")
    private BigDecimal pValue;
    @JsonProperty("effect_allele")
    private String effectAllele;
    @JsonProperty("other_allele")
    private String otherAllele;
}
