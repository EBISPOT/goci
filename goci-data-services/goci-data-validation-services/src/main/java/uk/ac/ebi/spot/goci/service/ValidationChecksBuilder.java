package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.utils.ErrorProcessingService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 18/04/2016.
 *
 * @author emma
 *         <p>
 *         Class that runs various combinations of error checks
 */
@Service
public class ValidationChecksBuilder {

    private ErrorCreationService errorCreationService;

    @Autowired
    public ValidationChecksBuilder(ErrorCreationService errorCreationService) {
        this.errorCreationService = errorCreationService;
    }

    /**
     * Run p-value checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runPvalueChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(errorCreationService.checkMantissaIsLessThan10(association));
        validationErrors.add(errorCreationService.checkExponentIsPresent(association));
        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run general checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runAnnotationChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError snpTypeError = errorCreationService.checkSnpType(association);
        if (snpTypeError.getError() != null) {
            validationErrors.add(snpTypeError);
        }

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run OR checks on a row
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runOrChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError orIsPresent = errorCreationService.checkOrIsPresentAndMoreThanOne(association);
        validationErrors.add(orIsPresent);

        ValidationError betaFoundForOr = errorCreationService.checkBetaValuesIsEmpty(association);
        validationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = errorCreationService.checkBetaUnitIsEmpty(association);
        validationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        validationErrors.add(betaDirectionFoundForOr);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }


    /**
     * Run OR checks on a row
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runAuthorLevelOrChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError betaFoundForOr = errorCreationService.checkBetaValuesIsEmpty(association);
        validationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = errorCreationService.checkBetaUnitIsEmpty(association);
        validationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        validationErrors.add(betaDirectionFoundForOr);

        ValidationError rangeNotFound =
                errorCreationService.checkRangeIsPresent(association);
        validationErrors.add(rangeNotFound);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }


    /**
     * Run Beta checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runBetaChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresentAndIsNotNegative(association);
        validationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association);
        validationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association);
        validationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        validationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association);
        validationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRangeIsEmpty(association);
        validationErrors.add(orRecipRangeFound);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run author level beta checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runAuthorLevelBetaChecks(Association association) {
        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresentAndIsNotNegative(association);
        validationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association);
        validationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association);
        validationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        validationErrors.add(orFound);

        ValidationError rangeNotFound =
                errorCreationService.checkRangeIsPresent(association);
        validationErrors.add(rangeNotFound);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runNoEffectErrors(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        validationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association);
        validationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRangeIsEmpty(association);
        validationErrors.add(orRecipRangeFound);

        ValidationError betaFound = errorCreationService.checkBetaValuesIsEmpty(association);
        validationErrors.add(betaFound);

        ValidationError betaUnitFound = errorCreationService.checkBetaUnitIsEmpty(association);
        validationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        validationErrors.add(betaDirectionFound);

        ValidationError rangeFound = errorCreationService.checkRangeIsEmpty(association);
        validationErrors.add(rangeFound);

        ValidationError standardErrorFound =
                errorCreationService.checkStandardErrorIsEmpty(association);
        validationErrors.add(standardErrorFound);

        ValidationError descriptionFound = errorCreationService.checkDescriptionIsEmpty(association);
        validationErrors.add(descriptionFound);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runAuthorLevelNoEffectChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        validationErrors.add(orFound);

        ValidationError betaFound = errorCreationService.checkBetaValuesIsEmpty(association);
        validationErrors.add(betaFound);

        ValidationError betaUnitFound = errorCreationService.checkBetaUnitIsEmpty(association);
        validationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        validationErrors.add(betaDirectionFound);

        ValidationError rangeFound = errorCreationService.checkRangeIsEmpty(association);
        validationErrors.add(rangeFound);

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run loci attributes checks on association
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runLociAttributeChecks(Association association) {

        Collection<ValidationError> validationErrors = new ArrayList<>();
        Collection<Locus> loci = association.getLoci();

        if (loci != null) {
            for (Locus locus : association.getLoci()) {
                Collection<RiskAllele> riskAlleles = locus.getStrongestRiskAlleles();
                Collection<Gene> authorReportedGenes = locus.getAuthorReportedGenes();

                // Check genes are valid
                authorReportedGenes.forEach(gene -> {
                    ValidationError geneError = errorCreationService.checkGene(gene);
                    validationErrors.add(geneError);
                });

                // Check risk allele attributes
                riskAlleles.forEach(riskAllele -> {

                    if (association.getSnpInteraction()) {

                    }

                    ValidationError riskAlleleError = errorCreationService.checkRiskAllele(riskAllele);
                    validationErrors.add(riskAlleleError);

                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                    ValidationError snpError = errorCreationService.checkSnp(snp);

                    authorReportedGenes.forEach(gene -> {
                        ValidationError snpGeneLocationError =
                                errorCreationService.checkSnpGeneLocation(snp, gene);
                        validationErrors.add(snpGeneLocationError);
                    });
                    validationErrors.add(snpError);
                });
            }
        }
        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }

    /**
     * Run risk frequency checks on association and the risk alleles linked to a locus
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runRiskFrequencyChecks(Association association) {
        Collection<ValidationError> validationErrors = new ArrayList<>();

        // Check the association risk frequency
        validationErrors.add(errorCreationService.checkAssociationRiskFrequency(association));

        // Check risk frequency on alleles if its a SNP interaction association
        if (association.getSnpInteraction()) {
            for (Locus locus : association.getLoci()) {
                locus.getStrongestRiskAlleles().forEach(riskAllele -> {
                    validationErrors.add(errorCreationService.checkAlleleRiskFrequency(riskAllele));
                });
            }
        }

        return ErrorProcessingService.checkForValidErrors(validationErrors);
    }
}
