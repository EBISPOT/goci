package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.model.StudySampleDescription;
import uk.ac.ebi.spot.goci.curation.service.StudySampleDescriptionsDownloadService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 24/04/2015.
 *
 * @author emma
 *         <p>
 *         Controller designed to handle the download of all studies with sample description and ancestry data
 */
@Controller
@RequestMapping("/sampledescriptions")
public class StudySampleDesciptionsController {

    private StudySampleDescriptionsDownloadService studySampleDescriptionsDownloadService;

    @Autowired
    public StudySampleDesciptionsController(StudySampleDescriptionsDownloadService studySampleDescriptionsDownloadService) {
        this.studySampleDescriptionsDownloadService = studySampleDescriptionsDownloadService;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public void getStudiesSampleDescriptions(HttpServletResponse response) {

        Collection<StudySampleDescription> studySampleDescriptions =
                studySampleDescriptionsDownloadService.generateStudySampleDescriptions();

        // Create date stamped tsv download file
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String now = dateFormat.format(date);

        String fileName = "GWASAncestry".concat("-").concat(now).concat(".tsv");
        response.setContentType("text/tsv");
        response.setHeader("Content-Disposition", "attachement; filename=" + fileName);

        try {
            studySampleDescriptionsDownloadService.createDownloadFile(response.getOutputStream(),
                                                                      studySampleDescriptions);
        }
        catch (IOException e) {
            getLog().error("Cannot create ancestry download file");
            e.printStackTrace();
        }
    }
}