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

    private String orType;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private Float orPerCopyStdError;

    private String associationType;

    private String multiSnpHaplotype;

    private String snpInteraction;

    private String snpApproved;

    private String snpStatuses;

    private Map<String, String> associationErrorMap = new HashMap<>();

    private String associationErrorsChecked;

    private String lastMappingPerformedBy;

    private String lastMappingDate;

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
                                   String orType,
                                   String orPerCopyRange,
                                   String orPerCopyRecipRange,
                                   String orPerCopyUnitDescr,
                                   Float orPerCopyStdError,
                                   String associationType,
                                   String multiSnpHaplotype,
                                   String snpInteraction,
                                   String snpApproved,
                                   String snpStatuses,
                                   Map<String, String> associationErrorMap,
                                   String associationErrorsChecked,
                                   String lastMappingPerfomredBy, String lastMappingDate) {
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
        this.orType = orType;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.orPerCopyStdError = orPerCopyStdError;
        this.associationType = associationType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.snpStatuses = snpStatuses;
        this.associationErrorMap = associationErrorMap;
        this.associationErrorsChecked = associationErrorsChecked;
        this.lastMappingPerformedBy = lastMappingPerfomredBy;
        this.lastMappingDate = lastMappingDate;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(String authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public String getStrongestRiskAlleles() {
        return strongestRiskAlleles;
    }

    public void setStrongestRiskAlleles(String strongestRiskAlleles) {
        this.strongestRiskAlleles = strongestRiskAlleles;
    }

    public String getSnps() {
        return snps;
    }

    public void setSnps(String snps) {
        this.snps = snps;
    }

    public String getProxySnps() {
        return proxySnps;
    }

    public void setProxySnps(String proxySnps) {
        this.proxySnps = proxySnps;
    }

    public String getAssociationRiskFrequency() {
        return associationRiskFrequency;
    }

    public void setAssociationRiskFrequency(String associationRiskFrequency) {
        this.associationRiskFrequency = associationRiskFrequency;
    }

    public String getRiskAlleleFrequencies() {
        return riskAlleleFrequencies;
    }

    public void setRiskAlleleFrequencies(String riskAlleleFrequencies) {
        this.riskAlleleFrequencies = riskAlleleFrequencies;
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

    public String getPvalueText() {
        return pvalueText;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public String getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(String efoTraits) {
        this.efoTraits = efoTraits;
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

    public String getOrType() {
        return orType;
    }

    public void setOrType(String orType) {
        this.orType = orType;
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

    public Float getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public void setOrPerCopyStdError(Float orPerCopyStdError) {
        this.orPerCopyStdError = orPerCopyStdError;
    }

    public String getAssociationType() {
        return associationType;
    }

    public void setAssociationType(String associationType) {
        this.associationType = associationType;
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

    public String getSnpApproved() {
        return snpApproved;
    }

    public void setSnpApproved(String snpApproved) {
        this.snpApproved = snpApproved;
    }

    public String getSnpStatuses() {
        return snpStatuses;
    }

    public void setSnpStatuses(String snpStatuses) {
        this.snpStatuses = snpStatuses;
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

    public String getLastMappingPerformedBy() {
        return lastMappingPerformedBy;
    }

    public void setLastMappingPerformedBy(String lastMappingPerformedBy) {
        this.lastMappingPerformedBy = lastMappingPerformedBy;
    }

    public String getLastMappingDate() {
        return lastMappingDate;
    }

    public void setLastMappingDate(String lastMappingDate) {
        this.lastMappingDate = lastMappingDate;
    }
}
