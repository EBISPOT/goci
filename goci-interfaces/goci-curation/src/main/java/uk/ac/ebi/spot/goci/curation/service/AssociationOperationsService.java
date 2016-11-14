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
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
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
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
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
    private AssociationMappingErrorService associationMappingErrorService;

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
                                        @Qualifier("associationTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                        AssociationMappingErrorService associationMappingErrorService) {
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
        this.associationMappingErrorService = associationMappingErrorService;
    }

    /**
     * Check a standard SNP association form for errors, these are critical errors that would prevent creating an
     * association
     *
     * @param form            The form to validate
     * @param measurementType Determine if user has selected and populated essential value on the form
     */
    public List<AssociationValidationView> checkSnpAssociationFormErrors(SnpAssociationStandardMultiForm form,
                                                                         String measurementType) {
        Collection<ValidationError> errors = new ArrayList<>();
        for (SnpFormRow row : form.getSnpFormRows()) {
            errors.add(errorCreationService.checkSnpValueIsPresent(row.getSnp()));
            errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(row.getStrongestRiskAllele()));
        }

        // Ensure user has entered required information on the form
        if (measurementType.equals("or")) {
            errors.add(errorCreationService.checkOrIsPresent(form.getOrPerCopyNum()));
        }
        if (measurementType.equals("beta")) {
            errors.add(errorCreationService.checkBetaIsPresentAndIsNotNegative(form.getBetaNum()));
        }

        Collection<ValidationError> updatedError = ErrorProcessingService.checkForValidErrors(errors);
        return processAssociationValidationErrors(updatedError);
    }

    /**
     * Check a SNP association interaction form for errors, these are critical errors that would prevent creating an
     * association
     *
     * @param form            The form to validate
     * @param measurementType Determine if user has selected and populated essential value on the form
     */
    public List<AssociationValidationView> checkSnpAssociationInteractionFormErrors(SnpAssociationInteractionForm form,
                                                                                    String measurementType) {
        Collection<ValidationError> errors = new ArrayList<>();
        for (SnpFormColumn column : form.getSnpFormColumns()) {
            errors.add(errorCreationService.checkSnpValueIsPresent(column.getSnp()));
            errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(column.getStrongestRiskAllele()));
        }

        // Ensure user has entered required information on the form
        if (measurementType.equals("or")) {
            errors.add(errorCreationService.checkOrIsPresent(form.getOrPerCopyNum()));
        }
        if (measurementType.equals("beta")) {
            errors.add(errorCreationService.checkBetaIsPresentAndIsNotNegative(form.getBetaNum()));
        }

        Collection<ValidationError> updatedError = ErrorProcessingService.checkForValidErrors(errors);
        return processAssociationValidationErrors(updatedError);
    }

    /**
     * Save association created from details on webform
     *
     * @param study       Study to assign association to
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
            saveAssociation(association, study, associationValidationErrors);

            // Run mapping on association
            runMapping(study.getHousekeeping().getCurator(), association, user);
        }
        return associationValidationViews;
    }

    /**
     * Save edited association
     *
     * @param study         Study to assign association to
     * @param association   Association to validate and save
     * @param associationId existing association Id
     * @param user
     */
    public Collection<AssociationValidationView> saveEditedAssociationFromForm(Study study,
                                                                               Association association,
                                                                               Long associationId,
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
            // Set ID of new association to the ID of the association we're currently editing
            association.setId(associationId);

            // Check for existing loci, when editing delete any existing loci and risk alleles
            // They will be recreated as part of the save method
            Association associationUserIsEditing =
                    associationRepository.findOne(associationId);
            lociAttributesService.deleteLocusAndRiskAlleles(associationUserIsEditing);

            // Add events
            association.setEvents(associationUserIsEditing.getEvents());

            // Add update event and save
            createAssociationUpdateEvent(association, user);
            saveAssociation(association, study, associationValidationErrors);

            // Run mapping on association
            runMapping(study.getHousekeeping().getCurator(), association, user);

        }
        return associationValidationViews;
    }


    /**
     * Validate & save association
     *
     * @param study         Study to assign association to
     * @param association   Association to validate and save
     * @param user
     */
    public Collection<AssociationValidationView> validateAndSaveAssociation(Study study,
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
//            // Set ID of new association to the ID of the association we're currently editing
//            association.setId(associationId);
//
//            // Check for existing loci, when editing delete any existing loci and risk alleles
//            // They will be recreated as part of the save method
//            Association associationUserIsEditing =
//                    associationRepository.findOne(associationId);
//            lociAttributesService.deleteLocusAndRiskAlleles(associationUserIsEditing);

            // Add events
//            association.setEvents(associationUserIsEditing.getEvents());

            // Add update event and save
            createAssociationUpdateEvent(association, user);
            saveAssociation(association, study, associationValidationErrors);

            // Run mapping on association
            runMapping(study.getHousekeeping().getCurator(), association, user);

        }
        return associationValidationViews;
    }

    /**
     * Save an association
     *
     * @param association Association to save
     * @param study       Study to assign association to
     * @param errors      Validation errors, these errors do not prevent a save but should be reviewed by curators
     */
    public void saveAssociation(Association association, Study study, Collection<ValidationError> errors) {

        association.getLoci().forEach(this::saveLocusAttributes);

        // Set the study ID for our association
        association.setStudy(study);

        // Save our association information
        association.setLastUpdateDate(new Date());
        associationRepository.save(association);
        associationValidationReportService.createAssociationValidationReport(errors, association.getId());
    }

    /**
     * Save an association
     *
     * @param association Association to map
     * @param curator     Curator running mapping
     * @param user        User to assign mapping event to
     */
    public void runMapping(Curator curator, Association association, SecureUser user) throws EnsemblMappingException {
        mappingService.validateAndMapAssociation(association, curator.getLastName(), user);
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
     * Gather mapping details for an association
     *
     * @param association Association with mapping details
     */
    public MappingDetails createMappingDetails(Association association) {
        MappingDetails mappingDetails = new MappingDetails();
        mappingDetails.setPerformer(association.getLastMappingPerformedBy());
        mappingDetails.setMappingDate(association.getLastMappingDate());
        mappingDetails.setAssociationErrorMap(associationMappingErrorService.createAssociationErrorMap(association.getAssociationReport()));
        return mappingDetails;
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
     * Approve association
     *
     * @param association Association to approve
     * @param user        User performing request
     */
    public void approveAssociation(Association association, SecureUser user) {
        // Mark errors as checked
        associationErrorsChecked(association);

        // Set snpChecked attribute to true
        association.setSnpApproved(true);
        association.setLastUpdateDate(new Date());

        // Add approve event
        createAssociationApproveEvent(association, user);
        associationRepository.save(association);
    }

    /**
     * Unapprove association
     *
     * @param association Association to unapprove
     * @param user        User performing request
     */
    public void unapproveAssociation(Association association, SecureUser user) {
        // Mark errors as unchecked
        associationErrorsUnchecked(association);

        // Set snpChecked attribute to true
        association.setSnpApproved(false);
        association.setLastUpdateDate(new Date());

        // Add unapprove event
        createAssociationUnapproveEvent(association, user);
        associationRepository.save(association);
    }

    /**
     * Add association creation event
     *
     * @param association Association to add creation event to
     * @param user        User performing request
     */
    public void createAssociationCreationEvent(Association association, SecureUser user) {
        trackingOperationService.create(association, user);
    }

    /**
     * Add association approve event
     *
     * @param association Association to add approve event to
     * @param user        User performing request
     */
    private void createAssociationApproveEvent(Association association, SecureUser user) {
        trackingOperationService.update(association, user, "ASSOCIATION_APPROVED");
    }

    /**
     * Add association unapprove event
     *
     * @param association Association to add unapprove event to
     * @param user        User performing request
     */
    private void createAssociationUnapproveEvent(Association association, SecureUser user) {
        trackingOperationService.update(association, user, "ASSOCIATION_UNAPPROVED");
    }

    /**
     * Add association update event
     *
     * @param association Association to add update event to
     * @param user        User performing request
     */
    private void createAssociationUpdateEvent(Association association, SecureUser user) {
        trackingOperationService.update(association, user, "ASSOCIATION_UPDATE");
    }

    /**
     * Mark errors for a particular association as checked, this involves updating the linked association report
     *
     * @param association Association to mark as errors checked
     */
    private void associationErrorsChecked(Association association) {
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
    private void associationErrorsUnchecked(Association association) {
        AssociationReport associationReport = association.getAssociationReport();
        associationReport.setErrorCheckedByCurator(false);
        associationReport.setLastUpdateDate(new Date());
        associationReportRepository.save(associationReport);
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
     * Create a form from association details
     *
     * @param association Association to create form from
     * @param service     Service to create form
     */
    private SnpAssociationForm createForm(Association association, SnpAssociationFormService service) {
        return service.createForm(association);
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
}