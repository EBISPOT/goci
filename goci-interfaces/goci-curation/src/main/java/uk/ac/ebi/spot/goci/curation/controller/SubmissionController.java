package uk.ac.ebi.spot.goci.curation.controller;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.deposition.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.Submission;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Controller
@RequestMapping("/submissions")
public class SubmissionController {

    private final DepositionSubmissionService submissionService;
    private final CurrentUserDetailsService currentUserDetailsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private RestTemplate template;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    public SubmissionController(@Autowired DepositionSubmissionService submissionService,
                                @Autowired CurrentUserDetailsService currentUserDetailsService) {
        this.submissionService = submissionService;
        this.currentUserDetailsService = currentUserDetailsService;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allSubmissionsPage(Model model) {
        Map<String, Submission> submissionList = getSubmissions();
        model.addAttribute("submissions", submissionList.values());
        return "view_submissions";
    }

    @RequestMapping(value = "/{submissionId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewSubmission(Model model, @PathVariable String submissionId) {

        DepositionSubmission depositionSubmission = getSubmission(submissionId);
        Submission submission = buildSubmission(depositionSubmission);
        model.addAttribute("submission", submission);
        model.addAttribute("submissionData", depositionSubmission);
        return "single_submission";
    }

    private Map<String, Submission> getSubmissions() {
        Map<String, Submission> submissionList = new TreeMap<>();
        int i = 0;
        Map<String, Integer> params = new HashMap<>();
        params.put("page", i);
        String response = template.getForObject(depositionIngestURL + "/submissions?page={page}", String.class,
                params);

//        DepositionSubmissionListWrapper submissions =
//                template.getForObject(depositionIngestURL + "/submissions" + "?page={page}",
//                        DepositionSubmissionListWrapper.class, params);
//        while (i < submissions.getPage().getTotalPages()) {
//            for (DepositionSubmission submission : submissions.getWrapper().getSubmissions()) {
        DepositionSubmission[] submissions =
                template.getForObject(depositionIngestURL + "/submissions" + "?page={page}", DepositionSubmission[].class, params);
        for (DepositionSubmission submission : submissions) {
            Submission testSub = buildSubmission(submission);
            submissionList.put(testSub.getId(), testSub);
            params.put("page", ++i);
            //      submissions = template.getForObject(depositionIngestURL + "/submissions?page={page}",
            //              DepositionSubmissionListWrapper.class, params);
        }
        //}
        return submissionList;
    }

    private Submission buildSubmission(DepositionSubmission depositionSubmission){
        Submission testSub = new Submission();
        testSub.setId(depositionSubmission.getSubmissionId());
        testSub.setPubMedID(depositionSubmission.getPublication().getPmid());
        testSub.setAuthor(depositionSubmission.getPublication().getFirstAuthor());
        testSub.setCurator(depositionSubmission.getCreated().getUser().getName());
        testSub.setStatus(depositionSubmission.getStatus());
        testSub.setTitle(depositionSubmission.getPublication().getTitle());
        testSub.setCreated(depositionSubmission.getCreated().getTimestamp().toString(DateTimeFormat.shortDateTime()));
        testSub.setPublicationStatus(depositionSubmission.getPublication().getStatus());
        testSub.setSubmissionType(submissionService.getSubmissionType(depositionSubmission));
        return testSub;
    }

    private DepositionSubmission getSubmission(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        String response =
                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", String.class, params);
        DepositionSubmission submission =
                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", DepositionSubmission.class,
                        params);

        return submission;
    }

    @CrossOrigin
    @RequestMapping(value = "/{submissionID}/import", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String importSubmission(@PathVariable String submissionID, Model model, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        Map<String, Submission> submissionList = getSubmissions();
        DepositionSubmission depositionSubmission = getSubmission(submissionID);
        Submission submission = submissionList.get(submissionID);
        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        List<String> statusMessages = submissionService.importSubmission(depositionSubmission, currentUser);

        submission.setStatus("IMPORTED");
        model.addAttribute("submissions", submissionList.values());
        redirectAttributes.addFlashAttribute("changesSaved", statusMessages);

        return "redirect:/submissions";
    }

    @CrossOrigin
    @RequestMapping(value = "/update/{submissionID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<Submission> updateSubmission(@PathVariable String submissionID,
                                                       @ModelAttribute Submission submission, Model model,
                                                       HttpServletRequest request) {
        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        submission = submissionService.updateSubmission(submission, currentUser);
        ResponseEntity<Submission> response = new ResponseEntity<>(submission, HttpStatus.OK);
        return response;
    }
}
