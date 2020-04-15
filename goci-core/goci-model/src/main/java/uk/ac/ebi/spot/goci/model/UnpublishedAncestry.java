package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class UnpublishedAncestry {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @JsonProperty("study_tag")
    private String studyTag;
    private String stage;
    private Integer sampleSize;
    private Integer cases;
    private Integer controls;
    @JsonProperty("sample_description")
    private String sampleDescription;
    @JsonProperty("ancestry_category")
    private String ancestryCategory;
    private String ancestry;
    @JsonProperty("ancestry_description")
    private String ancestryDescription;
    @JsonProperty("country_recruitment")
    private String countryRecruitment;

    @OneToOne
    @JoinColumn(name = "study_id", unique = true)
    private UnpublishedStudy study;

    public static UnpublishedAncestry create(DepositionSampleDto sampleDto){
        UnpublishedAncestry ancestry = new UnpublishedAncestry();
        ancestry.setStudyTag(sampleDto.getStudyTag());
        ancestry.setStage(sampleDto.getStage());
        ancestry.setSampleSize(sampleDto.getSize());
        ancestry.setCases(sampleDto.getCases());
        ancestry.setControls(sampleDto.getControls());
        ancestry.setSampleDescription(sampleDto.getSampleDescription());
        ancestry.setAncestryCategory(sampleDto.getAncestryCategory());
        ancestry.setAncestry(sampleDto.getAncestry());
        ancestry.setAncestryDescription(sampleDto.getAncestryDescription());
        ancestry.setCountryRecruitment(sampleDto.getCountryRecruitement());
        return ancestry;
    }
}