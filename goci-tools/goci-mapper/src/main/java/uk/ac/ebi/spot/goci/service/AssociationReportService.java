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
import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> errorToErrorType = createErrorMap();

        Collection<String> snpErrors = new ArrayList<>();
        Collection<String> snpGeneOnDiffChrErrors = new ArrayList<>();
        Collection<String> noGeneForSymbolErrors = new ArrayList<>();
        Collection<String> restServiceErrors = new ArrayList<>();
        Collection<String> suspectVariationErrors = new ArrayList<>();

        // Look for standard format error messages returned from mapping pipeline
        for (String error : errors) {

            for (Map.Entry<String, String> entry : errorToErrorType.entrySet()) {
                String errorMessage = entry.getKey();
                String errorType = entry.getValue();

                // rsID is not valid
                if (error.contains(errorMessage)) {
                    if (errorType.equals("restServiceError")) {
                        restServiceErrors.add(error);
                    }
                    if (errorType.equals("suspectVariationError")) {
                        suspectVariationErrors.add(error);
                    }
                    if (errorType.equals("snpError")) {
                        snpErrors.add(error);
                    }
                    if (errorType.equals("snpGeneOnDiffChrError")) {
                        snpGeneOnDiffChrErrors.add(error);
                    }
                    if (errorType.equals("noGeneForSymbolError")) {
                        noGeneForSymbolErrors.add(error);
                    }
                    else {
                        getLog().warn("For association ID: " + association.getId() +
                                              ", cannot determine error type for error " + error);
                    }
                }

                else {
                    getLog().warn("Association error: Association ID: " + association.getId() + " " + error);
                }
            }

        }

        // Format errors into string so they can be stored in database
        String allSnpErrors = null;
        String allSnpGeneOnDiffChrErrors = null;
        String allNoGeneForSymbolErrors = null;
        String allRestServiceErrors = null;
        String allSuspectVariationErrors = null;

        if (!snpErrors.isEmpty()) {
            allSnpErrors = String.join(", ", snpErrors);
        }

        if (!snpGeneOnDiffChrErrors.isEmpty()) {
            allSnpGeneOnDiffChrErrors = String.join(", ", snpGeneOnDiffChrErrors);
        }

        if (!noGeneForSymbolErrors.isEmpty()) {
            allNoGeneForSymbolErrors = String.join(", ", noGeneForSymbolErrors);
        }

        if (!restServiceErrors.isEmpty()) {
            allRestServiceErrors = String.join(", ", restServiceErrors);
        }

        if (!suspectVariationErrors.isEmpty()) {
            allSuspectVariationErrors = String.join(", ", suspectVariationErrors);
        }

        // Create association report object
        AssociationReport associationReport = new AssociationReport();
        associationReport.setLastUpdateDate(new Date());
        associationReport.setSnpError(allSnpErrors);
        associationReport.setSnpGeneOnDiffChr(allSnpGeneOnDiffChrErrors);
        associationReport.setNoGeneForSymbol(allNoGeneForSymbolErrors);
        associationReport.setRestServiceError(allRestServiceErrors);
        associationReport.setSuspectVariationError(allSuspectVariationErrors);

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
     * Creates a map of common errors and there types
     */
    private Map<String, String> createErrorMap() {

        Map<String, String> errorMap = new HashMap<>();

        // REST service error
        errorMap.putIfAbsent("No server is available to handle this request", "restServiceError");
        errorMap.putIfAbsent("is generating an invalid request", "restServiceError");
        errorMap.putIfAbsent("No data available", "restServiceError");

        // Add suspect variation errors that usually result from a snp not mapping
        errorMap.putIfAbsent("Variation does not map to the genome", "suspectVariationError");
        errorMap.putIfAbsent("Variation maps to more than one genomic location", "suspectVariationError");
        errorMap.putIfAbsent("Variation has more than 3 different alleles", "suspectVariationError");
        errorMap.putIfAbsent(
                "None of the variant alleles match the reference allele;Mapped position is not compatible with reported alleles,",
                "suspectVariationError");
        errorMap.putIfAbsent("no mapping available for the variant", "suspectVariationError");

        // Catch other common errors
        errorMap.putIfAbsent("not found for homo_sapiens", "snpError");
        errorMap.putIfAbsent("is on a different chromosome", "snpGeneOnDiffChrError");
        errorMap.putIfAbsent("No valid lookup found for symbol", "noGeneForSymbolError");


        return errorMap;
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
