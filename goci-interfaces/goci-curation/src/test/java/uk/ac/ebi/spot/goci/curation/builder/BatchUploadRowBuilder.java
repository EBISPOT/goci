package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

/**
 * Created by emma on 24/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for BatchUploadRow
 */
public class BatchUploadRowBuilder {

    BatchUploadRow batchUploadRow = new BatchUploadRow();

    public BatchUploadRowBuilder setRowNumber(Integer rowNumber) {
        batchUploadRow.setRowNumber(rowNumber);
        return this;
    }

    public BatchUploadRowBuilder setEffectType(String effectType) {
        batchUploadRow.setEffectType(effectType);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyNum(Float orPerCopyNum) {
        batchUploadRow.setOrPerCopyNum(orPerCopyNum);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyRecip(Float orPerCopyRecip) {
        batchUploadRow.setOrPerCopyRecip(orPerCopyRecip);
        return this;
    }

    public BatchUploadRowBuilder setBetaNum(Float betaNum) {
        batchUploadRow.setBetaNum(betaNum);
        return this;
    }

    public BatchUploadRowBuilder setBetaUnit(String betaUnit) {
        batchUploadRow.setBetaUnit(betaUnit);
        return this;
    }

    public BatchUploadRowBuilder setBetaDirection(String betaDirection) {
        batchUploadRow.setBetaDirection(betaDirection);
        return this;
    }

    public BatchUploadRowBuilder setRange(String range) {
        batchUploadRow.setRange(range);
        return this;
    }

    public BatchUploadRowBuilder setOrPerCopyRecipRange(String orPerCopyRecipRange) {
        batchUploadRow.setOrPerCopyRecipRange(orPerCopyRecipRange);
        return this;
    }

    public BatchUploadRowBuilder setStandardError(Float standardError) {
        batchUploadRow.setStandardError(standardError);
        return this;
    }

    public BatchUploadRowBuilder setDescription(String description) {
        batchUploadRow.setDescription(description);
        return this;
    }

    public BatchUploadRowBuilder setSnpType(String snpType){
        batchUploadRow.setSnpType(snpType);
        return this;
    }

    public BatchUploadRow build() {
        return batchUploadRow;
    }
}