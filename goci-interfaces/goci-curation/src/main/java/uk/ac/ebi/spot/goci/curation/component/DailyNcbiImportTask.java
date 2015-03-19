package uk.ac.ebi.spot.goci.curation.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.service.FtpFileService;
import uk.ac.ebi.spot.goci.repository.CatalogImportRepository;
import uk.ac.ebi.spot.goci.service.SpreadsheetProcessor;

import java.io.File;
import java.io.IOException;

/**
 * Created by emma on 19/03/15.
 *
 * @author emma
 *         <p>
 *         Scheduled component that runs daily and imports the NCBI annotated file
 */
@Component
public class DailyNcbiImportTask {

    private CatalogImportRepository catalogImportRepository;

    private SpreadsheetProcessor spreadsheetProcessor;

    private FtpFileService ftpFileService;

    @Autowired
    public DailyNcbiImportTask(CatalogImportRepository catalogImportRepository,
                               SpreadsheetProcessor spreadsheetProcessor,
                               FtpFileService ftpFileService) {
        this.catalogImportRepository = catalogImportRepository;
        this.spreadsheetProcessor = spreadsheetProcessor;
        this.ftpFileService = ftpFileService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void dailyNcbiImport() throws IOException {

        // Get annotated file
        File annotatedFile = ftpFileService.ftpDownload();

        // Load into database if we have a file
        if (annotatedFile.length() != 0) {
            String[][] data = spreadsheetProcessor.readFromFile(annotatedFile);
            catalogImportRepository.loadNCBIMappedData(data);
        }

        else {
            getLog().error("File returned from NCBI is empty");
        }
    }
}
