package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

/**
 * Created by emma on 24/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for BatchUploadRow
 */
public class BatchUploadRowBuilder {

    AssociationUploadRow associationUploadRow = new AssociationUploadRow();

    public BatchUploadRowBuilder setRowNumber(Integer rowNumber) {
        associationUploadRow.setRowNumber(rowNumber);
        return this;
    }

    public BatchUploadRowBuilder setEffectType(String effectType) {
        associationUploadRow.setEffectType(effectType);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyNum(Float orPerCopyNum) {
        associationUploadRow.setOrPerCopyNum(orPerCopyNum);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyRecip(Float orPerCopyRecip) {
        associationUploadRow.setOrPerCopyRecip(orPerCopyRecip);
        return this;
    }

    public BatchUploadRowBuilder setBetaNum(Float betaNum) {
        associationUploadRow.setBetaNum(betaNum);
        return this;
    }

    public BatchUploadRowBuilder setBetaUnit(String betaUnit) {
        associationUploadRow.setBetaUnit(betaUnit);
        return this;
    }

    public BatchUploadRowBuilder setBetaDirection(String betaDirection) {
        associationUploadRow.setBetaDirection(betaDirection);
        return this;
    }

    public BatchUploadRowBuilder setRange(String range) {
        associationUploadRow.setRange(range);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        associationUploadRow.setOrPerCopyRecipRange(orPerCopyRecipRange);
        return this;
    }

    public BatchUploadRowBuilder setStandardError(Float standardError) {
        associationUploadRow.setStandardError(standardError);
        return this;
    }

    public BatchUploadRowBuilder setDescription(String description) {
        associationUploadRow.setDescription(description);
        return this;
    }

    public BatchUploadRowBuilder setSnpType(String snpType){
        associationUploadRow.setSnpType(snpType);
        return this;
    }

    public AssociationUploadRow build() {
        return associationUploadRow;
    }
}