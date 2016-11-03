package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Object that represents a row in an upload spreadsheet containing association data
 *         Added listErrorCellType to manage the XLS formatting error.
 */
public class AssociationUploadRow {

    private Integer rowNumber;

    private String authorReportedGene;

    private String strongestAllele;

    private String otherAllele;

    private String snp;

    private String proxy;

    private String riskFrequency;

    // Equivalent to "Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls" in upload spreadsheet
    // Set to "NR' by default
    private String associationRiskFrequency;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private String pvalueDescription;

    private Float orPerCopyNum;

    private Float orPerCopyRecip;

    private Float betaNum;

    private String betaUnit;

    private String betaDirection;

    private String range;

    private String orPerCopyRecipRange;

    private Float standardError;

    private String description;

    private String multiSnpHaplotype = "N";

    private String snpInteraction ="N";

    private String snpStatus;

    private String snpType;

    private String efoTrait;

    private Collection<ValidationError> listErrorCellType = new ArrayList<>();

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getAuthorReportedGene() {
        return authorReportedGene;
    }

    public void setAuthorReportedGene(String authorReportedGene) {
        this.authorReportedGene = authorReportedGene;
    }

    public String getStrongestAllele() {
        return strongestAllele;
    }

    public void setStrongestAllele(String strongestAllele) {
        this.strongestAllele = strongestAllele;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public String getAssociationRiskFrequency() {
        return associationRiskFrequency;
    }

    public void setAssociationRiskFrequency(String associationRiskFrequency) {
        this.associationRiskFrequency = associationRiskFrequency;
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

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
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

    public Float getStandardError() {
        return standardError;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public void setMultiSnpHaplotype(String multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public String getSnpInteraction() {
        return snpInteraction;
    }

    public void setSnpInteraction(String snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public String getSnpStatus() {
        return snpStatus;
    }

    public void setSnpStatus(String snpStatus) {
        this.snpStatus = snpStatus;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public String getEfoTrait() {
        return efoTrait;
    }

    public void setEfoTrait(String efoTrait) {
        this.efoTrait = efoTrait;
    }

    public String getOtherAllele() {
        return otherAllele;
    }

    public void setOtherAllele(String otherAllele) {
        this.otherAllele = otherAllele;
    }

    public Collection<ValidationError> getListErrorCellType() { return this.listErrorCellType; }

    public void setListErrorCellType(Collection<ValidationError> listErrorCellType) {this.listErrorCellType = listErrorCellType; }

    public void addCellErrorType(ValidationError validationError) { this.listErrorCellType.add(validationError);}

}