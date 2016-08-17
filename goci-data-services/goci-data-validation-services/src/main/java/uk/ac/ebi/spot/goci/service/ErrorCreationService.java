package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.ValidationChecks;
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
public class ErrorCreationService {

    private ValidationChecks validationChecks;

    @Autowired
    public ErrorCreationService(ValidationChecks validationChecks) {
        this.validationChecks = validationChecks;
    }

    public ValidationError checkSnpValueIsPresent(String snp) {
        String errorMessage = validationChecks.checkValueIsPresent(snp);
        return ErrorProcessingService.createError(errorMessage, "SNP", false);
    }

    public ValidationError checkStrongestAlleleValueIsPresent(String allele) {
        String errorMessage = validationChecks.checkValueIsPresent(allele);
        return ErrorProcessingService.createError(errorMessage, "Risk Allele", false);
    }

    public ValidationError checkSnpType(String type) {
        String errorMessage = validationChecks.checkSnpType(type);
        return ErrorProcessingService.createError(errorMessage, "SNP type", false);
    }

    public ValidationError checkOrIsPresent(Float or) {
        String errorMessage = validationChecks.checkOrIsPresent(or);
        return ErrorProcessingService.createError(errorMessage, "OR", false);
    }

    public ValidationError checkOrRecipIsPresentAndLessThanOne(Float orPerCopyRecip) {
        String errorMessage = validationChecks.checkOrRecipIsPresentAndLessThanOne(orPerCopyRecip);
        return ErrorProcessingService.createError(errorMessage, "OR reciprocal", false);
    }

    public ValidationError checkOrAndOrRecip(Float or, Float orPerCopyRecip){
        String errorMessage = validationChecks.checkOrAndOrRecip(or, orPerCopyRecip);
        return ErrorProcessingService.createError(errorMessage, "OR and OR reciprocal", false);
    }

    public ValidationError checkBetaValuesIsEmpty(Float beta) {
        String errorMessage = validationChecks.checkValueIsEmpty(beta);
        return ErrorProcessingService.createError(errorMessage, "Beta", false);
    }

    public ValidationError checkBetaUnitIsEmpty(String unit) {
        String errorMessage = validationChecks.checkValueIsEmpty(unit);
        return ErrorProcessingService.createError(errorMessage, "Beta Unit", false);
    }

    public ValidationError checkBetaDirectionIsEmpty(String direction) {
        String errorMessage = validationChecks.checkValueIsEmpty(direction);
        return ErrorProcessingService.createError(errorMessage, "Beta Direction", false);
    }

    public ValidationError checkBetaIsPresentAndIsNotNegative(Float beta) {
        String errorMessage = validationChecks.checkBetaIsPresentAndIsNotNegative(beta);
        return ErrorProcessingService.createError(errorMessage, "Beta", false);
    }

    public ValidationError checkBetaUnitIsPresent(String unit) {
        String errorMessage = validationChecks.checkValueIsPresent(unit);
        return ErrorProcessingService.createError(errorMessage, "Beta Unit", false);
    }

    public ValidationError checkBetaDirectionIsPresent(String direction) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(direction);
        return ErrorProcessingService.createError(errorMessage, "Beta Direction", false);
    }

    public ValidationError checkOrEmpty(Float or) {
        String errorMessage = validationChecks.checkValueIsEmpty(or);
        return ErrorProcessingService.createError(errorMessage, "OR", false);
    }

    public ValidationError checkOrRecipEmpty(Float orRecip) {
        String errorMessage = validationChecks.checkValueIsEmpty(orRecip);
        return ErrorProcessingService.createError(errorMessage, "OR reciprocal", false);
    }

    public ValidationError checkOrPerCopyRecipRangeIsEmpty(String recipRange) {
        String errorMessage = validationChecks.checkValueIsEmpty(recipRange);
        return ErrorProcessingService.createError(errorMessage,
                                                  "OR reciprocal range", false);
    }

    public ValidationError checkRangeIsEmpty(String range) {
        String errorMessage = validationChecks.checkValueIsEmpty(range);
        return ErrorProcessingService.createError(errorMessage, "Range", false);
    }

    public ValidationError checkStandardErrorIsEmpty(Float standardError) {
        String errorMessage = validationChecks.checkValueIsEmpty(standardError);
        return ErrorProcessingService.createError(errorMessage, "Standard Error", false);
    }

    public ValidationError checkDescriptionIsEmpty(String description) {
        String errorMessage = validationChecks.checkValueIsEmpty(description);
        return ErrorProcessingService.createError(errorMessage,
                                                  "OR/Beta description", false);
    }

    // Pvalue checks
    public ValidationError checkMantissaIsLessThan10(Integer mantissa) {
        String errorMessage = validationChecks.checkMantissaIsLessThan10(mantissa);
        return ErrorProcessingService.createError(errorMessage, "P-value mantissa", false);
    }

    public ValidationError checkExponentIsPresentAndNegative(Integer exponent) {
        String errorMessage = validationChecks.checkExponentIsPresentAndNegative(exponent);
        return ErrorProcessingService.createError(errorMessage, "P-value exponent", false);
    }

    // Loci attributes checks
    public ValidationError checkGene(String gene) {
        String errorMessage = validationChecks.checkGene(gene);
        return ErrorProcessingService.createError(errorMessage, "Gene", true);
    }

    public ValidationError checkRiskAllele(String riskAllele) {
        String errorMessage = validationChecks.checkRiskAllele(riskAllele);
        if (errorMessage != null && errorMessage.equals("Value is empty")) {
            return ErrorProcessingService.createError(errorMessage, "Risk Allele", false);
        }
        else {
            return ErrorProcessingService.createError(errorMessage, "Risk Allele", true);
        }
    }

    public ValidationError checkSnp(String snp) {
        String errorMessage = validationChecks.checkSnp(snp);
        if (errorMessage != null && errorMessage.equals("SNP identifier is empty")) {
            return ErrorProcessingService.createError(errorMessage, "SNP", false);
        }
        else {
            return ErrorProcessingService.createError(errorMessage, "SNP", true);
        }
    }

    // Check risk frequency
    public ValidationError checkAssociationRiskFrequency(String riskFrequency) {
        String errorMessage = validationChecks.checkRiskFrequency(riskFrequency);
        return ErrorProcessingService.createError(errorMessage,
                                                  "Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls",
                                                  false);
    }

    public ValidationError checkAlleleRiskFrequency(String riskFrequency) {
        String errorMessage = validationChecks.checkRiskFrequency(riskFrequency);
        return ErrorProcessingService.createError(errorMessage,
                                                  "Independent SNP risk allele frequency in controls",
                                                  false);
    }

    // Check range
    public ValidationError checkRangeIsPresent(String range) {
        String errorMessage = validationChecks.checkValueIsPresent(range);
        return ErrorProcessingService.createError(errorMessage, "Range", false);
    }

    public ValidationError checkOrPerCopyRecipRangeIsPresent(String orPerCopyRecipRange) {
        String errorMessage = validationChecks.checkValueIsPresent(orPerCopyRecipRange);
        return ErrorProcessingService.createError(errorMessage, "OR reciprocal range", false);
    }

    // Check Gene and SNP are on same chromosome
    public ValidationError checkSnpGeneLocation(String snp, String gene) {
        String errorMessage = validationChecks.checkSnpGeneLocation(snp, gene);

        // Based on error message figure out the most appropriate field name
        String field = "Gene";
        if (errorMessage != null && errorMessage.startsWith("SNP")) {field = "SNP";}

        return ErrorProcessingService.createError(errorMessage, field, true);
    }

    // Check snp and risk allele use the correct delimiter
    public ValidationError checkSnpSynthax(String snp, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(snp, delimiter);
        return ErrorProcessingService.createError(errorMessage, "SNP", false);
    }

    public ValidationError checkRiskAlleleSynthax(String allele, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(allele, delimiter);
        return ErrorProcessingService.createError(errorMessage, "Risk Allele", false);
    }

    public ValidationError checkGeneSynthax(String gene, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(gene, delimiter);
        return ErrorProcessingService.createError(errorMessage, "Gene", false);
    }

    public ValidationError checkSnpStatusIsPresent(Boolean genomeWide, Boolean limitedList) {
        String errorMessage = validationChecks.checkSnpStatus(genomeWide, limitedList);
        return ErrorProcessingService.createError(errorMessage, "SNP Status", false);
    }
}