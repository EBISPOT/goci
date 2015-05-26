package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 12/02/15.
 * @author emma
 *
 * Service class to deal with form used by curators to enter snp/association details for interaction studies
 */
public class SnpAssociationInteractionForm {

    // Holds ID of association so we can create a link on form to edit the
    // linked association
    private Long associationId;

    private String pvalueText;

    private Float orPerCopyNum;

    private String snpType;

    private Boolean snpChecked;

    private Boolean orType;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyRecipRange;

    private String orPerCopyUnitDescr;

    private List<SnpFormColumn> snpFormColumns = new ArrayList<>();

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Integer numOfInteractions;

    private Map<String, String> associationErrorMap = new HashMap<>();

    private String riskFrequency;


    // Constructors
    public SnpAssociationInteractionForm() {
    }

    public SnpAssociationInteractionForm(Long associationId,
                                         String pvalueText,
                                         Float orPerCopyNum,
                                         String snpType,
                                         Boolean snpChecked,
                                         Boolean orType,
                                         Integer pvalueMantissa,
                                         Integer pvalueExponent,
                                         Float orPerCopyRecip,
                                         Float orPerCopyStdError,
                                         String orPerCopyRange,
                                         String orPerCopyRecipRange,
                                         String orPerCopyUnitDescr,
                                         List<SnpFormColumn> snpFormColumns,
                                         Collection<EfoTrait> efoTraits,
                                         Integer numOfInteractions,
                                         Map<String, String> associationErrorMap, String riskFrequency) {
        this.associationId = associationId;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.snpType = snpType;
        this.snpChecked = snpChecked;
        this.orType = orType;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyRecipRange = orPerCopyRecipRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.snpFormColumns = snpFormColumns;
        this.efoTraits = efoTraits;
        this.numOfInteractions = numOfInteractions;
        this.associationErrorMap = associationErrorMap;
        this.riskFrequency = riskFrequency;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
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

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
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

    public List<SnpFormColumn> getSnpFormColumns() {
        return snpFormColumns;
    }

    public void setSnpFormColumns(List<SnpFormColumn> snpFormColumns) {
        this.snpFormColumns = snpFormColumns;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Integer getNumOfInteractions() {
        return numOfInteractions;
    }

    public void setNumOfInteractions(Integer numOfInteractions) {
        this.numOfInteractions = numOfInteractions;
    }

    public Boolean getSnpChecked() {
        return snpChecked;
    }

    public void setSnpChecked(Boolean snpChecked) {
        this.snpChecked = snpChecked;
    }

    public Boolean getOrType() {
        return orType;
    }

    public void setOrType(Boolean orType) {
        this.orType = orType;
    }

    public Map<String, String> getAssociationErrorMap() {
        return associationErrorMap;
    }

    public void setAssociationErrorMap(Map<String, String> associationErrorMap) {
        this.associationErrorMap = associationErrorMap;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }
}
