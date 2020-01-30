package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class StudyExtension {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "study_id", unique = true)
    private Study study;

    private String statisticalModel;

    private String backgroundTrait;

    private String mappedBackgroundTrait;

    private String cohort;

    private String cohortSpecificReference;

    private String summaryStatisticsFile;

    private String summaryStatisticsAssembly;
}
