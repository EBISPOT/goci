package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CatalogSummaryView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 17/02/15.
 *
 * @author emma
 *         <p>
 *         Creates an array of strings containing data from views which will be added to file.
 */

@Service
public class ProcessView {

    private NCBICatalogService ncbiCatalogService;

    @Autowired
    public ProcessView(NCBICatalogService ncbiCatalogService) {
        this.ncbiCatalogService = ncbiCatalogService;
    }

    public List<String> serialiseViews() {

        Collection<CatalogSummaryView> views = ncbiCatalogService.getCatalogSummaryViewsWithStatusSendToNcbi();
        List<String> serialisedViews = new ArrayList<String>();

        // For each view create a line from the data returned
        for (CatalogSummaryView view : views) {

            String line = "";
            // Format dates
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

            // As a default set all strings to empty string with tab
            // Also trim strings as the database contains newlines/tabs etc.

            String dateAddedToCatalog = "" + "\t";
            if (view.getCatalogAddedDate() != null) {
                dateAddedToCatalog = df.format(view.getCatalogAddedDate()) + "\t";
            }

            String pubmedId = "" + "\t";
            if (view.getPubmedId() != null) {pubmedId = view.getPubmedId().trim() + "\t";}

            String firstAuthor = "" + "\t";
            if (view.getAuthor() != null) {firstAuthor = view.getAuthor().trim() + "\t";}

            String date = "" + "\t";
            if (view.getPublicationDate() != null) {
                date = df.format(view.getPublicationDate()) + "\t";
            }

            String journal = "" + "\t";
            if (view.getJournal() != null) {
                journal = view.getJournal().trim() + "\t";
            }

            String link = "" + "\t";
            if (view.getLink() != null) {link = view.getLink().trim() + "\t";}

            String study = "" + "\t";
            if (view.getStudy() != null) { study = view.getStudy().trim() + "\t";}

            String diseaseTrait = "" + "\t";
            if (view.getDiseaseTrait() != null) { diseaseTrait = view.getDiseaseTrait().trim() + "\t";}

            String initialSampleSize = "" + "\t";
            if (view.getInitialSampleDescription() != null) {
                initialSampleSize = view.getInitialSampleDescription().trim() + "\t";
            }

            String replicateSampleSize = "" + "\t";
            if (view.getReplicateSampleDescription() != null) {
                replicateSampleSize = view.getReplicateSampleDescription().trim() + "\t";
            }

            String region = "" + "\t";
            if (region != null) {region = view.getRegion().trim() + "\t";}

            String reportedGenes = "" + "\t";
            if (view.getReportedGene() != null) {
                reportedGenes = view.getReportedGene().trim() + "\t";
            }

            String strongestSnpRiskAllele = "" + "\t";
            if (view.getStrongestSnpRiskAllele() != null) {
                strongestSnpRiskAllele = view.getStrongestSnpRiskAllele().trim() + "\t";
            }

            String snps = "" + "\t";
            if (view.getSnpRsid() != null) {snps = view.getSnpRsid().trim() + "\t";}


            String riskAlleleFrequency = "" + "\t";
            if (view.getRiskAlleleFrequency() != null) {
                riskAlleleFrequency = view.getRiskAlleleFrequency().trim() + "\t";
            }

            String pValue = "" + "\t";
            if (view.getpValue() != null) {pValue = view.getpValue().toString().trim() + "\t";}

            String pValueText = "" + "\t";
            if (view.getpValueQualifier() != null) {pValueText = view.getpValueQualifier().trim() + "\t";}

            String orBeta = "" + "\t";
            if (view.getOrBeta() != null) { orBeta = view.getOrBeta().toString().trim() + "\t";}

            String ciText = "" + "\t";
            if (view.getCi() != null && view.getCiQualifier() != null) {
                ciText = view.getCi() + " " + view.getCiQualifier() + "\t";
            }
            else if (view.getCi() != null) { ciText = view.getCi() + "\t";}

            else if (view.getCiQualifier() != null) {ciText = view.getCiQualifier() + "\t";}

            String platform = "" + "\t";
            if (view.getPlatform() != null) { platform = view.getPlatform().trim() + "\t";}

            String cnv = "";
            if (view.getCnv() == true) {
                cnv = "Y" + "\t";
            }
            else {cnv = "N" + "\t";}

            String associationId = "" + "\t";
            if (view.getAssociationId() != null) { associationId = view.getAssociationId().toString() + "\t";}

            String studyId = "" + "\t";
            if (view.getStudyId() != null) { studyId = view.getStudyId().toString() + "\t";}

            String resultPublished = "";
            if (view.getResultPublished() != null) {
                resultPublished = "Y" + "\t";
            }
            else {resultPublished = "N" + "\t";}

            line = dateAddedToCatalog + pubmedId + firstAuthor + date + journal + link + study + diseaseTrait +
                    initialSampleSize + replicateSampleSize + region + reportedGenes + strongestSnpRiskAllele + snps +
                    riskAlleleFrequency + pValue + pValueText + orBeta + ciText + platform + cnv + associationId +
                    studyId + resultPublished + "\n";


            serialisedViews.add(line);
        } // end for

        return serialisedViews;
    }

    // For each line
    public void createFileForNcbi(String fileName, List<String> serialisedViews) {

        // Create a file from the file name supplied
        File file = new File(fileName);

        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Create file header line
        String header = "";
        header = "DATE ADDED TO CATALOG" + "\t"
                + "PUBMEDID" + "\t"
                + "FIRST AUTHOR" + "\t"
                + "DATE" + "\t"
                + "JOURNAL" + "\t"
                + "LINK" + "\t"
                + "STUDY" + "\t"
                + "DISEASE/TRAIT" + "\t"
                + "INITIAL SAMPLE SIZE" + "\t"
                + "REPLICATION SAMPLE SIZE" + "\t"
                + "REGION" + "\t"
                + "REPORTED GENE(S)" + "\t"
                + "STRONGEST SNP-RISK ALLELE" + "\t"
                + "SNPS" + "\t"
                + "RISK ALLELE FREQUENCY" + "\t"
                + "P-VALUE" + "\t"
                + "P-VALUE (TEXT)" + "\t"
                + "OR OR BETA" + "\t"
                + "95% CI (TEXT)" + "\t"
                + "PLATFORM [SNPS PASSING QC]" + "\t"
                + "CNV" + "\t"
                + "GWASTUDIESSNPID" + "\t"
                + "GWASTUDYID" + "\t"
                + "RESULTPUBLISHED" + "\n";

        try {
            output.write(header);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Write each line
        for (String view : serialisedViews) {

            try {
                output.write(view);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
