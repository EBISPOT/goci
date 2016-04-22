package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
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
     * @param effectType
     */
    public Collection<ValidationError> runOrChecks(Association association, String effectType) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError orIsPresent = errorCreationService.checkOrIsPresent(association, effectType);
        ValidationErrors.add(orIsPresent);

        ValidationError betaFoundForOr = errorCreationService.checkBetaValuesIsEmpty(association, effectType);
        ValidationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = errorCreationService.checkBetaUnitIsEmpty(association, effectType);
        ValidationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                errorCreationService.checkBetaDirectionIsEmpty(association, effectType);
        ValidationErrors.add(betaDirectionFoundForOr);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run Beta checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<ValidationError> runBetaChecks(Association association, String effectType) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresent(association, effectType);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association, effectType);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association, effectType);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association, effectType);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association, effectType);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRange(association, effectType);
        ValidationErrors.add(orRecipRangeFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run author level beta checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<ValidationError> runAuthorLevelBetaChecks(Association association, String effectType) {
        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError betaIsPresent = errorCreationService.checkBetaIsPresent(association, effectType);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = errorCreationService.checkBetaUnitIsPresent(association, effectType);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                errorCreationService.checkBetaDirectionIsPresent(association, effectType);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = errorCreationService.checkOrEmpty(association, effectType);
        ValidationErrors.add(orFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run no effect checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<ValidationError> runNoEffectErrors(Association association, String effectType) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError orFound = errorCreationService.checkOrEmpty(association, effectType);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = errorCreationService.checkOrRecipEmpty(association, effectType);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                errorCreationService.checkOrPerCopyRecipRange(association, effectType);
        ValidationErrors.add(orRecipRangeFound);

        ValidationError betaFound = errorCreationService.checkBetaValuesIsEmpty(association, effectType);
        ValidationErrors.add(betaFound);

        ValidationError betaUnitFound = errorCreationService.checkBetaUnitIsEmpty(association, effectType);
        ValidationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                errorCreationService.checkBetaDirectionIsEmpty(association, effectType);
        ValidationErrors.add(betaDirectionFound);

        ValidationError rangeFound = errorCreationService.checkRangeIsEmpty(association, effectType);
        ValidationErrors.add(rangeFound);

        ValidationError standardErrorFound =
                errorCreationService.checkStandardErrorIsEmpty(association, effectType);
        ValidationErrors.add(standardErrorFound);

        ValidationError descriptionFound = errorCreationService.checkDescriptionIsEmpty(association, effectType);
        ValidationErrors.add(descriptionFound);

        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    public Collection<ValidationError> runLociAttributeChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();
        Collection<Locus> loci = association.getLoci();

        if (loci == null) {
            for (Locus locus : association.getLoci()) {
                Collection<RiskAllele> riskAlleles = locus.getStrongestRiskAlleles();
                Collection<Gene> authorReportedGenes = locus.getAuthorReportedGenes();

                for (Gene gene : authorReportedGenes) {
                    ValidationError geneError = errorCreationService.checkGene(gene);
                    ValidationErrors.add(geneError);
                }

                for(RiskAllele riskAllele: riskAlleles){
                    ValidationError riskAlleleError = errorCreationService.checkRiskAllele(riskAllele);
                    ValidationErrors.add(riskAlleleError);
                }

            }
        }
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }
}
