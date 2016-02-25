package uk.ac.ebi.spot.goci.ui.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by emma on 24/02/15.
 *
 * @author emma
 *         <p>
 *         Controller used to handle download of files and generation of stats
 */
@Controller
public class FileController {

    // These parameters are read from application.properties file
    @Value("${download.full}")
    private Resource fullFileDownload;

    @Value("${download.alternative}")
    private Resource alternativeFileDownload;

    @Value("${download.studies}")
    private Resource studiesFileDownload;

    @Value("${download.studiesAlternative}")
    private Resource alternativeStudiesDownload;

    @Value("${download.NCBI}")
    private Resource fullFileDownloadNcbi;

    @Value("${catalog.stats.file}")
    private Resource catalogStatsFile;

    @Value("${download.ensemblmapping}")
    private Resource ensemblMappingFileDownload;

    @RequestMapping(value = "api/search/downloads/full",
                    method = RequestMethod.GET)
    public void getFullDownload(HttpServletResponse response) throws IOException {
        if (fullFileDownload.exists()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName = "gwas_catalog_v1.0-associations-downloaded_".concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            InputStream inputStream = null;
            inputStream = fullFileDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }
    }

    @RequestMapping(value = "api/search/downloads/studies",
                    method = RequestMethod.GET)
    public void getStudiesDownload(HttpServletResponse response) throws IOException {
        if (studiesFileDownload.exists()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName = "gwas_catalog_v1.0-studies-downloaded_".concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            InputStream inputStream = null;
            inputStream = studiesFileDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }
    }


    @RequestMapping(value = "api/search/downloads/alternative",
                    method = RequestMethod.GET,
                    produces = MediaType.TEXT_PLAIN_VALUE)
    public void getAlternativeDownload(HttpServletResponse response) throws IOException {
        if (alternativeFileDownload.exists()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName = "gwas_catalog_v1.0.1-associations-downloaded_".concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            InputStream inputStream = null;
            inputStream = alternativeFileDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }
    }

    @RequestMapping(value = "api/search/downloads/studies_alternative",
                    method = RequestMethod.GET)
    public void getAlternativeStudiesDownload(HttpServletResponse response) throws IOException {
        if (alternativeStudiesDownload.exists()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName = "gwas_catalog_v1.0.1-studies-downloaded_".concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            InputStream inputStream = null;
            inputStream = alternativeStudiesDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }
    }


    @RequestMapping(value = "api/search/downloads/full_NCBI",
                    method = RequestMethod.GET,
                    produces = MediaType.TEXT_PLAIN_VALUE)
    public void getFullNcbiDownload(HttpServletResponse response) throws IOException {

        if (fullFileDownloadNcbi.exists()) {

            InputStream inputStream = null;
            inputStream = fullFileDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }

    }

    @RequestMapping(value = "api/search/stats", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Map<String, Object> getCatalogStats() {
        Map<String, Object> response = new HashMap<>();

        String releasedate;
        String studycount;
        String snpcount;
        String associationcount;
        String genebuild;
        String dbsnpbuild;

        Properties properties = new Properties();
        try {
            properties.load(catalogStatsFile.getInputStream());
            releasedate = properties.getProperty("releasedate");
            studycount = properties.getProperty("studycount");
            snpcount = properties.getProperty("snpcount");
            associationcount = properties.getProperty("associationcount");
            genebuild = properties.getProperty("genomebuild");
            dbsnpbuild = properties.getProperty("dbsnpbuild");

            response.put("date", releasedate);
            response.put("studies", studycount);
            response.put("snps", snpcount);
            response.put("associations", associationcount);
            response.put("genebuild", genebuild);
            response.put("dbsnpbuild", dbsnpbuild);

        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: failed to read pubmed.properties resource", e);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: you must provide a integer query interval " +
                            "in minutes (pubmed.query.interval.mins)", e);
        }

        return response;
    }


    @RequestMapping(value = "api/search/downloads/ensembl_mapping",
                    method = RequestMethod.GET,
                    produces = MediaType.TEXT_PLAIN_VALUE)
    public void getEnsemblMappingDownload(HttpServletResponse response) throws IOException {

        if (ensemblMappingFileDownload.exists()) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String now = dateFormat.format(date);

            String fileName = "gwas_catalog_ensembl_mapping_v1.0-downloaded_".concat(now).concat(".tsv");
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

            InputStream inputStream = null;
            inputStream = ensemblMappingFileDownload.getInputStream();

            OutputStream outputStream;
            outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

        }
        else {
            throw new FileNotFoundException();
        }

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "File not found for download")
    @ExceptionHandler(FileNotFoundException.class)
    public void FileNotFoundException(FileNotFoundException fileNotFoundException) {
    }
}
