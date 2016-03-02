package uk.ac.ebi.spot.goci.curation.builder;


import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 12/02/2016.
 *
 * @author emma
 *         <p>
 *         Association builder used in testing
 */
public class AssociationBuilder {

    private Association association = new Association();

    public AssociationBuilder setId(Long id) {
        association.setId(id);
        return this;
    }

    public AssociationBuilder setRiskFrequency(String riskFrequency) {
        association.setRiskFrequency(riskFrequency);
        return this;
    }

    public AssociationBuilder setPvalueText(String pvalueText) {
        association.setPvalueText(pvalueText);
        return this;
    }

    public AssociationBuilder setOrPerCopyNum(Float orPerCopyNum) {
        association.setOrPerCopyNum(orPerCopyNum);
        return this;
    }

    public AssociationBuilder setOrType(Boolean orType) {
        association.setOrType(orType);
        return this;
    }

    public AssociationBuilder setSnpType(String snpType) {
        association.setSnpType(snpType);
        return this;
    }

    public AssociationBuilder setMultiSnpHaplotype(Boolean multiSnpHaplotype) {
        association.setMultiSnpHaplotype(multiSnpHaplotype);
        return this;
    }

    public AssociationBuilder setSnpInteraction(Boolean snpInteraction) {
        association.setSnpInteraction(snpInteraction);
        return this;
    }

    public AssociationBuilder setSnpApproved(Boolean snpApproved) {
        association.setSnpApproved(snpApproved);
        return this;
    }

    public AssociationBuilder setPvalueMantissa(Integer pvalueMantissa) {
        association.setPvalueMantissa(pvalueMantissa);
        return this;
    }

    public AssociationBuilder setPvalueExponent(Integer pvalueExponent) {
        association.setPvalueExponent(pvalueExponent);
        return this;
    }

    public AssociationBuilder setOrPerCopyRecip(Float orPerCopyRecip) {
        association.setOrPerCopyRecip(orPerCopyRecip);
        return this;
    }

    public AssociationBuilder setStandardError(Float stdError) {
        association.setStandardError(stdError);
        return this;
    }

    public AssociationBuilder setRange(String range) {
        association.setRange(range);
        return this;
    }

    public AssociationBuilder setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        association.setOrPerCopyRecipRange(orPerCopyRecipRange);
        return this;
    }

    public AssociationBuilder setDescription(String description) {
        association.setDescription(description);
        return this;
    }

    public AssociationBuilder setStudy(Study study) {
        association.setStudy(study);
        return this;
    }

    public AssociationBuilder setLoci(Collection<Locus> loci) {
        association.setLoci(loci);
        return this;
    }

    public AssociationBuilder setEfoTraits(Collection<EfoTrait> efoTraits) {
        association.setEfoTraits(efoTraits);
        return this;
    }

    public AssociationBuilder setAssociationReport(AssociationReport associationReport) {
        association.setAssociationReport(associationReport);
        return this;
    }

    public AssociationBuilder setLastMappingDate(Date lastMappingDate) {
        association.setLastMappingDate(lastMappingDate);
        return this;
    }

    public AssociationBuilder setLastMappingPerformedBy(String lastMappingPerformedBy) {
        association.setLastMappingPerformedBy(lastMappingPerformedBy);
        return this;
    }

    public AssociationBuilder setLastUpdateDate(Date lastUpdateDate) {
        association.setLastUpdateDate(lastUpdateDate);
        return this;
    }

    public Association build() {
        return association;
    }
}