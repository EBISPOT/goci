package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.service.FtpFileService;
import uk.ac.ebi.spot.goci.repository.CatalogExportRepository;
import uk.ac.ebi.spot.goci.export.CatalogSpreadsheetExporter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by emma on 17/03/15.
 *
 * @author emma
 *         <p>
 *         Scheduled component that runs daily and exports a file containing details of catalog studies and drops on
 *         NCBI ftp fpr mapping
 */
@Component
public class DailyNcbiExportTask {

    private CatalogExportRepository catalogExportRepository;

    private CatalogSpreadsheetExporter catalogSpreadsheetExporter;

    private FtpFileService ftpFileService;

    @Autowired
    public DailyNcbiExportTask(CatalogExportRepository catalogExportRepository,
                               CatalogSpreadsheetExporter catalogSpreadsheetExporter,
                               FtpFileService ftpFileService) {
        this.catalogExportRepository = catalogExportRepository;
        this.catalogSpreadsheetExporter = catalogSpreadsheetExporter;
        this.ftpFileService = ftpFileService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Scheduled for 00:15
    @Scheduled(cron = "0 15 0 * * *")
    public void dailyNcbiExport() throws IOException {

        // Create date stamped file
        String uploadDir =
                System.getProperty("java.io.tmpdir") + File.separator + "gwas_ncbi_export" + File.separator;

        DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
        String dateStamp = df.format(new Date());
        File outputFile = new File(uploadDir + dateStamp + "_gwas.txt");
        outputFile.getParentFile().mkdirs();

        // If at this stage we haven't got a file create one
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        getLog().info("Created file: " + outputFile);

        // Call methods to create NCBI spreadsheet
        String[][] data = catalogExportRepository.getNCBISpreadsheet();
        catalogSpreadsheetExporter.writeToFile(data, outputFile);

        // Check we have something in our output file
        if (outputFile.length() != 0) {
            getLog().info("Begin file upload to FTP...");
            ftpFileService.ftpFileUpload(outputFile);
        }
        else {
            getLog().error("File is empty");
        }
    }
}
