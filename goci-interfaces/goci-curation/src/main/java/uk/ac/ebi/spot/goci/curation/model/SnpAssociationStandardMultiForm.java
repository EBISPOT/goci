package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 26/02/2016.
 *
 * @author emma
 *         <p>
 *         DTO object that represents form used to enter standard or multi-snp association information
 */
public class SnpAssociationStandardMultiForm extends SnpAssociationForm {

    private List<SnpFormRow> snpFormRows = new ArrayList<>();

    private Collection<String> authorReportedGenes;

    // These attributes store locus attributes
    private String multiSnpHaplotypeDescr;

    private Integer multiSnpHaplotypeNum;

    // Constructors
    public SnpAssociationStandardMultiForm() {
    }

    public SnpAssociationStandardMultiForm(Long associationId,
                                           String riskFrequency,
                                           String pvalueText,
                                           Integer pvalueMantissa,
                                           Integer pvalueExponent,
                                           List<SnpMappingForm> snpMappingForms,
                                           Collection<EfoTrait> efoTraits,
                                           Collection<GenomicContext> genomicContexts,
                                           String snpType,
                                           Boolean snpApproved,
                                           Boolean orType,
                                           Float standardError,
                                           String range,
                                           String description,
                                           Float orPerCopyNum,
                                           Float orPerCopyRecip,
                                           String orPerCopyRecipRange,
                                           Float betaNum,
                                           String betaUnit,
                                           String betaDirection,
                                           List<SnpFormRow> snpFormRows,
                                           Collection<String> authorReportedGenes,
                                           String multiSnpHaplotypeDescr,
                                           Integer multiSnpHaplotypeNum) {
        super(associationId,
              riskFrequency,
              pvalueText,
              pvalueMantissa,
              pvalueExponent,
              snpMappingForms,
              efoTraits,
              genomicContexts,
              snpType,
              snpApproved,
              orType,
              standardError,
              range,
              description,
              orPerCopyNum,
              orPerCopyRecip,
              orPerCopyRecipRange,
              betaNum,
              betaUnit,
              betaDirection);
        this.snpFormRows = snpFormRows;
        this.authorReportedGenes = authorReportedGenes;
        this.multiSnpHaplotypeDescr = multiSnpHaplotypeDescr;
        this.multiSnpHaplotypeNum = multiSnpHaplotypeNum;
    }

    public List<SnpFormRow> getSnpFormRows() {
        return snpFormRows;
    }

    public void setSnpFormRows(List<SnpFormRow> snpFormRows) {
        this.snpFormRows = snpFormRows;
    }

    public Collection<String> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<String> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
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
}
