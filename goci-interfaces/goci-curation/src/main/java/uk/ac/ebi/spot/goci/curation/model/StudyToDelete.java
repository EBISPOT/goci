package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.Study;

import java.io.Serializable;

public class StudyToDelete implements Serializable {

    private final Study study;

    private final String numberOfSubmissions;

    public StudyToDelete(Study study, String numberOfSubmissions) {
        this.study = study;
        this.numberOfSubmissions = numberOfSubmissions;
    }

    public Study getStudy() {
        return study;
    }

    public String getNumberOfSubmissions() {
        return numberOfSubmissions;
    }
}
