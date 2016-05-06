package uk.ac.ebi.spot.goci.model;


import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         <p>
 *         Model of a GWAS study
 */

@Entity
public class Study implements Trackable {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Please enter an author")
    private String author;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Please enter a study date in format YYYY-MM-DD")
    private Date publicationDate;

    @NotBlank(message = "Please enter a publication")
    private String publication;

    @NotBlank(message = "Please enter a title")
    private String title;

    private String initialSampleSize;

    private String replicateSampleSize;

    @NotBlank(message = "Please enter a pubmed id")
    private String pubmedId;

    // Defaults set as false
    private Boolean cnv = false;

    private Boolean gxe = false;

    private Boolean gxg = false;

    private Integer snpCount;

    private String qualifier;

    private Boolean imputed = false;

    private Boolean pooled = false;

    private String studyDesignComment;


    @ManyToMany
    @JoinTable(name = "STUDY_PLATFORM",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "PLATFORM_ID"))
    private Collection<Platform> platforms;

    @OneToMany(mappedBy = "study")
    private Collection<Association> associations;

    @OneToMany(mappedBy = "study")
    private Collection<Ethnicity> ethnicities;

    @ManyToOne
    @JoinTable(name = "STUDY_DISEASE_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "DISEASE_TRAIT_ID"))
    private DiseaseTrait diseaseTrait;

    @ManyToMany
    @JoinTable(name = "STUDY_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits;

    @ManyToMany
    @JoinTable(name = "STUDY_SNP",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms;

    @OneToOne(orphanRemoval = true)
    private Housekeeping housekeeping;

    @OneToOne(mappedBy = "study", orphanRemoval = true)
    private StudyReport studyReport;

    @OneToMany
    @JoinTable(name = "STUDY_EVENT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

    // JPA no-args constructor
    public Study() {
    }

    public Study(String author,
                 Date publicationDate,
                 String publication,
                 String title,
                 String initialSampleSize,
                 String replicateSampleSize,
                 String pubmedId,
                 Boolean cnv,
                 Boolean gxe,
                 Boolean gxg,
                 Integer snpCount,
                 String qualifier,
                 Boolean imputed,
                 Boolean pooled,
                 String studyDesignComment,
                 Collection<Platform> platforms,
                 Collection<Association> associations,
                 Collection<Ethnicity> ethnicities,
                 DiseaseTrait diseaseTrait,
                 Collection<EfoTrait> efoTraits,
                 Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms,
                 Housekeeping housekeeping,
                 StudyReport studyReport,
                 Collection<Event> events) {
        this.author = author;
        this.publicationDate = publicationDate;
        this.publication = publication;
        this.title = title;
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.pubmedId = pubmedId;
        this.cnv = cnv;
        this.gxe = gxe;
        this.gxg = gxg;
        this.snpCount = snpCount;
        this.qualifier = qualifier;
        this.imputed = imputed;
        this.pooled = pooled;
        this.studyDesignComment = studyDesignComment;
        this.platforms = platforms;
        this.associations = associations;
        this.ethnicities = ethnicities;
        this.diseaseTrait = diseaseTrait;
        this.efoTraits = efoTraits;
        this.singleNucleotidePolymorphisms = singleNucleotidePolymorphisms;
        this.housekeeping = housekeeping;
        this.studyReport = studyReport;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInitialSampleSize() {
        return initialSampleSize;
    }

    public void setInitialSampleSize(String initialSampleSize) {
        this.initialSampleSize = initialSampleSize;
    }

    public String getReplicateSampleSize() {
        return replicateSampleSize;
    }

    public void setReplicateSampleSize(String replicateSampleSize) {
        this.replicateSampleSize = replicateSampleSize;
    }

    public Collection<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Collection<Platform> platforms) {
        this.platforms = platforms;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Boolean getCnv() {
        return cnv;
    }

    public void setCnv(Boolean cnv) {
        this.cnv = cnv;
    }

    public Boolean getGxe() {
        return gxe;
    }

    public void setGxe(Boolean gxe) {
        this.gxe = gxe;
    }

    public Boolean getGxg() {
        return gxg;
    }

    public void setGxg(Boolean gxg) {
        this.gxg = gxg;
    }

    public Collection<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(Collection<Association> associations) {
        this.associations = associations;
    }

    public DiseaseTrait getDiseaseTrait() {
        return diseaseTrait;
    }

    public void setDiseaseTrait(DiseaseTrait diseaseTrait) {
        this.diseaseTrait = diseaseTrait;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Collection<SingleNucleotidePolymorphism> getSingleNucleotidePolymorphisms() {
        return singleNucleotidePolymorphisms;
    }

    public void setSingleNucleotidePolymorphisms(Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms) {
        this.singleNucleotidePolymorphisms = singleNucleotidePolymorphisms;
    }

    public Housekeeping getHousekeeping() {
        return housekeeping;
    }

    public void setHousekeeping(Housekeeping housekeeping) {
        this.housekeeping = housekeeping;
    }

    public StudyReport getStudyReport() {
        return studyReport;
    }

    public void setStudyReport(StudyReport studyReport) {
        this.studyReport = studyReport;
    }

    public Collection<Ethnicity> getEthnicities() {
        return ethnicities;
    }

    public void setEthnicities(Collection<Ethnicity> ethnicities) {
        this.ethnicities = ethnicities;
    }


    public Integer getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(Integer snpCount) {
        this.snpCount = snpCount;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public boolean getImputed() {
        return imputed;
    }

    public void setImputed(boolean imputed) {
        this.imputed = imputed;
    }

    public boolean getPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public String getStudyDesignComment() {
        return studyDesignComment;
    }

    public void setStudyDesignComment(String studyDesignComment) {
        this.studyDesignComment = studyDesignComment;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    @Override public synchronized void addEvent(Event event) {
        Collection<Event> currentEvents = getEvents();
        currentEvents.add(event);
        setEvents((currentEvents));
    }
}
