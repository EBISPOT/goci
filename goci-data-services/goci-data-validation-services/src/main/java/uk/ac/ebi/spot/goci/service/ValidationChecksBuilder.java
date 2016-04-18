package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
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
    public Collection<AssociationValidationError> runPvalueChecks(Association association) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();
        associationValidationErrors.add(checkingService.checkMantissaIsLessThan10(association));
        associationValidationErrors.add(checkingService.checkExponentIsPresent(association));
        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run general checks on a row annotation
     *
     * @param association association to be checked
     */
    public Collection<AssociationValidationError> runAnnotationChecks(Association association) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError snpTypeError = checkingService.checkSnpType(association);
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

        AssociationValidationError orIsPresent = checkingService.checkOrIsPresent(association, effectType);
        associationValidationErrors.add(orIsPresent);

        AssociationValidationError betaFoundForOr = checkingService.checkBetaValuesIsEmpty(association, effectType);
        associationValidationErrors.add(betaFoundForOr);

        AssociationValidationError betaUnitFoundForOr = checkingService.checkBetaUnitIsEmpty(association, effectType);
        associationValidationErrors.add(betaUnitFoundForOr);

        AssociationValidationError betaDirectionFoundForOr =
                checkingService.checkBetaDirectionIsEmpty(association, effectType);
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

        AssociationValidationError betaIsPresent = checkingService.checkBetaIsPresent(association, effectType);
        associationValidationErrors.add(betaIsPresent);

        AssociationValidationError betaUnitNotFound = checkingService.checkBetaUnitIsPresent(association, effectType);
        associationValidationErrors.add(betaUnitNotFound);

        AssociationValidationError betaDirectionNotFound =
                checkingService.checkBetaDirectionIsPresent(association, effectType);
        associationValidationErrors.add(betaDirectionNotFound);

        AssociationValidationError orFound = checkingService.checkOrEmpty(association, effectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkingService.checkOrRecipEmpty(association, effectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound =
                checkingService.checkOrPerCopyRecipRange(association, effectType);
        associationValidationErrors.add(orRecipRangeFound);

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
    }

    /**
     * Run author level beta checks on a row
     *
     * @param association row to be checked
     * @param effectType
     */
    public Collection<AssociationValidationError> runAuthorLevelBetaChecks(Association association, String effectType) {
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        AssociationValidationError betaIsPresent = checkingService.checkBetaIsPresent(association, effectType);
        associationValidationErrors.add(betaIsPresent);

        AssociationValidationError betaUnitNotFound = checkingService.checkBetaUnitIsPresent(association, effectType);
        associationValidationErrors.add(betaUnitNotFound);

        AssociationValidationError betaDirectionNotFound =
                checkingService.checkBetaDirectionIsPresent(association, effectType);
        associationValidationErrors.add(betaDirectionNotFound);

        AssociationValidationError orFound = checkingService.checkOrEmpty(association, effectType);
        associationValidationErrors.add(orFound);

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

        AssociationValidationError orFound = checkingService.checkOrEmpty(association, effectType);
        associationValidationErrors.add(orFound);

        AssociationValidationError orRecipFound = checkingService.checkOrRecipEmpty(association, effectType);
        associationValidationErrors.add(orRecipFound);

        AssociationValidationError orRecipRangeFound =
                checkingService.checkOrPerCopyRecipRange(association, effectType);
        associationValidationErrors.add(orRecipRangeFound);

        AssociationValidationError betaFound = checkingService.checkBetaValuesIsEmpty(association, effectType);
        associationValidationErrors.add(betaFound);

        AssociationValidationError betaUnitFound = checkingService.checkBetaUnitIsEmpty(association, effectType);
        associationValidationErrors.add(betaUnitFound);

        AssociationValidationError betaDirectionFound =
                checkingService.checkBetaDirectionIsEmpty(association, effectType);
        associationValidationErrors.add(betaDirectionFound);

        AssociationValidationError rangeFound = checkingService.checkRangeIsEmpty(association, effectType);
        associationValidationErrors.add(rangeFound);

        AssociationValidationError standardErrorFound =
                checkingService.checkStandardErrorIsEmpty(association, effectType);
        associationValidationErrors.add(standardErrorFound);

        AssociationValidationError descriptionFound = checkingService.checkDescriptionIsEmpty(association, effectType);
        associationValidationErrors.add(descriptionFound);

        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
    }

    public Collection<AssociationValidationError> runLociAttributeChecks(Association association) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();
        Collection<Locus> loci = association.getLoci();

        if (loci == null) {
            for (Locus locus : association.getLoci()) {
                Collection<RiskAllele> riskAlleles = locus.getStrongestRiskAlleles();
                Collection<Gene> authorReportedGenes = locus.getAuthorReportedGenes();

                for (Gene gene : authorReportedGenes) {
                    AssociationValidationError geneError = checkingService.checkGene(gene);
                    associationValidationErrors.add(geneError);
                }

                for(RiskAllele riskAllele: riskAlleles){
                    AssociationValidationError riskAlleleError = checkingService.checkRiskAllele(riskAllele);
                    associationValidationErrors.add(riskAlleleError);
                }

            }
        }
        return ErrorProcessingService.checkForValidErrors(associationValidationErrors);
    }
}
