package uk.ac.ebi.spot.goci.model.deposition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionStudyDto {
    @JsonProperty("study_tag")
    private String studyTag;

    @JsonProperty("study_accession")
    private String accession;

    @JsonProperty("genotyping_technology")
    private String genotypingTechnology;

    @JsonProperty("array_manufacturer")
    private String arrayManufacturer;

    @JsonProperty("array_information")
    private String arrayInformation;

    @JsonProperty("imputation")
    private Boolean imputation;

    @JsonProperty("variant_count")
    private Integer variantCount;

    @JsonProperty("sample_description")
    private String sampleDescription;

    @JsonProperty("statistical_model")
    private String statisticalModel;

    @JsonProperty("study_description")
    private String studyDescription;

    @JsonProperty("trait")
    private String trait;

    @JsonProperty("efo_trait")
    private String efoTrait;

    @JsonProperty("background_trait")
    private String backgroundTrait;

    @JsonProperty("background_efo_trait")
    private String backgroundEfoTrait;

    @JsonProperty("checksum")
    private String checksum;

    @JsonProperty("summary_statistics_file")
    private String summaryStatisticsFile;

    @JsonProperty("summary_statistics_assembly")
    private String summaryStatisticsAssembly;

    @JsonProperty("cohort")
    private String cohort;

    @JsonProperty("cohort_id")
    private String cohortId;

    @JsonProperty("agreedToCc0")
    private Boolean agreedToCc0;

    @JsonProperty("diseaseTrait")
    private DiseaseTraitDto diseaseTraitDto;

    @JsonProperty("efoTraits")
    private List<EFOTraitDTO> efoTraitDtos;

    @JsonProperty("sumstats_flag")
    private Boolean sumstatsFlag;

    @JsonProperty("pooled_flag")
    private Boolean pooledFlag;

    @JsonProperty("gxe_flag")
    private Boolean gxeFlag;


    public Study buildStudy(){
        Study study = new Study();
        study.setStudyTag(studyTag);
        study.setAccessionId(accession);
        study.setImputed(imputation);
        study.setAgreedToCc0(agreedToCc0);
        return study;
    }
}
