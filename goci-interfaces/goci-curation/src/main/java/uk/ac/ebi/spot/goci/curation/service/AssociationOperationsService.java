package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.service.ErrorCreationService;
import uk.ac.ebi.spot.goci.service.MappingService;
import uk.ac.ebi.spot.goci.service.ValidationService;
import uk.ac.ebi.spot.goci.utils.ErrorProcessingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by emma on 03/03/2016.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on associations
 */
@Service
public class AssociationOperationsService {

    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;
    private AssociationReportRepository associationReportRepository;
    private AssociationRepository associationRepository;
    private LocusRepository locusRepository;
    private MappingService mappingService;
    private LociAttributesService lociAttributesService;
    private ValidationService validationService;
    private AssociationValidationReportService associationValidationReportService;
    private ErrorCreationService errorCreationService;
    private TrackingOperationService trackingOperationService;

    @Autowired
    public AssociationOperationsService(SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                        SnpInteractionAssociationService snpInteractionAssociationService,
                                        AssociationReportRepository associationReportRepository,
                                        AssociationRepository associationRepository,
                                        LocusRepository locusRepository,
                                        MappingService mappingService,
                                        LociAttributesService lociAttributesService,
                                        ValidationService validationService,
                                        AssociationValidationReportService associationValidationReportService,
                                        ErrorCreationService errorCreationService,
                                        @Qualifier("associationTrackingOperationServiceImpl") TrackingOperationService trackingOperationService) {
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
        this.associationReportRepository = associationReportRepository;
        this.associationRepository = associationRepository;
        this.locusRepository = locusRepository;
        this.mappingService = mappingService;
        this.lociAttributesService = lociAttributesService;
        this.validationService = validationService;
        this.associationValidationReportService = associationValidationReportService;
        this.errorCreationService = errorCreationService;
        this.trackingOperationService = trackingOperationService;
    }

    /**
     * Check a standard SNP association form for errors, these are critical errors that would prevent creating an
     * association
     *
     * @param form The form to validate
     */
    public List<AssociationValidationView> checkSnpAssociationFormErrors(SnpAssociationStandardMultiForm form) {
        Collection<ValidationError> errors = new ArrayList<>();
        for (SnpFormRow row : form.getSnpFormRows()) {
            errors.add(errorCreationService.checkSnpValueIsPresent(row.getSnp()));
            errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(row.getStrongestRiskAllele()));
        }
        Collection<ValidationError> updatedErrors = ErrorProcessingService.checkForValidErrors(errors);
        return processAssociationValidationErrors(updatedErrors);
    }

    /**
     * Check a SNP association interaction form for errors, these are critical errors that would prevent creating an
     * association
     *
     * @param form The form to validate
     */
    public List<AssociationValidationView> checkSnpAssociationInteractionFormErrors(SnpAssociationInteractionForm form) {
        Collection<ValidationError> errors = new ArrayList<>();
        for (SnpFormColumn column : form.getSnpFormColumns()) {
            errors.add(errorCreationService.checkSnpValueIsPresent(column.getSnp()));
            errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(column.getStrongestRiskAllele()));
        }
        Collection<ValidationError> updatedErrors = ErrorProcessingService.checkForValidErrors(errors);
        return processAssociationValidationErrors(updatedErrors);
    }

    /**
     * Save association created from details on webform
     *  @param study       Study to assign association to
     * @param association Association to validate and save
     * @param user
     */
    public Collection<AssociationValidationView> saveAssociationCreatedFromForm(Study study,
                                                                                Association association,
                                                                                SecureUser user)
            throws EnsemblMappingException {

        // Validate association
        Collection<ValidationError> associationValidationErrors =
                validationService.runAssociationValidation(association, "full");

        // Create errors view that will be returned via controller
        Collection<AssociationValidationView> associationValidationViews =
                processAssociationValidationErrors(associationValidationErrors);

        // Validation returns warnings and errors, errors prevent a save action
        long errorCount = associationValidationErrors.parallelStream()
                .filter(validationError -> !validationError.getWarning())
                .count();

        if (errorCount == 0) {
            createAssociationCreationEvent(association, user);
            savAssociation(association, study, associationValidationErrors);
        }
        return associationValidationViews;
    }

    /**
     * Save edited association
     *
     * @param study         Study to assign association to
     * @param association   Association to validate and save
     * @param associationId existing association Id
     */
    public Collection<AssociationValidationView> saveEditedAssociationFromForm(Study study,
                                                                               Association association,
                                                                               Long associationId)
            throws EnsemblMappingException {

        // Validate association
        Collection<ValidationError> associationValidationErrors =
                validationService.runAssociationValidation(association, "full");

        // Create errors view that will be returned via controller
        Collection<AssociationValidationView> associationValidationViews =
                processAssociationValidationErrors(associationValidationErrors);

        // Validation returns warnings and errors, errors prevent a save action
        long errorCount = associationValidationErrors.parallelStream()
                .filter(validationError -> !validationError.getWarning())
                .count();

        if (errorCount == 0) {
            // Set ID of new association to the ID of the association we're currently editing
            association.setId(associationId);

            // Check for existing loci, when editing delete any existing loci and risk alleles
            // They will be recreated as part of the save method
            Association associationUserIsEditing =
                    associationRepository.findOne(associationId);
            lociAttributesService.deleteLocusAndRiskAlleles(associationUserIsEditing);

            savAssociation(association, study, associationValidationErrors);
        }
        return associationValidationViews;
    }

    public void savAssociation(Association association, Study study, Collection<ValidationError> errors)
            throws EnsemblMappingException {

        association.getLoci().forEach(this::saveLocusAttributes);

        // Set the study ID for our association
        association.setStudy(study);

        // Save our association information
        association.setLastUpdateDate(new Date());
        associationRepository.save(association);
        associationValidationReportService.createAssociationValidationReport(errors, association.getId());

        // Run mapping on association
        runMapping(study.getHousekeeping().getCurator(), association);
    }


    private void runMapping(Curator curator, Association association) throws EnsemblMappingException {
        mappingService.validateAndMapAssociation(association, curator.getLastName());
    }

    /**
     * Save transient objects on association before saving association
     *
     * @param locus Locus to save
     */
    private void saveLocusAttributes(Locus locus) {

        // Save genes
        Collection<Gene> savedGenes = lociAttributesService.saveGene(locus.getAuthorReportedGenes());
        locus.setAuthorReportedGenes(savedGenes);

        // Save risk allele
        Collection<RiskAllele> savedRiskAlleles =
                lociAttributesService.saveRiskAlleles(locus.getStrongestRiskAlleles());
        locus.setStrongestRiskAlleles(savedRiskAlleles);

        locusRepository.save(locus);
    }

    /**
     * Check if association is an OR or BETA type association
     *
     * @param association Association to check
     */
    public String determineIfAssociationIsOrType(Association association) {

        String measurementType = "none";
        if (association.getBetaNum() != null) {
            measurementType = "beta";
        }
        else {
            if (association.getOrPerCopyNum() != null) {
                measurementType = "or";
            }
        }
        return measurementType;
    }

    /**
     * Generate a the correct form type from association details
     *
     * @param association Association to create form from
     */

    public SnpAssociationForm generateForm(Association association) {

        if (association.getSnpInteraction() != null && association.getSnpInteraction()) {
            return createForm(association, snpInteractionAssociationService);
        }

        else {
            return createForm(association, singleSnpMultiSnpAssociationService);
        }
    }

    /**
     * Create a form from association details
     *
     * @param association Association to create form from
     * @param service     Service to create form
     */
    private SnpAssociationForm createForm(Association association, SnpAssociationFormService service) {
        return service.createForm(association);
    }

    /**
     * Gather mapping details for an association
     *
     * @param association Association with mapping details
     */
    public MappingDetails createMappingDetails(Association association) {
        MappingDetails mappingDetails = new MappingDetails();
        mappingDetails.setPerformer(association.getLastMappingPerformedBy());
        mappingDetails.setMappingDate(association.getLastMappingDate());
        return mappingDetails;
    }


    /**
     * Mark errors for a particular association as checked, this involves updating the linked association report
     *
     * @param association Association to mark as errors checked
     */
    public void associationErrorsChecked(Association association) {
        AssociationReport associationReport = association.getAssociationReport();
        associationReport.setErrorCheckedByCurator(true);
        associationReport.setLastUpdateDate(new Date());
        associationReportRepository.save(associationReport);
    }


    /**
     * Mark errors for a particular association as unchecked, this involves updating the linked association report
     *
     * @param association Association to mark as errors unchecked
     */
    public void associationErrorsUnchecked(Association association) {
        AssociationReport associationReport = association.getAssociationReport();
        associationReport.setErrorCheckedByCurator(false);
        associationReport.setLastUpdateDate(new Date());
        associationReportRepository.save(associationReport);
    }


    /**
     * Determine last viewed association
     *
     * @param associationId ID of association last viewed
     */
    public LastViewedAssociation getLastViewedAssociation(Long associationId) {

        LastViewedAssociation lastViewedAssociation = new LastViewedAssociation();
        if (associationId != null) {
            lastViewedAssociation.setId(associationId);
        }
        return lastViewedAssociation;
    }

    /**
     * Retrieve validation warnings for an association and return this is a structure accessible by view
     *
     * @param errors List of validation errors to process
     */
    private List<AssociationValidationView> processAssociationValidationErrors(Collection<ValidationError> errors) {

        List<AssociationValidationView> associationValidationViews = new ArrayList<>();
        errors.forEach(validationError -> {
            associationValidationViews.add(new AssociationValidationView(validationError.getField(),
                                                                         validationError.getError(),
                                                                         validationError.getWarning()));
        });
        return associationValidationViews;
    }

    /**
     * Add association creation event
     *
     * @param association Association to add creation event to
     */
    public void createAssociationCreationEvent(Association association, SecureUser user) {
        trackingOperationService.create(association, user);
    }
}