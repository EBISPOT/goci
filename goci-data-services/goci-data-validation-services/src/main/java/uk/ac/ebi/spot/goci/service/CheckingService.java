package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.utils.ErrorProcessingService;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Runs checks and creates error messages that can then by returned to client
 */
@Service
public class CheckingService {

    private ValidationChecks validationChecks;

    @Autowired
    public CheckingService(ValidationChecks validationChecks) {
        this.validationChecks = validationChecks;
    }

    public AssociationValidationError checkSnpType(Association association) {
        String errorMessage = validationChecks.checkSnpType(association.getSnpType());
        return ErrorProcessingService.createError(errorMessage, "SNP type");
    }

    public AssociationValidationError checkOrIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrIsPresent(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    public AssociationValidationError checkBetaValuesIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaValueIsEmpty(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    public AssociationValidationError checkBetaUnitIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsEmpty(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    public AssociationValidationError checkBetaDirectionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsEmpty(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    public AssociationValidationError checkBetaIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaIsPresent(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    public AssociationValidationError checkBetaUnitIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsPresent(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    public AssociationValidationError checkBetaDirectionIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    public AssociationValidationError checkOrEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrEmpty(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    public AssociationValidationError checkOrRecipEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrRecipEmpty(association.getOrPerCopyRecip());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR reciprocal");
    }

    public AssociationValidationError checkOrPerCopyRecipRange(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrPerCopyRecipRangeIsEmpty(association.getOrPerCopyRecipRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR reciprocal range");
    }

    public AssociationValidationError checkRangeIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkRangeIsEmpty(association.getRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Range");
    }

    public AssociationValidationError checkStandardErrorIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkStandardErrorIsEmpty(association.getStandardError());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Standard Error");
    }

    public AssociationValidationError checkDescriptionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkDescriptionIsEmpty(association.getDescription());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR/Beta description");
    }

    public AssociationValidationError checkMantissaIsLessThan10(Association association) {
        String errorMessage = validationChecks.checkMantissaIsLessThan10(association.getPvalueMantissa());
        return ErrorProcessingService.createError(errorMessage, "P-value Mantissa");
    }

    public AssociationValidationError checkExponentIsPresent(Association association) {
        String errorMessage = validationChecks.checkExponentIsPresent(association.getPvalueExponent());
        return ErrorProcessingService.createError(errorMessage, "P-value exponent");
    }

    public AssociationValidationError checkGene(Gene gene) {
        String errorMessage = validationChecks.checkGene(gene.getGeneName());
        return ErrorProcessingService.createError(errorMessage, "Gene");
    }

    public AssociationValidationError checkRiskAllele(RiskAllele riskAllele) {
        String errorMessage = validationChecks.checkRiskAllele(riskAllele.getRiskAlleleName());
        return ErrorProcessingService.createError(errorMessage, "Risk Allele");
    }
}