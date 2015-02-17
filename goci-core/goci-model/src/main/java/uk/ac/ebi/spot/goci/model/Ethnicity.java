package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;

/**
 * Created by emma on 28/11/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing ethnicity information attached to a study
 */

@Entity
public class Ethnicity {
    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private Integer numberOfIndividuals;

    private String ethnicGroup;

    private String countryOfOrigin;

    private String countryOfRecruitment;

    private String description;

    // In database but not available in curation interface forms as no longer used
    private String previouslyReported;

    private String sampleSizesMatch;

    private String notes;

    @OneToOne
    private Study study;

    // JPA no-args constructor
    public Ethnicity() {
    }

    public Ethnicity(String type, Integer numberOfIndividuals, String ethnicGroup, String countryOfOrigin, String countryOfRecruitment, String description, String previouslyReported, String sampleSizesMatch, String notes) {
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

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override public String toString() {
        return "Ethnicity{" +
                "id=" + id +
                ", numberOfIndividuals=" + numberOfIndividuals +
                ", ethnicGroup='" + ethnicGroup + '\'' +
                ", description='" + description + '\'' +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", countryOfRecruitment='" + countryOfRecruitment + '\'' +
                '}';
    }
}
