package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 27/07/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates an association report based on errors returned from mapping pipeline.
 */
@Service
public class AssociationReportService {

    // Repositories
    private AssociationReportRepository associationReportRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    //Constructor
    @Autowired
    public AssociationReportService(AssociationReportRepository associationReportRepository) {
        this.associationReportRepository = associationReportRepository;
    }

    /**
     * Method used to format data returned from view via form so it can be stored in database
     *
     * @param association association, used to create new association report
     * @param errors      collection of errors returned from mapping pipeline
     */
    public void processAssociationErrors(Association association, Collection<String> errors) {

        Collection<String> snpErrors = new ArrayList<>();
        Collection<String> snpGeneOnDiffChrErrors = new ArrayList<>();
        Collection<String> noGeneForSymbolErrors = new ArrayList<>();

        // Look for standard format error messages returned from mapping pipeline
        for (String error : errors) {
            // rsID is not valid
            if (error.contains("different chromosome")) {
                snpGeneOnDiffChrErrors.add(error);
            }

            else if (error.contains("not found in Ensembl")) {

                // Gene not in the same chromosome as the variant
                if (error.contains("Variant")) {
                    snpErrors.add(error);
                }

                // Gene symbol not found in Ensembl
                if (error.contains("Reported gene")) {
                    noGeneForSymbolErrors.add(error);
                }
            }

            else {
                getLog().warn("Association error: Association ID: " + association.getId() + " " + error);
            }
        }

        // Format errors into string so they can be stored in database
        String allSnpErrors = null;
        String allSnpGeneOnDiffChrErrors = null;
        String allNoGeneForSymbolErrors = null;

        if (!snpErrors.isEmpty()) {
            allSnpErrors = String.join(", ", snpErrors);
        }

        if (!snpGeneOnDiffChrErrors.isEmpty()) {
            allSnpGeneOnDiffChrErrors = String.join(", ", snpGeneOnDiffChrErrors);
        }

        if (!noGeneForSymbolErrors.isEmpty()) {
            allNoGeneForSymbolErrors = String.join(", ", noGeneForSymbolErrors);
        }

        // Create association report object
        AssociationReport associationReport = new AssociationReport();
        associationReport.setLastUpdateDate(new Date());
        associationReport.setSnpError(allSnpErrors);
        associationReport.setSnpGeneOnDiffChr(allSnpGeneOnDiffChrErrors);
        associationReport.setNoGeneForSymbol(allNoGeneForSymbolErrors);

        // Before setting link to association check for any existing reports linked to this association
        AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
        if (existingReport != null) {
            associationReportRepository.delete(existingReport);
        }

        associationReport.setAssociation(association);

        // Save association report
        associationReportRepository.save(associationReport);
    }

    /**
     * This method is used when the mapping pipeline returns no errors. It removes any existing association reports and
     * replaces with a new one with no errors. This ensures errors from previous runs of the mapping pipeline do not
     * remain in database if they are no longer appearing as mapping pipeline errors.
     *
     * @param association association, used to create new association report
     */
    public void updateAssociationReportDetails(Association association) {

        // Create association report object
        AssociationReport associationReport = new AssociationReport();
        associationReport.setLastUpdateDate(new Date());

        // Before setting link to association remove any existing reports linked to this association
        AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
        if (existingReport != null) {
            associationReportRepository.delete(existingReport);
        }
        associationReport.setAssociation(association);

        // Save association report
        associationReportRepository.save(associationReport);
    }
}
