package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationValidationView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationReport;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.AssociationValidationReportRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 04/07/2016.
 *
 * @author emma
 *         <p>
 *         Service to handle operations related to AssociationValidationReports
 */
@Service
public class AssociationValidationReportService {

    private AssociationRepository associationRepository;
    private AssociationValidationReportRepository associationValidationReportRepository;

    @Autowired
    public AssociationValidationReportService(AssociationRepository associationRepository,
                                              AssociationValidationReportRepository associationValidationReportRepository) {
        this.associationRepository = associationRepository;
        this.associationValidationReportRepository = associationValidationReportRepository;
    }

    /**
     * Create association validation reports and add to association
     *
     * @param errors List of errors
     * @param id     Association ID
     */
    public void createAssociationValidationReport(Collection<ValidationError> errors, Long id) {

        // Get list of existing reports and create list of existing warnings
        Association association = associationRepository.findOne(id);
        Collection<AssociationValidationReport> existingReports =
                associationValidationReportRepository.findByAssociationId(id);

        existingReports.forEach(associationValidationReport -> associationValidationReportRepository.delete(
                associationValidationReport));

        // Create association validation reports
        errors.forEach(validationError -> {
            // if warning is not already linked to association
            AssociationValidationReport associationValidationReport =
                    new AssociationValidationReport(validationError.getError(),
                                                    validationError.getField(),
                                                    false,
                                                    association);
            associationValidationReportRepository.save(associationValidationReport);
        });
    }

    /**
     * Retrieve validation warnings for an association and return this is a structure accessible by view
     *
     * @param associationId ID of association to get warning for
     */
    public List<AssociationValidationView> getAssociationWarnings(Long associationId) {

        List<AssociationValidationView> associationValidationViews = new ArrayList<>();
        associationValidationReportRepository.findByAssociationId(associationId)
                .forEach(associationValidationReport -> {
                    associationValidationViews.add(new AssociationValidationView(associationValidationReport.getValidatedField(),
                                                                                 associationValidationReport.getWarning(),
                                                                                 true));
                });
        return associationValidationViews;
    }
}