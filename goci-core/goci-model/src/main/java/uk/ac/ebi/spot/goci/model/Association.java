package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
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
public class Association {
    @Id
    @GeneratedValue
    private Long id;

    private String riskFrequency;

    private String pvalueText;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Boolean multiSnpHaplotype = false;

    private Boolean snpInteraction = false;

    private Boolean snpApproved = false;

    private String snpType;

    // TODO REMOVE
    private Boolean orType = false;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    // OR specific values
    private Float orPerCopyNum;

    private Float orPerCopyRecip;

    private String orPerCopyRecipRange;

    // Beta specific values
    private Float betaNum;

    private String betaUnit;

    private String betaUnitDirection;

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

    @OneToOne(mappedBy = "association", cascade = CascadeType.REMOVE)
    private AssociationReport associationReport;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMappingDate;

    private String lastMappingPerformedBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    // JPA no-args constructor
    public Association() {
    }

    public Association(String riskFrequency,
                       String pvalueText,
                       Integer pvalueMantissa,
                       Integer pvalueExponent,
                       Boolean multiSnpHaplotype,
                       Boolean snpInteraction,
                       Boolean snpApproved,
                       String snpType,
                       Boolean orType,
                       Float orPerCopyStdError,
                       String orPerCopyRange,
                       String orPerCopyUnitDescr,
                       Float orPerCopyNum,
                       Float orPerCopyRecip,
                       String orPerCopyRecipRange,
                       Float betaNum,
                       String betaUnit,
                       String betaUnitDirection,
                       Study study,
                       Collection<Locus> loci,
                       Collection<EfoTrait> efoTraits,
                       AssociationReport associationReport,
                       Date lastMappingDate,
                       String lastMappingPerformedBy,
                       Date lastUpdateDate) {
        this.riskFrequency = riskFrequency;
        this.pvalueText = pvalueText;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.snpType = snpType;
        this.orType = orType;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.betaNum = betaNum;
        this.betaUnit = betaUnit;
        this.betaUnitDirection = betaUnitDirection;
        this.study = study;
        this.loci = loci;
        this.efoTraits = efoTraits;
        this.associationReport = associationReport;
        this.lastMappingDate = lastMappingDate;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastUpdateDate = lastUpdateDate;
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

    public String getPvalueText() {
        return pvalueText;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public Boolean getOrType() {
        return orType;
    }

    public void setOrType(Boolean orType) {
        this.orType = orType;
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

    public Float getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public void setOrPerCopyStdError(Float orPerCopyStdError) {
        this.orPerCopyStdError = orPerCopyStdError;
    }

    public String getOrPerCopyRange() {
        return orPerCopyRange;
    }

    public void setOrPerCopyRange(String orPerCopyRange) {
        this.orPerCopyRange = orPerCopyRange;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public void setOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
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

    public String getBetaUnitDirection() {
        return betaUnitDirection;
    }

    public void setBetaUnitDirection(String betaUnitDirection) {
        this.betaUnitDirection = betaUnitDirection;
    }
}
