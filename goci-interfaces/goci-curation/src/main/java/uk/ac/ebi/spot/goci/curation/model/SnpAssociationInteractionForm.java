package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // TODO DO WE NEED THIS IF VALUE WILL NEVER BE SET
    private Boolean multiSnpHaplotype = false;

    private Boolean snpInteraction = false;

    private Boolean snpChecked = false;

    private Boolean orType = false;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float pvalueFloat;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    private List<SnpFormColumn> snpFormColumn = new ArrayList<>();

    private Collection<EfoTrait> efoTraits = new ArrayList<>();

    private Integer numOfInteractions;


    // Constructors
    public SnpAssociationInteractionForm() {
    }


    public SnpAssociationInteractionForm(Long associationId, String pvalueText, Float orPerCopyNum, String snpType, Boolean multiSnpHaplotype, Boolean snpInteraction, Boolean snpChecked, Boolean orType, Integer pvalueMantissa, Integer pvalueExponent, Float pvalueFloat, Float orPerCopyRecip, Float orPerCopyStdError, String orPerCopyRange, String orPerCopyUnitDescr, List<SnpFormColumn> snpFormColumn, Collection<EfoTrait> efoTraits, Integer numOfInteractions) {
        this.associationId = associationId;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.snpType = snpType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.snpChecked = snpChecked;
        this.orType = orType;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.pvalueFloat = pvalueFloat;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.snpFormColumn = snpFormColumn;
        this.efoTraits = efoTraits;
        this.numOfInteractions = numOfInteractions;
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

    public Boolean getSnpChecked() {
        return snpChecked;
    }

    public void setSnpChecked(Boolean snpChecked) {
        this.snpChecked = snpChecked;
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

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public void setOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
    }

    public List<SnpFormColumn> getSnpFormColumn() {
        return snpFormColumn;
    }

    public void setSnpFormColumn(List<SnpFormColumn> snpFormColumn) {
        this.snpFormColumn = snpFormColumn;
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
}
