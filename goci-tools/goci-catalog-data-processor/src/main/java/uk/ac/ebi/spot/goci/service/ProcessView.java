package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CatalogSummaryView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 17/02/15.
 *
 * @author emma
 */
@Service
public class ProcessView {

    private NCBICatalogService ncbiCatalogService;

    @Autowired
    public ProcessView(NCBICatalogService ncbiCatalogService) {
        this.ncbiCatalogService = ncbiCatalogService;
    }

    public List<String> serialiseViews() {

        Collection<CatalogSummaryView> views = ncbiCatalogService.getCatalogSummaryViewsSendToNcbi();
        List<String> viewAsStrings = new ArrayList<String>();

        for (CatalogSummaryView view : views) {

            String line = "";

            String dateAddedToCatalog = view.getCatalogAddedDate().toString() + "\t";
            String pubmedId = view.getPubmedId() + "\t";
            String firstAuthor = view.getAuthor() + "\t";
            String date = view.getPublicationDate().toString() + "\t";
            String journal = view.getJournal() + "\t";
            String link = view.getLink() + "\t";
            String study = view.getStudy() + "\t";
            String diseaseTrait = view.getDiseaseTrait() + "\t";
            String initialSampleSize = view.getInitialSampleDescription() + "\t";
            String replicateSampleSize = view.getReplicateSampleDescription() + "\t";
            String region = view.getRegion() + "\t";
            String reportedGenes = view.getReportedGene() + "\t";
            String strongestSnpRiskAllele = view.getStrongestSnpRiskAllele() + "\t";
            String snps = view.getSnpRsid() + "\t";
            String riskAlleleFrequency = view.getRiskAlleleFrequency() + "\t";
            String pValue = view.getpValue() + "\t";
            String pValueText = view.getpValueQualifier() + "\t";
            String orBeta = view.getOrBeta() + "\t";
            String ciText = view.getCi() + " " + view.getCiQualifier() + "\t";
            String platform = view.getPlatform() + "\t";

            String cnv;
            if (view.getCnv() == true) {
                cnv = "Y" + "\t";
            }
            else {cnv = "N" + "\t";}

            String associationId = view.getAssociationId().toString() + "\t";
            String studyId = view.getStudyId().toString() + "\t";

            String resultPublished;
            if (view.getResultPublished() != null) {
                resultPublished = "Y" + "\t";
            }
            else {resultPublished = "N" + "\t";}

            line = dateAddedToCatalog + pubmedId + firstAuthor + date + journal + link + study + diseaseTrait +
                    initialSampleSize + replicateSampleSize + region + reportedGenes + strongestSnpRiskAllele + snps +
                    riskAlleleFrequency + pValue + pValueText + orBeta + ciText + platform + cnv + associationId +
                    studyId + resultPublished + "\n";


            viewAsStrings.add(line);
        } // end for

        return viewAsStrings;
    }


    public void createFileForNcbi(String fileName, List<String> viewsAsStrings) {
        File file = new File(fileName);
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        for (String view: viewsAsStrings){

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
