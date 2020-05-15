package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import javax.persistence.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class UnpublishedStudy {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
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
    private String summaryStatsFile;

    @JsonProperty("submission_id")
    private String submissionId;

    @JsonProperty("globus_folder")
    private String globusFolder;

    @JsonProperty("summary_statistics_assembly")
    private String sumStatsAssembly;

    @JsonProperty("cohort")
    private String cohort;

    @JsonProperty("cohort_id")
    private String cohortId;

    private Date createdDate;

    @ManyToMany
    @JoinTable(name = "unpublished_study_to_work", joinColumns = @JoinColumn(name = "study_id"), inverseJoinColumns =
    @JoinColumn(name = "work_id"))
    @JsonManagedReference
    private Collection<BodyOfWork> bodiesOfWork;

    @OneToMany(mappedBy = "study", orphanRemoval = true)
    private Collection<UnpublishedAncestry> ancestries;

    public static UnpublishedStudy createFromStudy(DepositionStudyDto studyDto, DepositionSubmission submissionDto){
        UnpublishedStudy study = BeanMapper.MAPPER.convert(studyDto);
        study.setSubmissionId(submissionDto.getSubmissionId());
        study.setGlobusFolder(submissionDto.getGlobusFolder());
        study.setCreatedDate(submissionDto.getCreated().getTimestamp().toDate());
         return study;
    }
}
