package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.ac.ebi.spot.goci.curation.model.LastViewedAssociation;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.validator.SnpFormColumnValidator;
import uk.ac.ebi.spot.goci.curation.validator.SnpFormRowValidator;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;

import java.util.Date;

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

    // Validators
    private SnpFormRowValidator snpFormRowValidator;
    private SnpFormColumnValidator snpFormColumnValidator;

    @Autowired
    public AssociationOperationsService(SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                        SnpInteractionAssociationService snpInteractionAssociationService,
                                        AssociationReportRepository associationReportRepository,
                                        SnpFormRowValidator snpFormRowValidator,
                                        SnpFormColumnValidator snpFormColumnValidator) {
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
        this.associationReportRepository = associationReportRepository;
        this.snpFormRowValidator = snpFormRowValidator;
        this.snpFormColumnValidator = snpFormColumnValidator;
    }

    /**
     * Check if association is an OR or BETA type association
     *
     * @param association Association to check
     */
    public String determineIfAssociationIsOrType(Association association) {

        String measurementType = "or";
        if (association.getBetaNum() != null) {
            measurementType = "beta";
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
     * Check a standard SNP association form for errors
     *
     * @param result Binding result from edit form
     * @param form   The form to validate
     */

    public Boolean checkSnpAssociationFormErrors(BindingResult result,
                                                 SnpAssociationStandardMultiForm form) {
        for (SnpFormRow row : form.getSnpFormRows()) {
            snpFormRowValidator.validate(row, result);
        }

        return result.hasErrors();
    }

    /**
     * Check a SNP association interaction form for errors
     *
     * @param result Binding result from edit form
     * @param form   The form to validate
     */
    public Boolean checkSnpAssociationInteractionFormErrors(BindingResult result,
                                                            SnpAssociationInteractionForm form) {

        for (SnpFormColumn column : form.getSnpFormColumns()) {
            snpFormColumnValidator.validate(column, result);
        }

        return result.hasErrors();
    }
}