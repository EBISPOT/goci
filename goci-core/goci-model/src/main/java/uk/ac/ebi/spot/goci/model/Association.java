package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an association
 */
@Entity
public class Association implements Trackable {
    @Id
    @GeneratedValue
    private Long id;

    private String riskFrequency;

    private String pvalueDescription;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Boolean multiSnpHaplotype = false;

    private Boolean snpInteraction = false;

    @JsonIgnore
    private Boolean snpApproved = false;

    private String snpType;

    private Float standardError;

    private String range;

    private String description;

    // OR specific values
    private Float orPerCopyNum;

    @JsonIgnore
    private Float orPerCopyRecip;

    @JsonIgnore
    private String orPerCopyRecipRange;

    // Beta specific values
    private Float betaNum;

    private String betaUnit;

    private String betaDirection;

    @OneToOne
    private Study study;

    // Association can have a number of loci attached depending on whether its a multi-snp haplotype
    // or SNP:SNP interaction
    @OneToMany
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "LOCUS_ID"))
    private Collection<Locus> loci = new ArrayList<>();

    // To avoid null values collections are by default initialized to an empty array list
    @ManyToMany
    @JoinTable(name = "ASSOCIATION_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "association", orphanRemoval = true)
    private AssociationReport associationReport;

    @JsonIgnore
    @OneToMany(mappedBy = "association", orphanRemoval = true)
    private Collection<AssociationValidationReport> associationValidationReports = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    @JsonIgnore
    private String lastMappingPerformedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    @JsonIgnore
    @OneToMany(fetch= FetchType.EAGER)
    @JoinTable(name = "ASSOCIATION_EVENT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="genericId", referencedColumnName="id",insertable=false,updatable=false)
    @Where(clause="content_type='Association'")
    @JsonIgnore
    private Collection<Note> notes;

    /**REST API fix: reversal of control of association-SNP and association-gene relationship from association to SNP/gene to fix deletion issues with respect to
     * the association-SNP/gene view table. Works but not optimal, improve solution if possible**/
//    @ManyToMany
//    @JoinTable(name = "ASSOCIATION_SNP_VIEW",
//               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
//               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    @ManyToMany(mappedBy = "associations")
    @JsonManagedReference
    private Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

//    @ManyToMany
//    @JoinTable(name = "ASSOCIATION_GENE_VIEW",
//               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
//               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
//    @ManyToMany(mappedBy = "associations")
//    @JsonManagedReference
//    private Collection<Gene> genes = new ArrayList<>();


    @PrePersist
    protected void onCreate() { lastUpdateDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { lastUpdateDate = new Date(); }

    // JPA no-args constructor
    public Association() {
    }

    public Association(String riskFrequency,
                       String pvalueDescription,
                       Integer pvalueMantissa,
                       Integer pvalueExponent,
                       Boolean multiSnpHaplotype,
                       Boolean snpInteraction,
                       Boolean snpApproved,
                       String snpType,
                       Float standardError,
                       String range,
                       String description,
                       Float orPerCopyNum,
                       Float orPerCopyRecip,
                       String orPerCopyRecipRange,
                       Float betaNum,
                       String betaUnit,
                       String betaDirection,
                       Study study,
                       Collection<Locus> loci,
                       Collection<EfoTrait> efoTraits,
                       AssociationReport associationReport,
                       Collection<AssociationValidationReport> associationValidationReport,
                       Date lastMappingDate,
                       String lastMappingPerformedBy,
                       Date lastUpdateDate,
                       Collection<Event> events,
                       Collection<SingleNucleotidePolymorphism> snps) {
        this.riskFrequency = riskFrequency;
        this.pvalueDescription = pvalueDescription;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.snpType = snpType;
        this.standardError = standardError;
        this.range = range;
        this.description = description;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.betaNum = betaNum;
        this.betaUnit = betaUnit;
        this.betaDirection = betaDirection;
        this.study = study;
        this.loci = loci;
        this.efoTraits = efoTraits;
        this.associationReport = associationReport;
        this.associationValidationReports = associationValidationReport;
        this.lastMappingDate = lastMappingDate;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastUpdateDate = lastUpdateDate;
        this.events = events;
        this.snps = snps;
//        this.genes = genes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public String getPvalueDescription() {
        return pvalueDescription;
    }

    public void setPvalueDescription(String pvalueDescription) {
        this.pvalueDescription = pvalueDescription;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public Boolean getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public void setMultiSnpHaplotype(Boolean multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public Boolean getSnpInteraction() {
        return snpInteraction;
    }

    public void setSnpInteraction(Boolean snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public Float getStandardError() {
        return standardError;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<Locus> getLoci() {
        return loci;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public void addEfoTrait(EfoTrait efoTrait) {
        efoTraits.add(efoTrait);
    }

    public Boolean getSnpApproved() {
        return snpApproved;
    }

    public void setSnpApproved(Boolean snpApproved) {
        this.snpApproved = snpApproved;
    }

    public AssociationReport getAssociationReport() {
        return associationReport;
    }

    public void setAssociationReport(AssociationReport associationReport) {
        this.associationReport = associationReport;
    }

    public Date getLastMappingDate() {
        return lastMappingDate;
    }

    public void setLastMappingDate(Date lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public double getPvalue() {
        return (pvalueMantissa * Math.pow(10, pvalueExponent));
    }

    public Float getBetaNum() {
        return betaNum;
    }

    public void setBetaNum(Float betaNum) {
        this.betaNum = betaNum;
    }

    public String getBetaUnit() {
        return betaUnit;
    }

    public void setBetaUnit(String betaUnit) {
        this.betaUnit = betaUnit;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    public void setBetaDirection(String betaDirection) {
        this.betaDirection = betaDirection;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public Collection<AssociationValidationReport> getAssociationValidationReports() {
        return associationValidationReports;
    }

    public void setAssociationValidationReports(Collection<AssociationValidationReport> associationValidationReports) {
        this.associationValidationReports = associationValidationReports;
    }

    @Override public synchronized void addEvent(Event event) {
        Collection<Event> currentEvents = getEvents();
        currentEvents.add(event);
        setEvents((currentEvents));
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps(){
        return snps;
    }

    public Collection<Note> getNotes() { return notes; }

    public void setNotes(Collection<Note> notes) { this.notes = notes; }
}
