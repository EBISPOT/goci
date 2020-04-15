package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import javax.persistence.*;
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
    private String accession;
    @JsonProperty("genotyping_technology")
        private String genotypingTechnology;
    @JsonProperty("array_manufacturer")
    private String arrayManufacturer;
//    private String arrayInformation;
//    private Boolean imputation;
//    private Integer variantCount;
//    private String sampleDescription;
    @JsonProperty("statistical_model")
    private String statisticalModel;
//    private String studyDescription;
    private String trait;
    //    private String efoTrait;
    @JsonProperty("background_trait")
    private String backgroundTrait;
    //    private String backgroundEfoTrait;
    private String checksum;
    @Column(name = "summary_stats_file")
    private String summaryStatisticsFile;
    @JsonProperty("submission_id")
    private String submissionId;
    @JsonProperty("globus_folder")
    private String globusFolder;
    //    private String summaryStatisticsAssembly;
//    private String cohort;
//    private String cohortId;
    private Date createdDate;

    @ManyToMany
    @JoinTable(name = "unpublished_study_to_work", joinColumns = @JoinColumn(name = "study_id"), inverseJoinColumns =
    @JoinColumn(name = "work_id"))
    @JsonManagedReference
    private Collection<BodyOfWork> bodiesOfWork;

    @OneToMany(mappedBy = "study", orphanRemoval = true)
    private Collection<UnpublishedAncestry> ancestries;

    public static UnpublishedStudy createFromStudy(DepositionStudyDto studyDto, DepositionSubmission submissionDto) {
        UnpublishedStudy study = new UnpublishedStudy();
        study.setAccession(studyDto.getAccession());
        study.setBackgroundTrait(studyDto.getBackgroundTrait());
        study.setChecksum(studyDto.getChecksum());
        study.setStudyTag(studyDto.getStudyTag());
        study.setSummaryStatisticsFile(studyDto.getSummaryStatisticsFile());
        study.setTrait(studyDto.getTrait());
        study.setSubmissionId(submissionDto.getSubmissionId());
        study.setGlobusFolder(submissionDto.getGlobusFolder());
        study.setCreatedDate(submissionDto.getCreated().getTimestamp().toDate());
        study.setStatisticalModel(studyDto.getStatisticalModel());
        study.setGenotypingTechnology(studyDto.getGenotypingTechnology());
        return study;
    }
}
