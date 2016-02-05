package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.MappingErrorComparisonReport;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 26/01/2016.
 *
 * @author emma
 *         <p>
 *         Service that compares mapping errors from previous and current release.
 */
@Service
public class MappingErrorComparisonService {

    // Location of output file
    @Value("${download.report}")
    private Resource report;

    private AssociationReportRepository associationReportRepository;

    private StudyRepository studyRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MappingErrorComparisonService(AssociationReportRepository associationReportRepository,
                                         StudyRepository studyRepository) {
        this.associationReportRepository = associationReportRepository;
        this.studyRepository = studyRepository;
    }

    /**
     * Method used to compare errors from a previous mapping run. Looking for SNPs with new errors, or SNPs with a
     * different type of error.
     *
     * @param oldErrors collection of all association reports in database before mapping to latest Ensembl release
     */
    public void compareOldVersusNewErrors(Collection<AssociationReport> oldErrors) {

        // Find all the latest association reports containing mapping errors
        Collection<AssociationReport> newAssociationReports = associationReportRepository.findAll();
        Map<Long, AssociationReport> associationIdToNewAssociationReportMap =
                createNewReportsMap(newAssociationReports);
        Collection<MappingErrorComparisonReport> comparisonReports = new ArrayList<>();

        // Create report
        for (AssociationReport oldErrorReport : oldErrors) {
            MappingErrorComparisonReport mappingErrorComparisonReport = new MappingErrorComparisonReport();

            // Establish association and study details
            Long associationId = oldErrorReport.getAssociation().getId();
            Study study = studyRepository.findByAssociationsId(associationId);
            String pubmedId = study.getPubmedId();
            Long studyId = study.getId();

            mappingErrorComparisonReport.setStudyId(studyId);
            mappingErrorComparisonReport.setAssociationId(associationId);
            mappingErrorComparisonReport.setPubmedId(pubmedId);

            // Compare errors
            AssociationReport newErrorReport = associationIdToNewAssociationReportMap.get(associationId);

            String oldSnpError = oldErrorReport.getSnpError();
            String newSnpError = newErrorReport.getSnpError();
            Boolean differenceInSnpErrors = compareDifferences(oldSnpError, newSnpError);
            if (differenceInSnpErrors) {
                mappingErrorComparisonReport.setOldSnpError(oldSnpError);
                mappingErrorComparisonReport.setNewSnpError(newSnpError);
            }

            String oldSnpGeneOnDiffChr = oldErrorReport.getSnpGeneOnDiffChr();
            String newSnpGeneOnDiffChr = newErrorReport.getSnpGeneOnDiffChr();
            Boolean differenceInSnpGeneOnDiffChr = compareDifferences(oldSnpGeneOnDiffChr, newSnpGeneOnDiffChr);
            if (differenceInSnpGeneOnDiffChr) {
                mappingErrorComparisonReport.setOldSnpGeneOnDiffChr(oldSnpGeneOnDiffChr);
                mappingErrorComparisonReport.setNewSnpGeneOnDiffChr(newSnpGeneOnDiffChr);
            }

            String oldNoGeneForSymbol = oldErrorReport.getNoGeneForSymbol();
            String newNoGeneForSymbol = newErrorReport.getNoGeneForSymbol();
            Boolean differenceNoGeneForSymbol = compareDifferences(oldNoGeneForSymbol, newNoGeneForSymbol);
            if (differenceNoGeneForSymbol) {
                mappingErrorComparisonReport.setOldNoGeneForSymbol(oldNoGeneForSymbol);
                mappingErrorComparisonReport.setNewNoGeneForSymbol(newNoGeneForSymbol);
            }

            String oldGeneError = oldErrorReport.getGeneError();
            String newGeneError = newErrorReport.getGeneError();
            Boolean differenceGeneError = compareDifferences(oldGeneError, newGeneError);
            if (differenceGeneError) {
                mappingErrorComparisonReport.setOldGeneError(oldGeneError);
                mappingErrorComparisonReport.setNewGeneError(newGeneError);
            }

            String oldRestServiceError = oldErrorReport.getRestServiceError();
            String newRestServiceError = newErrorReport.getRestServiceError();
            Boolean differenceRestServiceError = compareDifferences(oldRestServiceError, newRestServiceError);
            if (differenceRestServiceError) {
                mappingErrorComparisonReport.setOldRestServiceError(oldRestServiceError);
                mappingErrorComparisonReport.setNewRestServiceError(newRestServiceError);
            }

            String oldSuspectVariationError = oldErrorReport.getSuspectVariationError();
            String newSuspectVariationError = newErrorReport.getSuspectVariationError();
            Boolean differenceSuspectVariationError =
                    compareDifferences(oldSuspectVariationError, newSuspectVariationError);
            if (differenceSuspectVariationError) {
                mappingErrorComparisonReport.setOldSuspectVariationError(oldSuspectVariationError);
                mappingErrorComparisonReport.setNewSuspectVariationError(newSuspectVariationError);
            }

            // Only add reports if a significant difference has been found
            if (differenceInSnpErrors || differenceInSnpGeneOnDiffChr || differenceNoGeneForSymbol ||
                    differenceGeneError || differenceRestServiceError || differenceSuspectVariationError) {
                comparisonReports.add(mappingErrorComparisonReport);
            }
        }

        createFile(comparisonReports);
    }

    /**
     * Create file from comparisons
     *
     * @param comparisonReports reports to output to file
     */
    private void createFile(Collection<MappingErrorComparisonReport> comparisonReports) {

        StringBuilder output = new StringBuilder();

        if (comparisonReports.size() > 0) {

            String header =
                    "Study ID\tAssociation ID\tPubmed ID\tOld SNP Error\tNew SNP Error\tOld Snp Gene On Diff Chr Error\tNew Snp Gene On Diff Chr Error\tOld No Gene For Symbol Error\tNew No Gene For Symbol\tOld Gene Error\tNew Gene Error\tOld Suspect Variation Error\tNew Suspect Variation Error\tOld Rest Service Error\tNew Rest Service Error\n";

            output.append(header);

            for (MappingErrorComparisonReport comparisonReport : comparisonReports) {
                StringBuilder line = new StringBuilder();

                // Study ID
                if (comparisonReport.getStudyId() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getStudyId());
                }
                line.append("\t");

                // Association ID
                if (comparisonReport.getAssociationId() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getAssociationId());
                }
                line.append("\t");

                // Pubmed ID
                if (comparisonReport.getPubmedId() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getPubmedId());
                }
                line.append("\t");


                // SNP_ERROR
                if (comparisonReport.getOldSnpError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldSnpError());
                }
                line.append("\t");

                if (comparisonReport.getNewSnpError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewSnpError());
                }
                line.append("\t");

                // SNP_GENE_ON_DIFF_CHR
                if (comparisonReport.getOldSnpGeneOnDiffChr() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldSnpGeneOnDiffChr());
                }
                line.append("\t");

                if (comparisonReport.getNewSnpGeneOnDiffChr() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewSnpGeneOnDiffChr());
                }
                line.append("\t");

                // NO_GENE_FOR_SYMBOL
                if (comparisonReport.getOldNoGeneForSymbol() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldNoGeneForSymbol());
                }
                line.append("\t");

                if (comparisonReport.getNewNoGeneForSymbol() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewNoGeneForSymbol());
                }
                line.append("\t");

                // GENE_ERROR
                if (comparisonReport.getOldGeneError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldGeneError());
                }
                line.append("\t");

                if (comparisonReport.getNewGeneError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewGeneError());
                }
                line.append("\t");

                // SUSPECT_VARIATION_ERROR
                if (comparisonReport.getOldSuspectVariationError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldSuspectVariationError());
                }
                line.append("\t");

                if (comparisonReport.getNewSuspectVariationError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewSuspectVariationError());
                }
                line.append("\t");

                // REST_SERVICE_ERROR
                if (comparisonReport.getOldRestServiceError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getOldRestServiceError());
                }
                line.append("\t");

                if (comparisonReport.getNewRestServiceError() == null) {
                    line.append("");
                }
                else {
                    line.append(comparisonReport.getNewRestServiceError());
                }
                line.append("\t");


                // Add new line
                line.append("\n");
                output.append(line.toString());
            }
        }
        else {
            output.append("No changes in errors identified between current and previous Ensembl release.");
            getLog().info("No changes in errors identified between current and previous Ensembl release.");
        }

        // Create and write to file
        File outputFile = null;
        try {
            outputFile = report.getFile();
        }
        catch (IOException e) {
            getLog().info("Could not find file to write to errors to");
        }

        // If at this stage we haven't got a file create one and write to it
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
                getLog().info("Created file: " + outputFile);
            }
            catch (IOException e) {
                getLog().error("Could not create file");
            }
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "utf-8"))) {
            writer.write(String.valueOf(output));
        }
        catch (IOException e) {
            getLog().info("Could not write to " + outputFile);
        }
    }

    /**
     * Method used to compare errors. Looking for SNPs with new errors, or SNPs with a different type of error.
     *
     * @param oldError error message from mapping using previous release
     * @param newError error message from mapping using newly identified release
     */
    private Boolean compareDifferences(String oldError, String newError) {

        Boolean diffFound = false;

        // Both are not null
        if (oldError != null && newError != null) {
            // Error messages differ
            if (!oldError.isEmpty() && !newError.isEmpty()) {
                if (!oldError.equalsIgnoreCase(newError)) {
                    diffFound = true;
                }
            }
        }
        else {
            // case where a new error has appeared
            if (oldError == null && newError != null) {
                diffFound = true;
            }
        }

        return diffFound;
    }

    /**
     * Creates a map of association ID to its current mapping errors. This should make searching quicker
     *
     * @param reports collection of association reports
     */
    private Map<Long, AssociationReport> createNewReportsMap(Collection<AssociationReport> reports) {
        Map<Long, AssociationReport> associationIdToReportMap = new HashMap<>();
        for (AssociationReport report : reports) {
            associationIdToReportMap.put(report.getAssociation().getId(), report);
        }
        return associationIdToReportMap;
    }
}