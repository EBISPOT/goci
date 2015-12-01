package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 24/04/2015.
 *
 * @author emma
 *         <p>
 *         DTO used to store study, ethnicity and relevant housekeeyping information. Designed to allow curators ability to
 *         download a spreadsheet of the sample description, detailed ancestry and country information for all studies.
 */
public class StudySampleDescription {

    private Long studyId;

    private String author;

    private Date publicationDate;

    private String pubmedId;

    private String initialSampleSize;

    private String replicateSampleSize;

    private Boolean ethnicityCheckedLevelOne = false;

    private Boolean ethnicityCheckedLevelTwo = false;

    private String type;

    private Integer numberOfIndividuals;

    private String ethnicGroup;

    private String countryOfOrigin;

    private String countryOfRecruitment;

    private String description;

    private String sampleSizesMatch;

    private String notes;

    public StudySampleDescription(Long studyId, String author, Date publicationDate, String pubmedId, String initialSampleSize, String replicateSampleSize, Boolean ethnicityCheckedLevelOne, Boolean ethnicityCheckedLevelTwo, String type, Integer numberOfIndividuals, String ethnicGroup, String countryOfOrigin, String countryOfRecruitment, String description, String sampleSizesMatch, String notes) {
        this.studyId = studyId;
        this.author = author;
        this.publicationDate = publicationDate;
        this.pubmedId = pubmedId;
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
        this.type = type;
        this.numberOfIndividuals = numberOfIndividuals;
        this.ethnicGroup = ethnicGroup;
        this.countryOfOrigin = countryOfOrigin;
        this.countryOfRecruitment = countryOfRecruitment;
        this.description = description;
        this.sampleSizesMatch = sampleSizesMatch;
        this.notes = notes;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getAuthor() {
        return author;
    }

    public StudySampleDescription setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public StudySampleDescription setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public StudySampleDescription setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
        return this;
    }

    public String getInitialSampleSize() {
        return initialSampleSize;
    }

    public StudySampleDescription setInitialSampleSize(String initialSampleSize) {
        this.initialSampleSize = initialSampleSize;
        return this;
    }

    public String getReplicateSampleSize() {
        return replicateSampleSize;
    }

    public StudySampleDescription setReplicateSampleSize(String replicateSampleSize) {
        this.replicateSampleSize = replicateSampleSize;
        return this;
    }

    public Boolean isEthnicityCheckedLevelOne() {
        return ethnicityCheckedLevelOne;
    }

    public StudySampleDescription setEthnicityCheckedLevelOne(Boolean ethnicityCheckedLevelOne) {
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
        return this;
    }

    public Boolean isEthnicityCheckedLevelTwo() {
        return ethnicityCheckedLevelTwo;
    }

    public StudySampleDescription setEthnicityCheckedLevelTwo(Boolean ethnicityCheckedLevelTwo) {
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
        return this;
    }

    public String getType() {
        return type;
    }

    public StudySampleDescription setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public StudySampleDescription setNumberOfIndividuals(Integer numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
        return this;
    }

    public String getEthnicGroup() {
        return ethnicGroup;
    }

    public StudySampleDescription setEthnicGroup(String ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
        return this;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public StudySampleDescription setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
        return this;
    }

    public String getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public StudySampleDescription setCountryOfRecruitment(String countryOfRecruitment) {
        this.countryOfRecruitment = countryOfRecruitment;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public StudySampleDescription setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSampleSizesMatch() {
        return sampleSizesMatch;
    }

    public StudySampleDescription setSampleSizesMatch(String sampleSizesMatch) {
        this.sampleSizesMatch = sampleSizesMatch;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public StudySampleDescription setNotes(String notes) {
        this.notes = notes;
        return this;
    }
}
