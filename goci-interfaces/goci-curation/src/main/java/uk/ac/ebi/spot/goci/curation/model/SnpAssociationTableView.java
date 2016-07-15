package uk.ac.ebi.spot.goci.curation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Model object that creates a view of a studies associations that can easily be rendered in a HTML table.
 */
public class SnpAssociationTableView {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String authorReportedGenes;

    private String strongestRiskAlleles;

    private String snps;

    private String proxySnps;

    // Two different frequencies,
    // one for overall association and
    // one for each risk allele
    private String associationRiskFrequency;

    private String riskAlleleFrequencies;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private String pvalueDescription;

    private String efoTraits;

    private Float orPerCopyNum;

    private Float orPerCopyRecip;

    private String orPerCopyRecipRange;

    private String range;

    private String description;

    private Float standardError;

    // Beta specific values
    private Float betaNum;

    private String betaUnit;

    private String betaDirection;

    private String associationType;

    private String multiSnpHaplotype;

    private String snpInteraction;

    private String snpApproved;

    private String snpStatuses;

    private Map<String, String> associationErrorMap = new HashMap<>();

    private String associationErrorsChecked;

    private String lastMappingPerformedBy;

    private String lastMappingDate;

    private Boolean validationWarnings;

    // Constructors
    public SnpAssociationTableView() {
    }

    public SnpAssociationTableView(Long associationId,
                                   String authorReportedGenes,
                                   String strongestRiskAlleles,
                                   String snps,
                                   String proxySnps,
                                   String associationRiskFrequency,
                                   String riskAlleleFrequencies,
                                   Integer pvalueMantissa,
                                   Integer pvalueExponent,
                                   String pvalueDescription,
                                   String efoTraits,
                                   Float orPerCopyNum,
                                   Float orPerCopyRecip,
                                   String orPerCopyRecipRange,
                                   String range,
                                   String description,
                                   Float standardError,
                                   Float betaNum,
                                   String betaUnit,
                                   String betaDirection,
                                   String associationType,
                                   String multiSnpHaplotype,
                                   String snpInteraction,
                                   String snpApproved,
                                   String snpStatuses,
                                   Map<String, String> associationErrorMap,
                                   String associationErrorsChecked,
                                   String lastMappingPerformedBy,
                                   String lastMappingDate,
                                   Boolean validationWarnings) {
        this.associationId = associationId;
        this.authorReportedGenes = authorReportedGenes;
        this.strongestRiskAlleles = strongestRiskAlleles;
        this.snps = snps;
        this.proxySnps = proxySnps;
        this.associationRiskFrequency = associationRiskFrequency;
        this.riskAlleleFrequencies = riskAlleleFrequencies;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.pvalueDescription = pvalueDescription;
        this.efoTraits = efoTraits;
        this.orPerCopyNum = orPerCopyNum;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.range = range;
        this.description = description;
        this.standardError = standardError;
        this.betaNum = betaNum;
        this.betaUnit = betaUnit;
        this.betaDirection = betaDirection;
        this.associationType = associationType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.snpStatuses = snpStatuses;
        this.associationErrorMap = associationErrorMap;
        this.associationErrorsChecked = associationErrorsChecked;
        this.lastMappingPerformedBy = lastMappingPerformedBy;
        this.lastMappingDate = lastMappingDate;
        this.validationWarnings = validationWarnings;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

    public String getAssociationErrorsChecked() {
        return associationErrorsChecked;
    }

    public void setAssociationErrorsChecked(String associationErrorsChecked) {
        this.associationErrorsChecked = associationErrorsChecked;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getAssociationRiskFrequency() {
        return associationRiskFrequency;
    }

    public void setAssociationRiskFrequency(String associationRiskFrequency) {
        this.associationRiskFrequency = associationRiskFrequency;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    public String getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(String authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    public void setBetaDirection(String betaDirection) {
        this.betaDirection = betaDirection;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(String efoTraits) {
        this.efoTraits = efoTraits;
    }

    public String getLastMappingDate() {
        return lastMappingDate;
    }

    public void setLastMappingDate(String lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public String getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public void setMultiSnpHaplotype(String multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
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

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public String getProxySnps() {
        return proxySnps;
    }

    public void setProxySnps(String proxySnps) {
        this.proxySnps = proxySnps;
    }

    public String getPvalueDescription() {
        return pvalueDescription;
    }

    public void setPvalueDescription(String pvalueDescription) {
        this.pvalueDescription = pvalueDescription;
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

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRiskAlleleFrequencies() {
        return riskAlleleFrequencies;
    }

    public void setRiskAlleleFrequencies(String riskAlleleFrequencies) {
        this.riskAlleleFrequencies = riskAlleleFrequencies;
    }

    public String getSnpApproved() {
        return snpApproved;
    }

    public void setSnpApproved(String snpApproved) {
        this.snpApproved = snpApproved;
    }

    public String getSnpInteraction() {
        return snpInteraction;
    }

    public void setSnpInteraction(String snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public String getSnps() {
        return snps;
    }

    public void setSnps(String snps) {
        this.snps = snps;
    }

    public String getSnpStatuses() {
        return snpStatuses;
    }

    public void setSnpStatuses(String snpStatuses) {
        this.snpStatuses = snpStatuses;
    }

    public Float getStandardError() {
        return standardError;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public String getStrongestRiskAlleles() {
        return strongestRiskAlleles;
    }

    public void setStrongestRiskAlleles(String strongestRiskAlleles) {
        this.strongestRiskAlleles = strongestRiskAlleles;
    }

    public Boolean getValidationWarnings() {
        return validationWarnings;
    }

    public void setValidationWarnings(Boolean validationWarnings) {
        this.validationWarnings = validationWarnings;
    }
}