package uk.ac.ebi.spot.goci.curation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.model.deposition.Submission;

import java.util.Map;

@Controller
public class SplitSubmissionController {

    private final DepositionSubmissionService submissionService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    public SplitSubmissionController(@Autowired DepositionSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/imported_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String importedSubmissionsPage(Model model) {
        Map<String, Submission> submissionList = submissionService.getImportedSubmissions();
        model.addAttribute("submissions", submissionList.values());
        return "view_submissions";
    }

    @RequestMapping(value = "/other_submissions", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String otherSubmissionsPage(Model model) {
        Map<String, Submission> submissionList = submissionService.getOtherSubmissions();
        model.addAttribute("submissions", submissionList.values());
        return "view_submissions";
    }
}
