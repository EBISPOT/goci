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

    // Two different frequencies, one for overall association and
    // one for each risk allele
    private String associationRiskFrequency;

    private String riskAlleleFrequencies;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private String pvalueText;

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

    private String syntaxErrorsFound;

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
                                   String pvalueText,
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
                                   String syntaxErrorsFound) {
        this.associationId = associationId;
        this.authorReportedGenes = authorReportedGenes;
        this.strongestRiskAlleles = strongestRiskAlleles;
        this.snps = snps;
        this.proxySnps = proxySnps;
        this.associationRiskFrequency = associationRiskFrequency;
        this.riskAlleleFrequencies = riskAlleleFrequencies;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.pvalueText = pvalueText;
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
        this.syntaxErrorsFound = syntaxErrorsFound;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public void setAuthorReportedGenes(String authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public void setStrongestRiskAlleles(String strongestRiskAlleles) {
        this.strongestRiskAlleles = strongestRiskAlleles;
    }

    public void setSnps(String snps) {
        this.snps = snps;
    }

    public void setProxySnps(String proxySnps) {
        this.proxySnps = proxySnps;
    }

    public void setAssociationRiskFrequency(String associationRiskFrequency) {
        this.associationRiskFrequency = associationRiskFrequency;
    }

    public void setRiskAlleleFrequencies(String riskAlleleFrequencies) {
        this.riskAlleleFrequencies = riskAlleleFrequencies;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public void setEfoTraits(String efoTraits) {
        this.efoTraits = efoTraits;
    }

    public void setOrPerCopyNum(Float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public void setOrPerCopyRecip(Float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public void setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        this.orPerCopyRecipRange = orPerCopyRecipRange;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStandardError(Float standardError) {
        this.standardError = standardError;
    }

    public void setBetaNum(Float betaNum) {
        this.betaNum = betaNum;
    }

    public void setBetaUnit(String betaUnit) {
        this.betaUnit = betaUnit;
    }

    public void setBetaDirection(String betaDirection) {
        this.betaDirection = betaDirection;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    public void setMultiSnpHaplotype(String multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public void setSnpInteraction(String snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public void setSnpApproved(String snpApproved) {
        this.snpApproved = snpApproved;
    }

    public void setSnpStatuses(String snpStatuses) {
        this.snpStatuses = snpStatuses;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

    public void setAssociationErrorsChecked(String associationErrorsChecked) {
        this.associationErrorsChecked = associationErrorsChecked;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public void setLastMappingDate(String lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }

    public void setSyntaxErrorsFound(String syntaxErrorsFound) {
        this.syntaxErrorsFound = syntaxErrorsFound;
    }


    public Long getAssociationId() {
        return associationId;
    }

    public String getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public String getStrongestRiskAlleles() {
        return strongestRiskAlleles;
    }

    public String getSnps() {
        return snps;
    }

    public String getProxySnps() {
        return proxySnps;
    }

    public String getAssociationRiskFrequency() {
        return associationRiskFrequency;
    }

    public String getRiskAlleleFrequencies() {
        return riskAlleleFrequencies;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public String getPvalueText() {
        return pvalueText;
    }

    public String getEfoTraits() {
        return efoTraits;
    }

    public Float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public Float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public String getOrPerCopyRecipRange() {
        return orPerCopyRecipRange;
    }

    public String getRange() {
        return range;
    }

    public String getDescription() {
        return description;
    }

    public Float getStandardError() {
        return standardError;
    }

    public Float getBetaNum() {
        return betaNum;
    }

    public String getBetaUnit() {
        return betaUnit;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    public String getAssociationType() {
        return associationType;
    }

    public String getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public String getSnpInteraction() {
        return snpInteraction;
    }

    public String getSnpApproved() {
        return snpApproved;
    }

    public String getSnpStatuses() {
        return snpStatuses;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public String getAssociationErrorsChecked() {
        return associationErrorsChecked;
    }

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public String getLastMappingDate() {
        return lastMappingDate;
    }

    public String getSyntaxErrorsFound() {
        return syntaxErrorsFound;
    }
}
