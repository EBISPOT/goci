package uk.ac.ebi.spot.goci.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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

    private String initialSampleSize;

    @JsonProperty("replicationSampleSize")
    private String replicateSampleSize;

    // Defaults set as false
    @JsonIgnore
    private Boolean cnv = false;

    private Boolean gxe = false;

    private Boolean gxg = false;

//    private Boolean genomewideArray = true;
//
//    private Boolean targetedArray = false;

    private Integer snpCount;

    private String qualifier;

    private Boolean imputed = false;

    private Boolean pooled = false;

    private String studyDesignComment;

    private String accessionId;

    private Boolean fullPvalueSet = false;

    private Boolean userRequested = false;

    @JsonIgnore
    private Boolean openTargets = false;

    @ManyToMany
    @JoinTable(name = "STUDY_PLATFORM",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "PLATFORM_ID"))
    @JsonManagedReference
    private Collection<Platform> platforms;

    @OneToMany(mappedBy = "study")
    private Collection<Association> associations;

    @OneToMany(mappedBy = "study")
    @JsonManagedReference
    private Collection<Ancestry> ancestries;

    @ManyToOne
    @JoinTable(name = "STUDY_DISEASE_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "DISEASE_TRAIT_ID"))
    @JsonManagedReference
    private DiseaseTrait diseaseTrait;

    @ManyToMany
    @JoinTable(name = "STUDY_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    @JsonManagedReference
    private Collection<EfoTrait> efoTraits;

    @OneToOne(orphanRemoval = true)
    @JsonIgnore
    private Housekeeping housekeeping;

    @JsonIgnore
    @OneToOne(mappedBy = "study", orphanRemoval = true)
    private StudyReport studyReport;

    @OneToMany
    @JsonIgnore
    @JoinTable(name = "STUDY_EVENT",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    @OrderBy // important don't remove. Tracking ticket
    private Collection<Event> events = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "STUDY_GENOTYPING_TECHNOLOGY",
               joinColumns = @JoinColumn(name = "STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "GENOTYPING_TECHNOLOGY_ID"))
    @JsonManagedReference
    private Collection<GenotypingTechnology> genotypingTechnologies;

    @OneToMany(mappedBy = "study")
    private Collection<WeeklyTracking> weeklyTrackings;

    @OneToOne(cascade = {CascadeType.ALL})
    @JsonManagedReference("publicationInfo")
    @JoinColumn(name = "publication_id")
    private Publication publicationId;




    /**REST API fix: reversal of control of study-SNP relationship from study to SNP to fix deletion issues with respect to
     * the study-SNP view table. Works but not optimal, improve solution if possible**/
//    @ManyToMany
//    @JoinTable(name = "STUDY_SNP_VIEW",
//               joinColumns = @JoinColumn(name = "STUDY_ID"),
//               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    @ManyToMany(mappedBy = "studies")
    private Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

    //If you want just the notes linked with the study please check Association.java
    /**
     * @OneToMany(mappedBy = "study")
     * @JsonIgnore
     * Write the annotation this way will caused the study object contains ALL the notes, including studyNotes and
     * associationNote.However, we decide that the study onject should only include studyNotes, thus the blow
     * annotation was used instead.
     */
    @OneToMany
    @JoinColumn(name="genericId", referencedColumnName="id",insertable=false,updatable=false)
    @Where(clause="content_type='Study'")
    @JsonIgnore
    private Collection<StudyNote> notes = new ArrayList<>();

    // JPA no-args constructor
    public Study() {
    }

    public Study(String initialSampleSize,
                 String replicateSampleSize,
                 Boolean cnv,
                 Boolean gxe,
                 Boolean gxg,
//                 Boolean genomewideArray,
//                 Boolean targetedArray,
                 Integer snpCount,
                 String qualifier,
                 Boolean imputed,
                 Boolean pooled,
                 String studyDesignComment,
                 String accessionId,
                 Boolean fullPvalueSet,
                 Boolean userRequested,
                 Boolean openTargets,
                 Collection<Platform> platforms,
                 Collection<Association> associations,
                 Collection<Ancestry> ancestries,
                 DiseaseTrait diseaseTrait,
                 Collection<EfoTrait> efoTraits,
                 Housekeeping housekeeping,
                 StudyReport studyReport, Collection<Event> events,
                 Collection<SingleNucleotidePolymorphism> snps,
                 Collection<GenotypingTechnology> genotypingTechnologies) {
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.cnv = cnv;
        this.gxe = gxe;
        this.gxg = gxg;
//        this.genomewideArray = genomewideArray;
//        this.targetedArray = targetedArray;
        this.snpCount = snpCount;
        this.qualifier = qualifier;
        this.imputed = imputed;
        this.pooled = pooled;
        this.studyDesignComment = studyDesignComment;
        this.accessionId = accessionId;
        this.fullPvalueSet = fullPvalueSet;
        this.userRequested = userRequested;
        this.openTargets = openTargets;
        this.platforms = platforms;
        this.associations = associations;
        this.ancestries = ancestries;
        this.diseaseTrait = diseaseTrait;
        this.efoTraits = efoTraits;
        this.housekeeping = housekeeping;
        this.studyReport = studyReport;
        this.events = events;
        this.snps = snps;
        this.genotypingTechnologies = genotypingTechnologies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

//    public Boolean getTargetedArray() {
//        return targetedArray;
//    }

//    public void setTargetedArray(Boolean targetedArray) {
//        this.targetedArray = targetedArray;
//    }

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

    public Collection<Ancestry> getAncestries() {
        return ancestries;
    }

    public void setAncestries(Collection<Ancestry> ancestries) {
        this.ancestries = ancestries;
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

//    public Boolean getGenomewideArray() {
//        return genomewideArray;
//    }
//
//    public void setGenomewideArray(Boolean genomewideArray) {
//        this.genomewideArray = genomewideArray;
//    }

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

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public Boolean getFullPvalueSet() { return fullPvalueSet; }

    public void setFullPvalueSet(Boolean fullPvalueSet) {
        this.fullPvalueSet = fullPvalueSet;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }

    public Collection<StudyNote> getNotes() { return notes; }

    public void setNotes(Collection<StudyNote> notes) { this.notes = notes; }

    public void addNote(StudyNote note) {
        Collection<StudyNote> currentNotes = getNotes();
        currentNotes.add(note);
        setNotes((currentNotes));
    }

    @JsonIgnore
    public String getTagDuplicatedNote() {
        Collection<StudyNote> currentNotes = getNotes();
        for  (Note note:currentNotes) {
            if (note.getNoteSubject().getSubject().compareToIgnoreCase("Duplication TAG") == 0) {
                return note.getTextNote();
            }
        }
        return "";
    }

    public Collection<GenotypingTechnology> getGenotypingTechnologies() {
        return genotypingTechnologies;
    }

    public void setGenotypingTechnologies(Collection<GenotypingTechnology> genotypingTechnologies) {
        this.genotypingTechnologies = genotypingTechnologies;
    }

    public Boolean getUserRequested() {
        return userRequested;
    }

    public void setUserRequested(Boolean userRequested) {
        this.userRequested = userRequested;
    }

    public Boolean getOpenTargets() {
        return openTargets;
    }

    public void setOpenTargets(Boolean openTargets) {
        this.openTargets = openTargets;
    }

    @JsonProperty("publicationInfo")
    public Publication getPublicationId() { return publicationId; }

    @JsonProperty("publicationInfo")
    public void setPublicationId(Publication publicationId) { this.publicationId = publicationId; }
}
