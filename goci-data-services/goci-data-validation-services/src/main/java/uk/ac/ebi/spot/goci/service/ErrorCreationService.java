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
        return ErrorProcessingService.createError(errorMessage, "SNP");
    }

    public ValidationError checkStrongestAlleleValueIsPresent(String allele) {
        String errorMessage = validationChecks.checkValueIsPresent(allele);
        return ErrorProcessingService.createError(errorMessage, "Risk Allele");
    }

    public ValidationError checkSnpType(String type) {
        String errorMessage = validationChecks.checkSnpType(type);
        return ErrorProcessingService.createError(errorMessage, "SNP type");
    }

    public ValidationError checkOrIsPresentAndMoreThanOne(Float or) {
        String errorMessage = validationChecks.checkOrIsPresentAndMoreThanOne(or);
        return ErrorProcessingService.createError(errorMessage, "OR");
    }

    public ValidationError checkBetaValuesIsEmpty(Float beta) {
        String errorMessage = validationChecks.checkValueIsEmpty(beta);
        return ErrorProcessingService.createError(errorMessage, "Beta");
    }

    public ValidationError checkBetaUnitIsEmpty(String unit) {
        String errorMessage = validationChecks.checkValueIsEmpty(unit);
        return ErrorProcessingService.createError(errorMessage, "Beta Unit");
    }

    public ValidationError checkBetaDirectionIsEmpty(String direction) {
        String errorMessage = validationChecks.checkValueIsEmpty(direction);
        return ErrorProcessingService.createError(errorMessage, "Beta Direction");
    }

    public ValidationError checkBetaIsPresentAndIsNotNegative(Float beta) {
        String errorMessage = validationChecks.checkBetaIsPresentAndIsNotNegative(beta);
        return ErrorProcessingService.createError(errorMessage, "Beta");
    }

    public ValidationError checkBetaUnitIsPresent(String unit) {
        String errorMessage = validationChecks.checkValueIsPresent(unit);
        return ErrorProcessingService.createError(errorMessage, "Beta Unit");
    }

    public ValidationError checkBetaDirectionIsPresent(String direction) {
        String errorMessage = validationChecks.checkBetaDirectionIsPresent(direction);
        return ErrorProcessingService.createError(errorMessage, "Beta Direction");
    }

    public ValidationError checkOrEmpty(Float or) {
        String errorMessage = validationChecks.checkValueIsEmpty(or);
        return ErrorProcessingService.createError(errorMessage, "OR");
    }

    public ValidationError checkOrRecipEmpty(Float orRecip) {
        String errorMessage = validationChecks.checkValueIsEmpty(orRecip);
        return ErrorProcessingService.createError(errorMessage, "OR reciprocal");
    }

    public ValidationError checkOrPerCopyRecipRangeIsEmpty(String recipRange) {
        String errorMessage = validationChecks.checkValueIsEmpty(recipRange);
        return ErrorProcessingService.createError(errorMessage,
                                                  "OR reciprocal range");
    }

    public ValidationError checkRangeIsEmpty(String range) {
        String errorMessage = validationChecks.checkValueIsEmpty(range);
        return ErrorProcessingService.createError(errorMessage, "Range");
    }

    public ValidationError checkStandardErrorIsEmpty(Float standardError) {
        String errorMessage = validationChecks.checkValueIsEmpty(standardError);
        return ErrorProcessingService.createError(errorMessage, "Standard Error");
    }

    public ValidationError checkDescriptionIsEmpty(String description) {
        String errorMessage = validationChecks.checkValueIsEmpty(description);
        return ErrorProcessingService.createError(errorMessage,
                                                  "OR/Beta description");
    }

    // Pvalue checks
    public ValidationError checkMantissaIsLessThan10(Integer mantissa) {
        String errorMessage = validationChecks.checkMantissaIsLessThan10(mantissa);
        return ErrorProcessingService.createError(errorMessage, "P-value Mantissa");
    }

    public ValidationError checkExponentIsPresent(Integer exponent) {
        String errorMessage = validationChecks.checkExponentIsPresent(exponent);
        return ErrorProcessingService.createError(errorMessage, "P-value exponent");
    }

    // Loci attributes checks
    public ValidationError checkGene(String gene) {
        String errorMessage = validationChecks.checkGene(gene);
        return ErrorProcessingService.createError(errorMessage, "Gene");
    }

    public ValidationError checkRiskAllele(String riskAllele) {
        String errorMessage = validationChecks.checkRiskAllele(riskAllele);
        return ErrorProcessingService.createError(errorMessage, "Risk Allele");
    }

    public ValidationError checkSnp(String snp) {
        String errorMessage = validationChecks.checkSnp(snp);
        return ErrorProcessingService.createError(errorMessage, "SNP");
    }

    // Check risk frequency
    public ValidationError checkAssociationRiskFrequency(String riskFrequency) {
        String errorMessage = validationChecks.checkRiskFrequency(riskFrequency);
        return ErrorProcessingService.createError(errorMessage,
                                                  "Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls");
    }

    public ValidationError checkAlleleRiskFrequency(String riskFrequency) {
        String errorMessage = validationChecks.checkRiskFrequency(riskFrequency);
        return ErrorProcessingService.createError(errorMessage, "Independent SNP risk allele frequency in controls");
    }

    // Check range
    public ValidationError checkRangeIsPresent(String range) {
        String errorMessage = validationChecks.checkValueIsPresent(range);
        return ErrorProcessingService.createError(errorMessage, "Range");
    }

    // Check Gene and SNP are on same chromosome
    public ValidationError checkSnpGeneLocation(String snp, String gene) {
        String errorMessage = validationChecks.checkSnpGeneLocation(snp, gene);

        // Based on error message figure out the most appropriate field name
        String field = "Gene";
        if (errorMessage != null && errorMessage.startsWith("SNP")) {field = "SNP";}

        return ErrorProcessingService.createError(errorMessage, field);
    }

    // Check snp and risk allele use the correct delimiter
    public ValidationError checkSnpSynthax(String snp, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(snp, delimiter);
        return ErrorProcessingService.createError(errorMessage, "SNP");
    }

    public ValidationError checkRiskAlleleSynthax(String allele, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(allele, delimiter);
        return ErrorProcessingService.createError(errorMessage, "Risk Allele");
    }

    public ValidationError checkGeneSynthax(String gene, String delimiter) {
        String errorMessage = validationChecks.checkSynthax(gene, delimiter);
        return ErrorProcessingService.createError(errorMessage, "Gene");
    }

    public ValidationError checkSnpStatusIsPresent(Boolean genomeWide, Boolean limitedList) {
        String errorMessage = validationChecks.checkSnpStatus(genomeWide, limitedList);
        return ErrorProcessingService.createError(errorMessage, "SNP Status");
    }
}