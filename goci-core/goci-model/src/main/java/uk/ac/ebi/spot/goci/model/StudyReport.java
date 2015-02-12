package uk.ac.ebi.spot.goci.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 12/02/15
 */
public class StudyReport {
    @Id
    @GeneratedValue
    private Long id;

    private Long pubmedIdError;

    private String ncbiPaperTitle;

    private String ncbiFirstAuthor;

    private String ncbiNormalisedFirstAuthor;

    private String ncbiFirstUpdateDate;

    @OneToOne
    private Study study;

    // JPA no-args constructor
    public StudyReport() {
    }

    public StudyReport(Long pubmedIdError,
                       String ncbiPaperTitle,
                       String ncbiFirstAuthor,
                       String ncbiNormalisedFirstAuthor, String ncbiFirstUpdateDate) {
        this.pubmedIdError = pubmedIdError;
        this.ncbiPaperTitle = ncbiPaperTitle;
        this.ncbiFirstAuthor = ncbiFirstAuthor;
        this.ncbiNormalisedFirstAuthor = ncbiNormalisedFirstAuthor;
        this.ncbiFirstUpdateDate = ncbiFirstUpdateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPubmedIdError() {
        return pubmedIdError;
    }

    public void setPubmedIdError(Long pubmedIdError) {
        this.pubmedIdError = pubmedIdError;
    }

    public String getNcbiPaperTitle() {
        return ncbiPaperTitle;
    }

    public void setNcbiPaperTitle(String ncbiPaperTitle) {
        this.ncbiPaperTitle = ncbiPaperTitle;
    }

    public String getNcbiFirstAuthor() {
        return ncbiFirstAuthor;
    }

    public void setNcbiFirstAuthor(String ncbiFirstAuthor) {
        this.ncbiFirstAuthor = ncbiFirstAuthor;
    }

    public String getNcbiNormalisedFirstAuthor() {
        return ncbiNormalisedFirstAuthor;
    }

    public void setNcbiNormalisedFirstAuthor(String ncbiNormalisedFirstAuthor) {
        this.ncbiNormalisedFirstAuthor = ncbiNormalisedFirstAuthor;
    }

    public String getNcbiFirstUpdateDate() {
        return ncbiFirstUpdateDate;
    }

    public void setNcbiFirstUpdateDate(String ncbiFirstUpdateDate) {
        this.ncbiFirstUpdateDate = ncbiFirstUpdateDate;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override public String toString() {
        return "StudyReport{" +
                "id=" + id +
                ", pubmedIdError=" + pubmedIdError +
                ", ncbiPaperTitle='" + ncbiPaperTitle + '\'' +
                ", ncbiFirstAuthor='" + ncbiFirstAuthor + '\'' +
                ", ncbiNormalisedFirstAuthor='" + ncbiNormalisedFirstAuthor + '\'' +
                ", ncbiFirstUpdateDate='" + ncbiFirstUpdateDate + '\'' +
                '}';
    }
}
