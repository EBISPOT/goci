package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.repository.CatalogExportRepository;
import uk.ac.ebi.spot.goci.service.SpreadsheetProcessor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emma on 17/03/15.
 */
@Component
public class DailyNcbiExportTask {

    private CatalogExportRepository catalogExportRepository;

    private SpreadsheetProcessor spreadsheetProcessor;

    @Autowired
    public DailyNcbiExportTask(CatalogExportRepository catalogExportRepository,
                               SpreadsheetProcessor spreadsheetProcessor) {
        this.catalogExportRepository = catalogExportRepository;
        this.spreadsheetProcessor = spreadsheetProcessor;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Scheduled(cron = "0 22 17 * * *")
    public void dailyNcbiExport() throws IOException {

        // Create file
        String uploadDir =
                System.getProperty("java.io.tmpdir") + File.separator + "gwas_ncbi_export" + File.separator;

        DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
        String dateStamp = df.format(new Date());
        File outputFile = new File(uploadDir + dateStamp + "_gwas.txt");
        outputFile.getParentFile().mkdirs();

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        getLog().info("Created file: " + outputFile);

        // Call methods to create NCBI spreadsheet
        String[][] data = catalogExportRepository.getNCBISpreadsheet();
        spreadsheetProcessor.writeToFile(data, outputFile);

        // Check we have something in our output file
        if (outputFile.length() != 0) {

            // TODO Copy to NCBI ftp

        }
        else {
            getLog().error("File is empty");
        }


    }


}
