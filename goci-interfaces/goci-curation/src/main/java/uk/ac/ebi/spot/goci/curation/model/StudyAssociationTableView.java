package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 09/10/2015.
 * <p>
 *
 * @author emma
 *         <p>
 *         Object used to create a view of multi-snp or SNP interaction studies that is presented to user as a table via
 *         StudyContoller
 */
public class StudyAssociationTableView {

    private Long studyId;

    private String author;

    private Date publicationDate;

    private String publication;

    private String title;

    private String pubmedId;

    private String studyDiseaseTrait;

    private String studyEfoTrait;

    private Integer totalNumberOfAssociations;

    private Integer numberOfMultiSnpHaplotypeAssociations;

    private Integer numberOfSnpInteractiionAssociations;

    private String Notes;

    private String curator;

    private String curationStatus;

    private String associationEfoTraits;

    // Constructors
    public StudyAssociationTableView() {
    }

    public StudyAssociationTableView(Long studyId,
                                     String author,
                                     Date publicationDate,
                                     String publication,
                                     String title,
                                     String pubmedId,
                                     String studyDiseaseTrait,
                                     String studyEfoTrait,
                                     Integer totalNumberOfAssociations,
                                     Integer numberOfMultiSnpHaplotypeAssociations,
                                     Integer numberOfSnpInteractiionAssociations,
                                     String notes,
                                     String curator,
                                     String curationStatus,
                                     String associationEfoTraits) {
        this.studyId = studyId;
        this.author = author;
        this.publicationDate = publicationDate;
        this.publication = publication;
        this.title = title;
        this.pubmedId = pubmedId;
        this.studyDiseaseTrait = studyDiseaseTrait;
        this.studyEfoTrait = studyEfoTrait;
        this.totalNumberOfAssociations = totalNumberOfAssociations;
        this.numberOfMultiSnpHaplotypeAssociations = numberOfMultiSnpHaplotypeAssociations;
        this.numberOfSnpInteractiionAssociations = numberOfSnpInteractiionAssociations;
        Notes = notes;
        this.curator = curator;
        this.curationStatus = curationStatus;
        this.associationEfoTraits = associationEfoTraits;
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getStudyDiseaseTrait() {
        return studyDiseaseTrait;
    }

    public void setStudyDiseaseTrait(String studyDiseaseTrait) {
        this.studyDiseaseTrait = studyDiseaseTrait;
    }

    public String getStudyEfoTrait() {
        return studyEfoTrait;
    }

    public void setStudyEfoTrait(String studyEfoTrait) {
        this.studyEfoTrait = studyEfoTrait;
    }

    public Integer getTotalNumberOfAssociations() {
        return totalNumberOfAssociations;
    }

    public void setTotalNumberOfAssociations(Integer totalNumberOfAssociations) {
        this.totalNumberOfAssociations = totalNumberOfAssociations;
    }

    public Integer getNumberOfMultiSnpHaplotypeAssociations() {
        return numberOfMultiSnpHaplotypeAssociations;
    }

    public void setNumberOfMultiSnpHaplotypeAssociations(Integer numberOfMultiSnpHaplotypeAssociations) {
        this.numberOfMultiSnpHaplotypeAssociations = numberOfMultiSnpHaplotypeAssociations;
    }

    public Integer getNumberOfSnpInteractiionAssociations() {
        return numberOfSnpInteractiionAssociations;
    }

    public void setNumberOfSnpInteractiionAssociations(Integer numberOfSnpInteractiionAssociations) {
        this.numberOfSnpInteractiionAssociations = numberOfSnpInteractiionAssociations;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }

    public String getCurationStatus() {
        return curationStatus;
    }

    public void setCurationStatus(String curationStatus) {
        this.curationStatus = curationStatus;
    }

    public String getAssociationEfoTraits() {
        return associationEfoTraits;
    }

    public void setAssociationEfoTraits(String associationEfoTraits) {
        this.associationEfoTraits = associationEfoTraits;
    }
}
