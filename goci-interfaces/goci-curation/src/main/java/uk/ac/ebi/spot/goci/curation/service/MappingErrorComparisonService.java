package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MappingErrorComparisonReport;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationReportRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

        Collection<AssociationReport> newAssociationReports = associationReportRepository.findAll();
        Map<Long, AssociationReport> associationIdToNewAssociationReportMap =
                createNewReportsMap(newAssociationReports);
        Collection<MappingErrorComparisonReport> comparisonReports = new ArrayList<>();

        for (AssociationReport oldError : oldErrors) {

            // Establish association and study details
            Long associationId = oldError.getAssociation().getId();
            Study study = studyRepository.findByAssociationsId(associationId);
            String pubmedId = study.getPubmedId();
            Long studyId = study.getId();

            // Compare errors
            AssociationReport newError = associationIdToNewAssociationReportMap.get(associationId);
            String oldSnpError = oldError.getSnpError();
            String newSnpError = newError.getSnpError();
            Boolean differenceInSnpErrors = compareDifferences(oldSnpError, newSnpError);

            String oldSnpGeneOnDiffChr = oldError.getSnpGeneOnDiffChr();
            String newSnpGeneOnDiffChr = newError.getSnpGeneOnDiffChr();
            Boolean differenceInSnpGeneOnDiffChr = compareDifferences(oldSnpGeneOnDiffChr, newSnpGeneOnDiffChr);

            String oldNoGeneForSymbol = oldError.getNoGeneForSymbol();
            String newNoGeneForSymbol = newError.getNoGeneForSymbol();
            Boolean differenceNoGeneForSymbol = compareDifferences(oldNoGeneForSymbol, newNoGeneForSymbol);

            String oldGeneError = oldError.getGeneError();
            String newGeneError = newError.getGeneError();
            Boolean differenceGeneError = compareDifferences(oldGeneError, newGeneError);

            String oldRestServiceError = oldError.getRestServiceError();
            String newRestServiceError = newError.getRestServiceError();
            Boolean differenceRestServiceError = compareDifferences(oldRestServiceError, newRestServiceError);

            String oldSuspectVariationError = oldError.getSuspectVariationError();
            String newSuspectVariationError = newError.getSuspectVariationError();
            Boolean differenceSuspectVariationError =
                    compareDifferences(oldSuspectVariationError, newSuspectVariationError);


            // Only add reports if a significant difference has been found
            if (differenceInSnpErrors || differenceInSnpGeneOnDiffChr || differenceNoGeneForSymbol ||
                    differenceGeneError || differenceRestServiceError || differenceSuspectVariationError) {

                // Create report
                MappingErrorComparisonReport mappingErrorComparisonReport = new MappingErrorComparisonReport();
                mappingErrorComparisonReport.setStudyId(studyId);
                mappingErrorComparisonReport.setAssociationId(associationId);
                mappingErrorComparisonReport.setPubmedId(pubmedId);
                mappingErrorComparisonReport.setOldSnpError(oldSnpError);
                mappingErrorComparisonReport.setNewSnpError(newSnpError);

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

        if (comparisonReports.size() > 0) {

            String header =
                    "Study ID\tAssociation ID\tPubmed ID\tOld SNP Error\tNew SNP Error\tOld Snp Gene On Diff Chr Error\tNew Snp Gene On Diff Chr Error\tOld No Gene For Symbol Error\tNew No Gene For Symbol\tOld Gene Error\tNew Gene Error\tOld Suspect Variation Error\tNew Suspect Variation Error\tOld Rest Service Error\tNew Rest Service Error\n";


            StringBuilder output = new StringBuilder();
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

            // Create and write to file
            String uploadDir =
                    System.getProperty("java.io.tmpdir") + File.separator + "gwas_new_release_mapping_error_report" +
                            File.separator;
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
            String dateStamp = df.format(new Date());
            File outputFile = new File(uploadDir + dateStamp + "_gwas_new_release_mapping_error_report.txt");

            // If at this stage we haven't got a file create one and write to it
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                    getLog().info("Created file: " + outputFile);

                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(outputFile), "utf-8"))) {
                        writer.write(String.valueOf(output));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                catch (IOException e) {
                    getLog().error("Could not create file " + uploadDir);
                }
            }
        }
        else {
            getLog().info("No difference in errors identified by latest Ensembl release and previous release.");
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
     * Creates a map of association ID to its current mapping errors
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