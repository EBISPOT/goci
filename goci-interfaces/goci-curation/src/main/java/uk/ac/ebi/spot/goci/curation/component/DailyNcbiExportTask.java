package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.spot.goci.curation.service.FtpFileService;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.export.CatalogSpreadsheetExporter;
import uk.ac.ebi.spot.goci.repository.CatalogExportRepository;
import uk.ac.ebi.spot.goci.service.GOCIMailService;

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
@Controller
public class DailyNcbiExportTask {

    private CatalogExportRepository catalogExportRepository;

    private CatalogSpreadsheetExporter catalogSpreadsheetExporter;

    private FtpFileService ftpFileService;

    private MailService mailService;

    @Autowired
    public DailyNcbiExportTask(CatalogExportRepository catalogExportRepository,
                               CatalogSpreadsheetExporter catalogSpreadsheetExporter,
                               FtpFileService ftpFileService,
                               MailService mailService) {
        this.catalogExportRepository = catalogExportRepository;
        this.catalogSpreadsheetExporter = catalogSpreadsheetExporter;
        this.ftpFileService = ftpFileService;
        this.mailService = mailService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Scheduled for 00:15 and link to upload the file if the FTP is giving issue about connection timeout. GOCI-2049
    @Scheduled(cron = "0 15 0 * * SUN")
    public void scheduleNcbiExport() throws IOException {
        String emailSubject = "FTP NCBI: File upload with success";
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

            try {
                ftpFileService.ftpFileUpload(outputFile);
            } catch (Exception exception) {
                emailSubject="FTP NCBI: File upload FAILED!!";
            }

            mailService.sendNcbiFTPUploadEmail(emailSubject);
        }
        else {
            getLog().error("File is empty");
        }
    }

    @RequestMapping( value = "dailyNcbiExport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<String> manualNcbiExport() {
        String result = "";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        try {
            scheduleNcbiExport();
        } catch (Exception exception) {
            // Nothing to do. Mail will be sent in both cases.
        }

        result = new StringBuilder("{\"success\":\"Sent an e-mail to Devops").append("\"}").toString();
        return new ResponseEntity<>(result,responseHeaders, HttpStatus.OK);

    }

}
