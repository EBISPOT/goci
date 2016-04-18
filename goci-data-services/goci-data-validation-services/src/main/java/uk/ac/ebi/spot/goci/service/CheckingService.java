package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.utils.ErrorProcessingService;

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
     * Run p-value checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<AssociationValidationError> runPvalueChecks(Association association) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();
        associationValidationErrors.add(checkMantissaIsLessThan10(association));
        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
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

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
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

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
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

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
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

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
    }

    private AssociationValidationError checkSnpType(Association association) {
        String errorMessage = validationChecks.checkSnpType(association.getSnpType());
        return ErrorProcessingService.createError(errorMessage, "SNP type");
    }

    private AssociationValidationError checkOrIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrIsPresent(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    private AssociationValidationError checkBetaValuesIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaValueIsEmpty(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    private AssociationValidationError checkBetaUnitIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsEmpty(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    private AssociationValidationError checkBetaDirectionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsEmpty(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    private AssociationValidationError checkBetaIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaIsPresent(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    private AssociationValidationError checkBetaUnitIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsPresent(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    private AssociationValidationError checkBetaDirectionIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    private AssociationValidationError checkOrEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrEmpty(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    private AssociationValidationError checkOrRecipEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrRecipEmpty(association.getOrPerCopyRecip());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR reciprocal");
    }

    private AssociationValidationError checkOrPerCopyRecipRange(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrPerCopyRecipRangeIsEmpty(association.getOrPerCopyRecipRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR reciprocal range");
    }

    private AssociationValidationError checkRangeIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkRangeIsEmpty(association.getRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Range");
    }

    private AssociationValidationError checkStandardErrorIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkStandardErrorIsEmpty(association.getStandardError());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Standard Error");
    }

    private AssociationValidationError checkDescriptionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkDescriptionIsEmpty(association.getDescription());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR/Beta description");
    }

    private AssociationValidationError checkMantissaIsLessThan10(Association association) {
        String errorMessage = validationChecks.checkMantissaIsLessThan10(association.getPvalueMantissa());
        return ErrorProcessingService.createError(errorMessage, "P-value Mantissa");
    }
}