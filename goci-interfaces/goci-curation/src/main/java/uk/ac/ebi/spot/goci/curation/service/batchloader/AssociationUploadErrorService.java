package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Error checking of uploaded spreadsheet
 */
@Service
public class AssociationUploadErrorService {

    /**
     * Check file rows for any errors
     *
     * @return errors, any errors encountered
     */
    public Collection<BatchUploadError> checkRowForErrors(Collection<BatchUploadRow> rows) {

        // Create collection to store all newly created associations
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        for (BatchUploadRow row : rows) {

            String rowEffectType = row.getEffectType();

            // Run checks depending on effect type
            if (rowEffectType.equals("OR")) {
                Collection<BatchUploadError> orErrors = runOrChecks(row, rowEffectType);
                if (!orErrors.isEmpty()) {
                    batchUploadErrors.addAll(orErrors);
                }
            }

            if (rowEffectType.equals("Beta")) {
                Collection<BatchUploadError> betaErrors = runBetaChecks(row, rowEffectType);
                if (!betaErrors.isEmpty()) {
                    batchUploadErrors.addAll(betaErrors);
                }
            }

            if (rowEffectType.equals("NR")) {
                Collection<BatchUploadError> noEffectErrors = runNoEffectErrors(row, rowEffectType);
                if (!noEffectErrors.isEmpty()) {
                    batchUploadErrors.addAll(noEffectErrors);
                }
            }
        }
        return batchUploadErrors;
    }

    /**
     * Run OR checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    private Collection<BatchUploadError> runOrChecks(BatchUploadRow row, String rowEffectType) {
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        BatchUploadError orIsPresent = checkOrIsPresent(row, rowEffectType);
        batchUploadErrors.add(orIsPresent);

        BatchUploadError betaFoundForOr = checkBetaValuesIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaFoundForOr);

        BatchUploadError betaUnitFoundForOr = checkBetaUnitIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaUnitFoundForOr);

        BatchUploadError betaDirectionFoundForOr = checkBetaDirectionIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaDirectionFoundForOr);

        return batchUploadErrors;
    }

    /**
     * Run Beta checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    private Collection<BatchUploadError> runBetaChecks(BatchUploadRow row, String rowEffectType) {
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        BatchUploadError betaUnitNotFound = checkBetaUnitIsPresent(row, rowEffectType);
        batchUploadErrors.add(betaUnitNotFound);

        BatchUploadError betaDirectionNotFound = checkBetaDirectionIsPresent(row, rowEffectType);
        batchUploadErrors.add(betaDirectionNotFound);

        BatchUploadError orFound = checkOrEmpty(row, rowEffectType);
        batchUploadErrors.add(orFound);

        BatchUploadError orRecipFound = checkOrRecipEmpty(row, rowEffectType);
        batchUploadErrors.add(orRecipFound);

        BatchUploadError orRecipRangeFound = checkOrPerCopyRecipRange(row, rowEffectType);
        batchUploadErrors.add(orRecipRangeFound);

        return batchUploadErrors;
    }

    /**
     * Run no effect checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    private Collection<BatchUploadError> runNoEffectErrors(BatchUploadRow row, String rowEffectType) {
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        BatchUploadError orFound = checkOrEmpty(row, rowEffectType);
        batchUploadErrors.add(orFound);

        BatchUploadError orRecipFound = checkOrRecipEmpty(row, rowEffectType);
        batchUploadErrors.add(orRecipFound);

        BatchUploadError orRecipRangeFound = checkOrPerCopyRecipRange(row, rowEffectType);
        batchUploadErrors.add(orRecipRangeFound);

        BatchUploadError betaFound = checkBetaValuesIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaFound);

        BatchUploadError betaUnitFound = checkBetaUnitIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaUnitFound);

        BatchUploadError betaDirectionFound = checkBetaDirectionIsEmpty(row, rowEffectType);
        batchUploadErrors.add(betaDirectionFound);

        return batchUploadErrors;
    }


    /**
     * "OR" MUST be filled.
     */
    private BatchUploadError checkOrIsPresent(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getOrPerCopyNum() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("OR");
            error.setError("OR num is empty for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta" columns MUST be empty
     */
    private BatchUploadError checkBetaValuesIsEmpty(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getBetaNum() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta");
            error.setError("Beta value found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta unit" columns MUST be empty
     */
    private BatchUploadError checkBetaUnitIsEmpty(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();

        if (row.getBetaUnit() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta unit");
            error.setError("Beta unit found for association with effect type: " + rowEffectType);
        }
        return error;

    }

    /**
     * "Beta direction" columns MUST be empty
     */
    private BatchUploadError checkBetaDirectionIsEmpty(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();

        if (row.getBetaDirection() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Direction");
            error.setError("Beta direction found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta unit" MUST be filled
     */
    private BatchUploadError checkBetaUnitIsPresent(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();

        if (row.getBetaNum() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Unit");
            error.setError("Beta unit is empty for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta direction" MUST be filled
     */
    private BatchUploadError checkBetaDirectionIsPresent(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getBetaDirection() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Direction");
            error.setError("Beta direction is empty for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "OR" MUST be empty.
     */
    private BatchUploadError checkOrEmpty(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getOrPerCopyNum() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("OR");
            error.setError("OR num found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Reciprocal OR" MUST be empty.
     */
    private BatchUploadError checkOrRecipEmpty(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getOrPerCopyRecip() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Reciprocal OR");
            error.setError("Reciprocal OR found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Reciprocal CI" MUST be empty.
     */
    private BatchUploadError checkOrPerCopyRecipRange(BatchUploadRow row, String rowEffectType) {
        BatchUploadError error = new BatchUploadError();
        if (row.getOrPerCopyRecipRange() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Reciprocal confidence interval");
            error.setError("Reciprocal confidence interval found for association with effect type: " + rowEffectType);
        }
        return error;
    }
}