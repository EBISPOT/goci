package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 28/01/15.
 *
 * @author emma
 *         <p>
 *         DTO superclass that holds common association form information.
 */

public abstract class SnpAssociationForm {

    // Holds ID of association so we can create a link on form to edit the linked association
    private Long associationId;

    private String riskFrequency;

    private String pvalueDescription;


    @Min(value=1,message = "The Mantissa must be between [1..9]")
    @Max(value=9,message = "The Mantissa must be between [1..9]")
    private Integer pvalueMantissa;

    @Max(value=-6, message = "The exponent must < -5")
    private Integer pvalueExponent;

    private List<SnpMappingForm> snpMappingForms = new ArrayList<>();

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

    private String snpType;

    private Boolean snpApproved = false;

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

    // Constructors
    public SnpAssociationForm() {
    }

    public SnpAssociationForm(Long associationId,
                              String riskFrequency,
                              String pvalueDescription,
                              Integer pvalueMantissa,
                              Integer pvalueExponent,
                              List<SnpMappingForm> snpMappingForms,
                              Collection<EfoTrait> efoTraits,
                              Collection<GenomicContext> genomicContexts,
                              Collection<SingleNucleotidePolymorphism> snps,
                              String snpType,
                              Boolean snpApproved,
                              Float standardError,
                              String range,
                              String description,
                              Float orPerCopyNum,
                              Float orPerCopyRecip,
                              String orPerCopyRecipRange,
                              Float betaNum,
                              String betaUnit,
                              String betaDirection) {
        this.associationId = associationId;
        this.riskFrequency = riskFrequency;
        this.pvalueDescription = pvalueDescription;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.snpMappingForms = snpMappingForms;
        this.efoTraits = efoTraits;
        this.genomicContexts = genomicContexts;
        this.snps = snps;
        this.snpType = snpType;
        this.snpApproved = snpApproved;
        this.standardError = standardError;
        this.range = range;
        this.description = description;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.betaNum = betaNum;
        this.betaUnit = betaUnit;
        this.betaDirection = betaDirection;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    public void setBetaDirection(String betaDirection) {
        this.betaDirection = betaDirection;
    }

    public String getBetaUnit() {
        return betaUnit;
    }

    public void setBetaUnit(String betaUnit) {
        this.betaUnit = betaUnit;
    }

    public Float getBetaNum() {
        return betaNum;
    }

    public void setBetaNum(Float betaNum) {
        this.betaNum = betaNum;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Float getStandardError() {
        return standardError;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public Boolean getSnpApproved() {
        return snpApproved;
    }

    public void setSnpApproved(Boolean snpApproved) {
        this.snpApproved = snpApproved;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public List<SnpMappingForm> getSnpMappingForms() {
        return snpMappingForms;
    }

    public void setSnpMappingForms(List<SnpMappingForm> snpMappingForms) {
        this.snpMappingForms = snpMappingForms;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public String getPvalueDescription() {
        return pvalueDescription;
    }

    public void setPvalueDescription(String pvalueDescription) {
        this.pvalueDescription = pvalueDescription;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }
}

