package uk.ac.ebi.spot.goci.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class StudyExtension {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Study study;

    private String statisticalModel;

    private String backgroundTrait;

    private String mappedBackgroundTrait;

    private String cohort;

    private String cohortSpecificReference;

    private String summaryStatisticsFile;

    private String summaryStatisticsAssembly;
}
