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

        Collection<ValidationError> ValidationErrors = new ArrayList<>();
        ValidationErrors.add(errorCreationService.checkMantissaIsLessThan10(association));
        ValidationErrors.add(errorCreationService.checkExponentIsPresent(association));
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run general checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runAnnotationChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError snpTypeError = errorCreationService.checkSnpType(association);
        if (snpTypeError.getError() != null) {
            ValidationErrors.add(snpTypeError);
        }

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run OR checks on a row
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runOrChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError orIsPresent = errorCreationService.checkOrIsPresentAndLessThanOne(association);
        ValidationErrors.add(orIsPresent);

        ValidationError betaFoundForOr = errorCreationService.checkBetaValuesIsEmpty(association);
        ValidationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = errorCreationService.checkBetaUnitIsEmpty(association);
        ValidationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        ValidationErrors.add(betaDirectionFoundForOr);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }


    /**
     * Run OR checks on a row
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runAuthorLevelOrChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError betaFoundForOr = errorCreationService.checkBetaValuesIsEmpty(association);
        ValidationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = errorCreationService.checkBetaUnitIsEmpty(association);
        ValidationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        ValidationErrors.add(betaDirectionFoundForOr);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }


    /**
     * Run Beta checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runBetaChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresentAndIsNotNegative(association);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRange(association);
        ValidationErrors.add(orRecipRangeFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run author level beta checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runAuthorLevelBetaChecks(Association association) {
        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresentAndIsNotNegative(association);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        ValidationErrors.add(orFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runNoEffectErrors(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRange(association);
        ValidationErrors.add(orRecipRangeFound);

        ValidationError betaFound = errorCreationService.checkBetaValuesIsEmpty(association);
        ValidationErrors.add(betaFound);

        ValidationError betaUnitFound = errorCreationService.checkBetaUnitIsEmpty(association);
        ValidationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        ValidationErrors.add(betaDirectionFound);

        ValidationError rangeFound = errorCreationService.checkRangeIsEmpty(association);
        ValidationErrors.add(rangeFound);

        ValidationError standardErrorFound =
                errorCreationService.checkStandardErrorIsEmpty(association);
        ValidationErrors.add(standardErrorFound);

        ValidationError descriptionFound = errorCreationService.checkDescriptionIsEmpty(association);
        ValidationErrors.add(descriptionFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runAuthorLevelNoEffectChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError orFound = errorCreationService.checkOrEmpty(association);
        ValidationErrors.add(orFound);

        ValidationError betaFound = errorCreationService.checkBetaValuesIsEmpty(association);
        ValidationErrors.add(betaFound);

        ValidationError betaUnitFound = errorCreationService.checkBetaUnitIsEmpty(association);
        ValidationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                errorCreationService.checkBetaDirectionIsEmpty(association);
        ValidationErrors.add(betaDirectionFound);

        ValidationError rangeFound = errorCreationService.checkRangeIsEmpty(association);
        ValidationErrors.add(rangeFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run loci attributes checks on association
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runLociAttributeChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();
        Collection<Locus> loci = association.getLoci();

        if (loci != null) {
            for (Locus locus : association.getLoci()) {
                Collection<RiskAllele> riskAlleles = locus.getStrongestRiskAlleles();
                Collection<Gene> authorReportedGenes = locus.getAuthorReportedGenes();

                authorReportedGenes.forEach(gene -> {
                    ValidationError geneError = errorCreationService.checkGene(gene);
                    ValidationErrors.add(geneError);
                });

                riskAlleles.forEach(riskAllele -> {
                    ValidationError riskAlleleError = errorCreationService.checkRiskAllele(riskAllele);
                    ValidationErrors.add(riskAlleleError);

                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                    ValidationError snpError = errorCreationService.checkSnp(snp);
                    ValidationErrors.add(snpError);
                });
            }
        }
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run risk frequency checks on association
     *
     * @param association row to be checked
     */
    public Collection<ValidationError> runRiskFrequencyChecks(Association association) {
        Collection<ValidationError> ValidationErrors = new ArrayList<>();
        ValidationError error = errorCreationService.checkRiskFrequency(association);
        ValidationErrors.add(error);
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }
}
