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

    private CheckingService checkingService;

    @Autowired
    public ValidationChecksBuilder(CheckingService checkingService) {
        this.checkingService = checkingService;
    }

    /**
     * Run p-value checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runPvalueChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();
        ValidationErrors.add(checkingService.checkMantissaIsLessThan10(association));
        ValidationErrors.add(checkingService.checkExponentIsPresent(association));
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }

    /**
     * Run general checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<ValidationError> runAnnotationChecks(Association association) {

        Collection<ValidationError> ValidationErrors = new ArrayList<>();

        ValidationError snpTypeError = checkingService.checkSnpType(association);
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

        ValidationError orIsPresent = checkingService.checkOrIsPresent(association, effectType);
        ValidationErrors.add(orIsPresent);

        ValidationError betaFoundForOr = checkingService.checkBetaValuesIsEmpty(association, effectType);
        ValidationErrors.add(betaFoundForOr);

        ValidationError betaUnitFoundForOr = checkingService.checkBetaUnitIsEmpty(association, effectType);
        ValidationErrors.add(betaUnitFoundForOr);

        ValidationError betaDirectionFoundForOr =
                checkingService.checkBetaDirectionIsEmpty(association, effectType);
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

        ValidationError betaIsPresent = checkingService.checkBetaIsPresent(association, effectType);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = checkingService.checkBetaUnitIsPresent(association, effectType);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                checkingService.checkBetaDirectionIsPresent(association, effectType);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = checkingService.checkOrEmpty(association, effectType);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = checkingService.checkOrRecipEmpty(association, effectType);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                checkingService.checkOrPerCopyRecipRange(association, effectType);
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

        ValidationError betaIsPresent = checkingService.checkBetaIsPresent(association, effectType);
        ValidationErrors.add(betaIsPresent);

        ValidationError betaUnitNotFound = checkingService.checkBetaUnitIsPresent(association, effectType);
        ValidationErrors.add(betaUnitNotFound);

        ValidationError betaDirectionNotFound =
                checkingService.checkBetaDirectionIsPresent(association, effectType);
        ValidationErrors.add(betaDirectionNotFound);

        ValidationError orFound = checkingService.checkOrEmpty(association, effectType);
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

        ValidationError orFound = checkingService.checkOrEmpty(association, effectType);
        ValidationErrors.add(orFound);

        ValidationError orRecipFound = checkingService.checkOrRecipEmpty(association, effectType);
        ValidationErrors.add(orRecipFound);

        ValidationError orRecipRangeFound =
                checkingService.checkOrPerCopyRecipRange(association, effectType);
        ValidationErrors.add(orRecipRangeFound);

        ValidationError betaFound = checkingService.checkBetaValuesIsEmpty(association, effectType);
        ValidationErrors.add(betaFound);

        ValidationError betaUnitFound = checkingService.checkBetaUnitIsEmpty(association, effectType);
        ValidationErrors.add(betaUnitFound);

        ValidationError betaDirectionFound =
                checkingService.checkBetaDirectionIsEmpty(association, effectType);
        ValidationErrors.add(betaDirectionFound);

        ValidationError rangeFound = checkingService.checkRangeIsEmpty(association, effectType);
        ValidationErrors.add(rangeFound);

        ValidationError standardErrorFound =
                checkingService.checkStandardErrorIsEmpty(association, effectType);
        ValidationErrors.add(standardErrorFound);

        ValidationError descriptionFound = checkingService.checkDescriptionIsEmpty(association, effectType);
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
                    ValidationError geneError = checkingService.checkGene(gene);
                    ValidationErrors.add(geneError);
                }

                for(RiskAllele riskAllele: riskAlleles){
                    ValidationError riskAlleleError = checkingService.checkRiskAllele(riskAllele);
                    ValidationErrors.add(riskAlleleError);
                }

            }
        }
        return ErrorProcessingService.checkForValidErrors(ValidationErrors);
    }
}
