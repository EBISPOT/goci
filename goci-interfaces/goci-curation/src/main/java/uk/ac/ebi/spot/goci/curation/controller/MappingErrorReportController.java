package uk.ac.ebi.spot.goci.curation.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by emma on 27/01/2016.
 *
 * @author emma
 */
@Controller
@RequestMapping("/mappingerrorreport")
public class MappingErrorReportController {

    // These parameters are read from application.properties file
    @Value("${download.report}")
    private Resource report;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public void getReport(HttpServletResponse response, Model model) throws IOException {

        if (report.exists()) {

            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", "attachement; filename=" + report.getFilename());

            InputStream inputStream = null;
            inputStream = report.getInputStream();

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
}
