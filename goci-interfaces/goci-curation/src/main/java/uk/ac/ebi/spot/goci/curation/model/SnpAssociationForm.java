package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by emma on 28/01/15.
 *
 * @author emma
 *         <p>
 *         New service class to deal with form used by curators to enter snp/association details
 */

public class SnpAssociationForm {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String riskFrequency;

    private String pvalueText;

    private Float orPerCopyNum;

    private Boolean orType = false;

    private String snpType;

    private Boolean multiSnpHaplotype = false;

    private Boolean snpInteraction = false;

    private Boolean snpApproved = false;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float pvalueFloat;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private List<SnpFormRow> snpFormRows = new ArrayList<>();

    private List<SnpMappingForm> snpMappingForms = new ArrayList<>();

    private Collection<String> authorReportedGenes;

    // These attributes store locus attributes
    private String multiSnpHaplotypeDescr;

    private Integer multiSnpHaplotypeNum;

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private Map<String, String> associationErrorMap = new HashMap<>();

    // Constructors
    public SnpAssociationForm() {
    }

    public SnpAssociationForm(Long associationId,
                              String riskFrequency,
                              String pvalueText,
                              Float orPerCopyNum,
                              Boolean orType,
                              String snpType,
                              Boolean multiSnpHaplotype,
                              Boolean snpInteraction,
                              Boolean snpApproved,
                              Boolean errorCheckedByCurator,
                              Integer pvalueMantissa,
                              Integer pvalueExponent,
                              Float pvalueFloat,
                              Float orPerCopyRecip,
                              Float orPerCopyStdError,
                              String orPerCopyRange,
                              String orPerCopyRecipRange,
                              String orPerCopyUnitDescr,
                              List<SnpFormRow> snpFormRows,
                              List<SnpMappingForm> snpMappingForms,
                              Collection<String> authorReportedGenes,
                              String multiSnpHaplotypeDescr,
                              Integer multiSnpHaplotypeNum,
                              Collection<EfoTrait> efoTraits,
                              Collection<GenomicContext> genomicContexts,
                              Map<String, String> associationErrorMap) {
        this.associationId = associationId;
        this.riskFrequency = riskFrequency;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.orType = orType;
        this.snpType = snpType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpApproved = snpApproved;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.pvalueFloat = pvalueFloat;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.snpFormRows = snpFormRows;
        this.snpMappingForms = snpMappingForms;
        this.authorReportedGenes = authorReportedGenes;
        this.multiSnpHaplotypeDescr = multiSnpHaplotypeDescr;
        this.multiSnpHaplotypeNum = multiSnpHaplotypeNum;
        this.efoTraits = efoTraits;
        this.genomicContexts = genomicContexts;
        this.associationErrorMap = associationErrorMap;
    }

    // Getters/setters
    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
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

    public String getMultiSnpHaplotypeDescr() {
        return multiSnpHaplotypeDescr;
    }

    public void setMultiSnpHaplotypeDescr(String multiSnpHaplotypeDescr) {
        this.multiSnpHaplotypeDescr = multiSnpHaplotypeDescr;
    }

    public Integer getMultiSnpHaplotypeNum() {
        return multiSnpHaplotypeNum;
    }

    public void setMultiSnpHaplotypeNum(Integer multiSnpHaplotypeNum) {
        this.multiSnpHaplotypeNum = multiSnpHaplotypeNum;
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

    public Float getPvalueFloat() {
        return pvalueFloat;
    }

    public void setPvalueFloat(Float pvalueFloat) {
        this.pvalueFloat = pvalueFloat;
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

    public List<SnpFormRow> getSnpFormRows() {
        return snpFormRows;
    }

    public void setSnpFormRows(List<SnpFormRow> snpFormRows) {
        this.snpFormRows = snpFormRows;
    }

    public List<SnpMappingForm> getSnpMappingForms() {
        return snpMappingForms;
    }

    public void setSnpMappingForms(List<SnpMappingForm> snpMappingForms) {
        this.snpMappingForms = snpMappingForms;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Boolean getSnpApproved() {
        return snpApproved;
    }

    public void setSnpApproved(Boolean snpApproved) {
        this.snpApproved = snpApproved;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

}

