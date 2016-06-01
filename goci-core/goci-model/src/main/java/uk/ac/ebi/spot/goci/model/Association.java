package uk.ac.ebi.spot.goci.model;

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

    private Boolean snpApproved = false;

    private String snpType;

    private Float standardError;

    private String range;

    private String description;

    // OR specific values
    private Float orPerCopyNum;

    private Float orPerCopyRecip;

    private String orPerCopyRecipRange;

    // Beta specific values
    private Float betaNum;

    private String betaUnit;

    private String betaDirection;

    @ManyToOne
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

    @OneToOne(mappedBy = "association", orphanRemoval = true)
    private AssociationReport associationReport;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    private String lastMappingPerformedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    @OneToMany
    @JoinTable(name = "ASSOCIATION_EVENT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

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
                       Date lastMappingDate,
                       String lastMappingPerformedBy,
                       Date lastUpdateDate, Collection<Event> events) {
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
        this.lastMappingDate = lastMappingDate;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastUpdateDate = lastUpdateDate;
        this.events = events;
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

    @Override public synchronized void addEvent(Event event) {
        Collection<Event> currentEvents = getEvents();
        currentEvents.add(event);
        setEvents((currentEvents));
    }
}
