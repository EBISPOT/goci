package builder;

import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

/**
 * Created by emma on 24/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for BatchUploadRow
 */
public class AssociationUploadRowBuilder {

    AssociationUploadRow associationUploadRow = new AssociationUploadRow();

    public AssociationUploadRowBuilder setRowNumber(Integer rowNumber) {
        associationUploadRow.setRowNumber(rowNumber);
        return this;
    }

    public AssociationUploadRowBuilder setAuthorReportedGene(String genes) {
        associationUploadRow.setAuthorReportedGene(genes);
        return this;
    }

    public AssociationUploadRowBuilder setStrongestAllele(String alleles) {
        associationUploadRow.setStrongestAllele(alleles);
        return this;
    }

    public AssociationUploadRowBuilder setSnp(String snp) {
        associationUploadRow.setSnp(snp);
        return this;
    }

    public AssociationUploadRowBuilder setProxy(String proxy) {
        associationUploadRow.setProxy(proxy);
        return this;
    }

    public AssociationUploadRowBuilder setRiskFrequency(String riskFrequency) {
        associationUploadRow.setRiskFrequency(riskFrequency);
        return this;
    }

    public AssociationUploadRowBuilder setEffectType(String effectType) {
        associationUploadRow.setEffectType(effectType);
        return this;
    }

    public AssociationUploadRowBuilder setOrPerCopyNum(Float orPerCopyNum) {
        associationUploadRow.setOrPerCopyNum(orPerCopyNum);
        return this;
    }

    public AssociationUploadRowBuilder setOrPerCopyRecip(Float orPerCopyRecip) {
        associationUploadRow.setOrPerCopyRecip(orPerCopyRecip);
        return this;
    }

    public AssociationUploadRowBuilder setBetaNum(Float betaNum) {
        associationUploadRow.setBetaNum(betaNum);
        return this;
    }

    public AssociationUploadRowBuilder setBetaUnit(String betaUnit) {
        associationUploadRow.setBetaUnit(betaUnit);
        return this;
    }

    public AssociationUploadRowBuilder setBetaDirection(String betaDirection) {
        associationUploadRow.setBetaDirection(betaDirection);
        return this;
    }

    public AssociationUploadRowBuilder setRange(String range) {
        associationUploadRow.setRange(range);
        return this;
    }

    public AssociationUploadRowBuilder setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        associationUploadRow.setOrPerCopyRecipRange(orPerCopyRecipRange);
        return this;
    }

    public AssociationUploadRowBuilder setStandardError(Float standardError) {
        associationUploadRow.setStandardError(standardError);
        return this;
    }

    public AssociationUploadRowBuilder setDescription(String description) {
        associationUploadRow.setDescription(description);
        return this;
    }

    public AssociationUploadRowBuilder setSnpType(String snpType) {
        associationUploadRow.setSnpType(snpType);
        return this;
    }

    public AssociationUploadRow build() {
        return associationUploadRow;
    }
}