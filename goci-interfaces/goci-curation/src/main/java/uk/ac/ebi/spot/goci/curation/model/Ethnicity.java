package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by emma on 28/11/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing ethnicity information attached to a study
 */

@Entity
@Table(name = "GWASETHNICITY")
public class Ethnicity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "GWASID")
    private String studyID;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "NUMINDIVIDUALS")
    private Integer numberOfIndividuals;

    @Column(name = "ETHNICGROUP")
    private String ethnicGroup;

    @Column(name = "COUNTRYORIGIN")
    private String countryOfOrigin;

    @Column(name = "COUNTRYRECRUITMENT")
    private String countryOfRecruitment;

    @Column(name = "ADDLDESCRIPTION")
    private String description;

    @Column(name = "PREVIOUSLYREPORTED")
    private String previouslyReported;

    @Column(name = "SAMPLESIZESMATCH")
    private String sampleSizesMatch;

    @Column(name = "NOTES")
    private String notes;

    // JPA no-args constructor
    public Ethnicity() {
    }

    public Ethnicity(String studyID, String type, Integer numberOfIndividuals, String ethnicGroup, String countryOfOrigin, String countryOfRecruitment, String description, String previouslyReported, String sampleSizesMatch, String notes) {
        this.studyID = studyID;
        this.type = type;
        this.numberOfIndividuals = numberOfIndividuals;
        this.ethnicGroup = ethnicGroup;
        this.countryOfOrigin = countryOfOrigin;
        this.countryOfRecruitment = countryOfRecruitment;
        this.description = description;
        this.previouslyReported = previouslyReported;
        this.sampleSizesMatch = sampleSizesMatch;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudyID() {
        return studyID;
    }

    public void setStudyID(String studyID) {
        this.studyID = studyID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public void setNumberOfIndividuals(Integer numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
    }

    public String getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(String ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public void setCountryOfRecruitment(String countryOfRecruitment) {
        this.countryOfRecruitment = countryOfRecruitment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviouslyReported() {
        return previouslyReported;
    }

    public void setPreviouslyReported(String previouslyReported) {
        this.previouslyReported = previouslyReported;
    }

    public String getSampleSizesMatch() {
        return sampleSizesMatch;
    }

    public void setSampleSizesMatch(String sampleSizesMatch) {
        this.sampleSizesMatch = sampleSizesMatch;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Ethnicity{" +
                "id=" + id +
                ", studyID='" + studyID + '\'' +
                ", type='" + type + '\'' +
                ", numberOfIndividuals=" + numberOfIndividuals +
                ", ethnicGroup='" + ethnicGroup + '\'' +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", countryOfRecruitment='" + countryOfRecruitment + '\'' +
                ", description='" + description + '\'' +
                ", previouslyReported='" + previouslyReported + '\'' +
                ", sampleSizesMatch='" + sampleSizesMatch + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
