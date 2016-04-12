package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

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
public class CheckingService {

    private ValidationChecks validationChecks;

    @Autowired
    public CheckingService(ValidationChecks validationChecks) {
        this.validationChecks = validationChecks;
    }

    /**
     * Run general checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<AssociationValidationError> runAnnotationChecks(Association association) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError snpTypeError = checkSnpType(association);
        if (snpTypeError.getError() != null) {
            associationValidationErrors.add(snpTypeError);
        }

        return checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run OR checks on a row
     *
     * @param association association to be checked
     * @param effectType
     */
    public Collection<AssociationValidationError> runOrChecks(Association association, String effectType) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError orIsPresent = checkOrIsPresent(association, effectType);
        associationValidationErrors.add(orIsPresent);

        AssociationValidationError betaFoundForOr = checkBetaValuesIsEmpty(association, effectType);
        associationValidationErrors.add(betaFoundForOr);

        AssociationValidationError betaUnitFoundForOr = checkBetaUnitIsEmpty(association, effectType);
        associationValidationErrors.add(betaUnitFoundForOr);

        AssociationValidationError betaDirectionFoundForOr = checkBetaDirectionIsEmpty(association, effectType);
        associationValidationErrors.add(betaDirectionFoundForOr);

        return checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run Beta checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<AssociationValidationError> runBetaChecks(Association association, String effectType) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError betaIsPresent = checkBetaIsPresent(association, effectType);
        associationValidationErrors.add(betaIsPresent);

        AssociationValidationError betaUnitNotFound = checkBetaUnitIsPresent(association, effectType);
        associationValidationErrors.add(betaUnitNotFound);

        AssociationValidationError betaDirectionNotFound = checkBetaDirectionIsPresent(association, effectType);
        associationValidationErrors.add(betaDirectionNotFound);

        AssociationValidationError orFound = checkOrEmpty(association, effectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkOrRecipEmpty(association, effectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound = checkOrPerCopyRecipRange(association, effectType);
        associationValidationErrors.add(orRecipRangeFound);

        return checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<AssociationValidationError> runNoEffectErrors(Association association, String effectType) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError orFound = checkOrEmpty(association, effectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkOrRecipEmpty(association, effectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound = checkOrPerCopyRecipRange(association, effectType);
        associationValidationErrors.add(orRecipRangeFound);

        AssociationValidationError betaFound = checkBetaValuesIsEmpty(association, effectType);
        associationValidationErrors.add(betaFound);

        AssociationValidationError betaUnitFound = checkBetaUnitIsEmpty(association, effectType);
        associationValidationErrors.add(betaUnitFound);

        AssociationValidationError betaDirectionFound = checkBetaDirectionIsEmpty(association, effectType);
        associationValidationErrors.add(betaDirectionFound);

        AssociationValidationError rangeFound = checkRangeIsEmpty(association, effectType);
        associationValidationErrors.add(rangeFound);

        AssociationValidationError standardErrorFound = checkStandardErrorIsEmpty(association, effectType);
        associationValidationErrors.add(standardErrorFound);

        AssociationValidationError descriptionFound = checkDescriptionIsEmpty(association, effectType);
        associationValidationErrors.add(descriptionFound);

        return checkForValidErrors(associationValidationErrors);
    }

    private AssociationValidationError checkSnpType(Association association) {
        String errorMessage = validationChecks.checkSnpType(association.getSnpType());
        return createError(errorMessage, "SNP type");
    }

    private AssociationValidationError checkOrIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrIsPresent(association.getOrPerCopyNum());
        return createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    private AssociationValidationError checkBetaValuesIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaValueIsEmpty(association.getBetaNum());
        return createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    private AssociationValidationError checkBetaUnitIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsEmpty(association.getBetaUnit());
        return createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    private AssociationValidationError checkBetaDirectionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsEmpty(association.getBetaDirection());
        return createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    private AssociationValidationError checkBetaIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaIsPresent(association.getBetaNum());
        return createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    private AssociationValidationError checkBetaUnitIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsPresent(association.getBetaUnit());
        return createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    private AssociationValidationError checkBetaDirectionIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(association.getBetaDirection());
        return createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    private AssociationValidationError checkOrEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrEmpty(association.getOrPerCopyNum());
        return createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    private AssociationValidationError checkOrRecipEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrRecipEmpty(association.getOrPerCopyRecip());
        return createError(errorMessage + "with effect type: " + effectType, "OR reciprocal");
    }

    private AssociationValidationError checkOrPerCopyRecipRange(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrPerCopyRecipRangeIsEmpty(association.getOrPerCopyRecipRange());
        return createError(errorMessage + "with effect type: " + effectType,
                           "OR reciprocal range");
    }

    private AssociationValidationError checkRangeIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkRangeIsEmpty(association.getRange());
        return createError(errorMessage + "with effect type: " + effectType, "Range");
    }

    private AssociationValidationError checkStandardErrorIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkStandardErrorIsEmpty(association.getStandardError());
        return createError(errorMessage + "with effect type: " + effectType, "Standard Error");
    }

    private AssociationValidationError checkDescriptionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkDescriptionIsEmpty(association.getDescription());
        return createError(errorMessage + "with effect type: " + effectType,
                           "OR/Beta description");
    }

    /**
     * Create error object
     *
     * @param message       Error message
     * @param columnChecked Name of the column checked
     */
    private AssociationValidationError createError(String message, String columnChecked) {
        AssociationValidationError error = new AssociationValidationError();

        // If there is an error create a fully formed object
        if (message != null) {
            error.setColumnName(columnChecked);
            error.setError(message);
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
