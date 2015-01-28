
package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by emma on 28/01/15.
 * @author emma
 *
 * New service class to deal with form used by curators to enter snp/association details
 */

public class SnpAssociationForm {

    private String riskFrequency;

    private String pvalueText;

    private Float orPerCopyNum;

    private Boolean orType = false;

    private String snpType;

    private Boolean multiSnpHaplotype = false;

    private Integer multiSnpHaplotypeNum;

    private Boolean snpInteraction = false;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    private Collection<SnpFormRow> snpFormRows = new ArrayList<>();

    private Collection<String> authorReportedGenes;

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    public SnpAssociationForm() {
    }

    public SnpAssociationForm(String riskFrequency, String pvalueText, Float orPerCopyNum, Boolean orType, String snpType, Boolean multiSnpHaplotype, Integer multiSnpHaplotypeNum, Boolean snpInteraction, Integer pvalueMantissa, Integer pvalueExponent, Float orPerCopyRecip, Float orPerCopyStdError, String orPerCopyRange, String orPerCopyUnitDescr, Collection<SnpFormRow> snpFormRows, Collection<String> authorReportedGenes, Collection<EfoTrait> efoTraits) {
        this.riskFrequency = riskFrequency;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.orType = orType;
        this.snpType = snpType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.multiSnpHaplotypeNum = multiSnpHaplotypeNum;
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
}

