package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.model.EfoTrait;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 */
public class SNPAssociationForm {

    private String riskFrequency;

    private Float pvalueFloat;

    private String pvalueText;

    private Float orPerCopyNum;

    private String orType;

    private String snpType;

    private String multiSnpHaplotype;

    private String snpInteraction;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    private Collection<SnpFormRow> snpFormRows = new ArrayList<>();

    private Collection authorReportedGenes;

    private Collection<EfoTrait> efoTraits;

    public SNPAssociationForm() {
    }

    public SNPAssociationForm(String riskFrequency, Float pvalueFloat, String pvalueText, Float orPerCopyNum, String orType, String snpType, String multiSnpHaplotype, String snpInteraction, Integer pvalueMantissa, Integer pvalueExponent, Float orPerCopyRecip, Float orPerCopyStdError, String orPerCopyRange, String orPerCopyUnitDescr, Collection<SnpFormRow> snpFormRows, Collection authorReportedGenes, Collection<EfoTrait> efoTraits) {
        this.riskFrequency = riskFrequency;
        this.pvalueFloat = pvalueFloat;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.orType = orType;
        this.snpType = snpType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.snpFormRows = snpFormRows;
        this.authorReportedGenes = authorReportedGenes;
        this.efoTraits = efoTraits;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public Float getPvalueFloat() {
        return pvalueFloat;
    }

    public void setPvalueFloat(Float pvalueFloat) {
        this.pvalueFloat = pvalueFloat;
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

    public String getOrType() {
        return orType;
    }

    public void setOrType(String orType) {
        this.orType = orType;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
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

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public void setOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
    }

    public Collection<SnpFormRow> getSnpFormRows() {
        return snpFormRows;
    }

    public void setSnpFormRows(Collection<SnpFormRow> snpFormRows) {
        this.snpFormRows = snpFormRows;
    }

    public Collection getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }
}
