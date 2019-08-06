package uk.ac.ebi.spot.goci.model;

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
public class DepositionAssociationDto {
    @JsonProperty("study_tag")
    private String studyTag;

    @JsonProperty("haplotype_id")
    private String haplotypeId;

    @JsonProperty("variant_id")
    private String variantId;

    private Double pvalue;

    @JsonProperty("pvalue_text")
    private String pvalueText;

    @JsonProperty("proxy_variant")
    private String proxyVariant;

    @JsonProperty("effect_allele")
    private String effectAllele;

    @JsonProperty("other_allele")
    private String otherAllele;

    @JsonProperty("effect_allele_frequency")
    private Double effectAlleleFrequency;

    @JsonProperty("odds_ratio")
    private Double oddsRatio;

    @JsonProperty("beta")
    private Double beta;

    @JsonProperty("beta_unit")
    private String betaUnit;

    @JsonProperty("ci_lower")
    private Double ciLower;

    @JsonProperty("ci_upper")
    private Double ciUpper;

    @JsonProperty("standard_error")
    private Double standardError;
}
