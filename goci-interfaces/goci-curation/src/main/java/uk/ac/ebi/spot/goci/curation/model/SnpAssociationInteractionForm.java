package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.GenomicContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 12/02/15.
 *
 * @author emma
 *         <p>
 *         DTO object that represents form used to enter interaction association information
 */
public class SnpAssociationInteractionForm extends SnpAssociationForm {

    private List<SnpFormColumn> snpFormColumns = new ArrayList<>();

    private Integer numOfInteractions;

    // Constructors
    public SnpAssociationInteractionForm() {
    }

    public SnpAssociationInteractionForm(Long associationId,
                                         String riskFrequency,
                                         String pvalueDescription,
                                         Integer pvalueMantissa,
                                         Integer pvalueExponent,
                                         List<SnpMappingForm> snpMappingForms,
                                         Collection<EfoTrait> efoTraits,
                                         Collection<GenomicContext> genomicContexts,
                                         String snpType,
                                         Boolean snpApproved,
                                         Float standardError,
                                         String range,
                                         String description,
                                         Float orPerCopyNum,
                                         Float orPerCopyRecip,
                                         String orPerCopyRecipRange,
                                         Float betaNum,
                                         String betaUnit,
                                         String betaDirection,
                                         List<SnpFormColumn> snpFormColumns, Integer numOfInteractions) {
        super(associationId,
              riskFrequency,
              pvalueDescription,
              pvalueMantissa,
              pvalueExponent,
              snpMappingForms,
              efoTraits,
              genomicContexts,
              snpType,
              snpApproved,
              standardError,
              range,
              description,
              orPerCopyNum,
              orPerCopyRecip,
              orPerCopyRecipRange,
              betaNum,
              betaUnit,
              betaDirection);
        this.snpFormColumns = snpFormColumns;
        this.numOfInteractions = numOfInteractions;
    }

    public List<SnpFormColumn> getSnpFormColumns() {
        return snpFormColumns;
    }

    public void setSnpFormColumns(List<SnpFormColumn> snpFormColumns) {
        this.snpFormColumns = snpFormColumns;
    }

    public Integer getNumOfInteractions() {
        return numOfInteractions;
    }

    public void setNumOfInteractions(Integer numOfInteractions) {
        this.numOfInteractions = numOfInteractions;
    }
}
