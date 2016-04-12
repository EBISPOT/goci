package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Class that runs various combinations of error checks
 */
@Service
public class CheckService {

    /**
     * Run general checks on a row annotation
     *
     * @param row row to be checked
     */

    public Collection<AssociationValidationError> runAnnotationChecks(AssociationUploadRow row) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError snpTypeError = checkSnpType(row);
        if (snpTypeError.getError() != null) {
            associationValidationErrors.add(snpTypeError);
        }

        return checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run OR checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    public Collection<AssociationValidationError> runOrChecks(AssociationUploadRow row, String rowEffectType) {
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError orIsPresent = checkOrIsPresent(row, rowEffectType);
        associationValidationErrors.add(orIsPresent);

        AssociationValidationError betaFoundForOr = checkBetaValuesIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaFoundForOr);

        AssociationValidationError betaUnitFoundForOr = checkBetaUnitIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaUnitFoundForOr);

        AssociationValidationError betaDirectionFoundForOr = checkBetaDirectionIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaDirectionFoundForOr);

        return checkForValidErrors(associationValidationErrors);
    }


    /**
     * Run Beta checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    public Collection<AssociationValidationError> runBetaChecks(AssociationUploadRow row, String rowEffectType) {
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError betaIsPresent = checkBetaIsPresent(row, rowEffectType);
        associationValidationErrors.add(betaIsPresent);

        AssociationValidationError betaUnitNotFound = checkBetaUnitIsPresent(row, rowEffectType);
        associationValidationErrors.add(betaUnitNotFound);

        AssociationValidationError betaDirectionNotFound = checkBetaDirectionIsPresent(row, rowEffectType);
        associationValidationErrors.add(betaDirectionNotFound);

        AssociationValidationError orFound = checkOrEmpty(row, rowEffectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkOrRecipEmpty(row, rowEffectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound = checkOrPerCopyRecipRange(row, rowEffectType);
        associationValidationErrors.add(orRecipRangeFound);

        return checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param row           row to be checked
     * @param rowEffectType
     */
    public Collection<AssociationValidationError> runNoEffectErrors(AssociationUploadRow row, String rowEffectType) {
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError orFound = checkOrEmpty(row, rowEffectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkOrRecipEmpty(row, rowEffectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound = checkOrPerCopyRecipRange(row, rowEffectType);
        associationValidationErrors.add(orRecipRangeFound);

        AssociationValidationError betaFound = checkBetaValuesIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaFound);

        AssociationValidationError betaUnitFound = checkBetaUnitIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaUnitFound);

        AssociationValidationError betaDirectionFound = checkBetaDirectionIsEmpty(row, rowEffectType);
        associationValidationErrors.add(betaDirectionFound);

        AssociationValidationError rangeFound = checkRangeIsEmpty(row, rowEffectType);
        associationValidationErrors.add(rangeFound);

        AssociationValidationError standardErrorFound = checkStandardErrorIsEmpty(row, rowEffectType);
        associationValidationErrors.add(standardErrorFound);

        AssociationValidationError descriptionFound = checkDescriptionIsEmpty(row, rowEffectType);
        associationValidationErrors.add(descriptionFound);

        return checkForValidErrors(associationValidationErrors);
    }


    /**
     * Check snp type contains only values 'novel' or 'known'
     *
     * @param row row to be checked
     */
    private AssociationValidationError checkSnpType(AssociationUploadRow row) {
        AssociationValidationError error = new AssociationValidationError();

        if (row.getSnpType() != null) {
            switch (row.getSnpType()) {
                case "novel":
                    break;
                case "known":
                    break;
                default:
                    error.setRow(row.getRowNumber());
                    error.setColumnName("SNP type");
                    error.setError("SNP type does not contain novel or known");
            }
        }
        else {
            error.setRow(row.getRowNumber());
            error.setColumnName("SNP type");
            error.setError("SNP type is empty");
        }
        return error;
    }


    /**
     * "OR" MUST be filled.
     */
    private AssociationValidationError checkOrIsPresent(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
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
    private AssociationValidationError checkBetaValuesIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
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
    private AssociationValidationError checkBetaUnitIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();

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
    private AssociationValidationError checkBetaDirectionIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();

        if (row.getBetaDirection() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Direction");
            error.setError("Beta direction found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta" MUST be filled
     */
    private AssociationValidationError checkBetaIsPresent(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();

        if (row.getBetaNum() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta");
            error.setError("Beta is empty for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta unit" MUST be filled
     */
    private AssociationValidationError checkBetaUnitIsPresent(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();

        if (row.getBetaUnit() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Unit");
            error.setError("Beta unit is empty for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Beta direction" MUST be filled
     */
    private AssociationValidationError checkBetaDirectionIsPresent(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getBetaDirection() == null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Beta Direction");
            error.setError("Beta direction is empty for association with effect type: " + rowEffectType);
        }
        else {
            switch (row.getBetaDirection()) {
                case "increase":
                    break;
                case "decrease":
                    break;
                default:
                    error.setRow(row.getRowNumber());
                    error.setColumnName("Beta Direction");
                    error.setError("Beta direction is not increase or decrease for association with effect type: " +
                                           rowEffectType);
            }
        }
        return error;
    }

    /**
     * "OR" MUST be empty.
     */
    private AssociationValidationError checkOrEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
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
    private AssociationValidationError checkOrRecipEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getOrPerCopyRecip() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("OR reciprocal");
            error.setError("OR reciprocal found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "OR reciprocal range" MUST be empty.
     */
    private AssociationValidationError checkOrPerCopyRecipRange(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getOrPerCopyRecipRange() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("OR reciprocal range");
            error.setError("OR reciprocal range found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Range" MUST be empty.
     */
    private AssociationValidationError checkRangeIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getRange() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Range");
            error.setError("Range found for association with effect type: " + rowEffectType);
        }
        return error;
    }


    /**
     * "Standard Error" MUST be empty.
     */
    private AssociationValidationError checkStandardErrorIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getStandardError() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("Standard Error");
            error.setError("Standard error found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * "Description" MUST be empty.
     */
    private AssociationValidationError checkDescriptionIsEmpty(AssociationUploadRow row, String rowEffectType) {
        AssociationValidationError error = new AssociationValidationError();
        if (row.getDescription() != null) {
            error.setRow(row.getRowNumber());
            error.setColumnName("OR/Beta description");
            error.setError("OR/Beta description found for association with effect type: " + rowEffectType);
        }
        return error;
    }

    /**
     * Check error objects created to ensure we only return those with an actual message and location
     *
     * @param errors Errors to be checked
     * @return validErrors list of errors with message and location
     */
    private Collection<AssociationValidationError> checkForValidErrors(Collection<AssociationValidationError> errors) {
        Collection<AssociationValidationError> validErrors = new ArrayList<>();
        for (AssociationValidationError error : errors) {
            if (error.getError() != null) {
                validErrors.add(error);
            }
        }
        return validErrors;
    }


}
