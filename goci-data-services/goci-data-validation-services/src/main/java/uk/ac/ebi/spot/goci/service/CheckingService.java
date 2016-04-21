package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.ValidationError;
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

    public ValidationError checkSnpValueIsPresent(AssociationUploadRow row) {
        String errorMessage = validationChecks.checkValueIsPresent(row.getSnp());
        return ErrorProcessingService.createError(errorMessage, "SNP");
    }

    public ValidationError checkStrongestAlleleValueIsPresent(AssociationUploadRow row) {
        String errorMessage = validationChecks.checkValueIsPresent(row.getStrongestAllele());
        return ErrorProcessingService.createError(errorMessage, "Strongest SNP-Risk Allele/Effect Allele");
    }

    public ValidationError checkSnpType(Association association) {
        String errorMessage = validationChecks.checkSnpType(association.getSnpType());
        return ErrorProcessingService.createError(errorMessage, "SNP type");
    }

    public ValidationError checkOrIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrIsPresent(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    public ValidationError checkBetaValuesIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaValueIsEmpty(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    public ValidationError checkBetaUnitIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsEmpty(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    public ValidationError checkBetaDirectionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsEmpty(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    public ValidationError checkBetaIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaIsPresent(association.getBetaNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta");
    }

    public ValidationError checkBetaUnitIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaUnitIsPresent(association.getBetaUnit());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Unit");
    }

    public ValidationError checkBetaDirectionIsPresent(Association association, String effectType) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(association.getBetaDirection());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Beta Direction");
    }

    public ValidationError checkOrEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrEmpty(association.getOrPerCopyNum());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR");
    }

    public ValidationError checkOrRecipEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrRecipEmpty(association.getOrPerCopyRecip());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "OR reciprocal");
    }

    public ValidationError checkOrPerCopyRecipRange(Association association, String effectType) {
        String errorMessage = validationChecks.checkOrPerCopyRecipRangeIsEmpty(association.getOrPerCopyRecipRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR reciprocal range");
    }

    public ValidationError checkRangeIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkRangeIsEmpty(association.getRange());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Range");
    }

    public ValidationError checkStandardErrorIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkStandardErrorIsEmpty(association.getStandardError());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType, "Standard Error");
    }

    public ValidationError checkDescriptionIsEmpty(Association association, String effectType) {
        String errorMessage = validationChecks.checkDescriptionIsEmpty(association.getDescription());
        return ErrorProcessingService.createError(errorMessage + "with effect type: " + effectType,
                                                  "OR/Beta description");
    }

    public ValidationError checkMantissaIsLessThan10(Association association) {
        String errorMessage = validationChecks.checkMantissaIsLessThan10(association.getPvalueMantissa());
        return ErrorProcessingService.createError(errorMessage, "P-value Mantissa");
    }

    public ValidationError checkExponentIsPresent(Association association) {
        String errorMessage = validationChecks.checkExponentIsPresent(association.getPvalueExponent());
        return ErrorProcessingService.createError(errorMessage, "P-value exponent");
    }

    public ValidationError checkGene(Gene gene) {
        String errorMessage = validationChecks.checkGene(gene.getGeneName());
        return ErrorProcessingService.createError(errorMessage, "Gene");
    }

    public ValidationError checkRiskAllele(RiskAllele riskAllele) {
        String errorMessage = validationChecks.checkRiskAllele(riskAllele.getRiskAlleleName());
        return ErrorProcessingService.createError(errorMessage, "Risk Allele");
    }
}