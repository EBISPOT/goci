package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.AncestralGroup;
import uk.ac.ebi.spot.goci.model.Country;

import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 24/04/2015.
 *
 * @author emma
 *         <p>
 *         DTO used to store study, ancestry and relevant housekeeyping information. Designed to allow curators ability
 *         to download a spreadsheet of the sample description, detailed ancestry and country information for all
 *         studies.
 */
public class StudySampleDescription {

    private Long studyId;

    private String author;

    private Date publicationDate;

    private String pubmedId;

    private String initialSampleSize;

    private String replicateSampleSize;

    private Boolean ancestryCheckedLevelOne = false;

    private Boolean ancestryCheckedLevelTwo = false;

    private String type;

    private Integer numberOfIndividuals;

    private Collection<AncestralGroup> ancestralGroups;

    private Collection<Country> countryOfOrigin;

    private Collection<Country> countryOfRecruitment;

    private String description;

    private String sampleSizesMatch;

    private String notes;

    public StudySampleDescription(Long studyId,
                                  String author,
                                  Date publicationDate,
                                  String pubmedId,
                                  String initialSampleSize,
                                  String replicateSampleSize,
                                  Boolean ancestryCheckedLevelOne,
                                  Boolean ancestryCheckedLevelTwo,
                                  String type,
                                  Integer numberOfIndividuals,
                                  Collection<AncestralGroup> ancestralGroups,
                                  Collection<Country> countryOfOrigin,
                                  Collection<Country> countryOfRecruitment,
                                  String description,
                                  String sampleSizesMatch,
                                  String notes) {
        this.studyId = studyId;
        this.author = author;
        this.publicationDate = publicationDate;
        this.pubmedId = pubmedId;
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.ancestryCheckedLevelOne = ancestryCheckedLevelOne;
        this.ancestryCheckedLevelTwo = ancestryCheckedLevelTwo;
        this.type = type;
        this.numberOfIndividuals = numberOfIndividuals;
        this.ancestralGroups = ancestralGroups;
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

    public Boolean isAncestryCheckedLevelOne() {
        return ancestryCheckedLevelOne;
    }

    public StudySampleDescription setAncestryCheckedLevelOne(Boolean ancestryCheckedLevelOne) {
        this.ancestryCheckedLevelOne = ancestryCheckedLevelOne;
        return this;
    }

    public Boolean isAncestryCheckedLevelTwo() {
        return ancestryCheckedLevelTwo;
    }

    public StudySampleDescription setAncestryCheckedLevelTwo(Boolean ancestryCheckedLevelTwo) {
        this.ancestryCheckedLevelTwo = ancestryCheckedLevelTwo;
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

    public Collection<AncestralGroup> getAncestralGroups() {
        return ancestralGroups;
    }

    public StudySampleDescription setAncestralGroups(Collection<AncestralGroup> ancestralGroups) {
        this.ancestralGroups = ancestralGroups;
        return this;
    }

    public Collection<Country> getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public StudySampleDescription setCountryOfOrigin(Collection<Country> countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
        return this;
    }

    public Collection<Country> getCountryOfRecruitment() {
        return countryOfRecruitment;
    }

    public StudySampleDescription setCountryOfRecruitment(Collection<Country> countryOfRecruitment) {
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
