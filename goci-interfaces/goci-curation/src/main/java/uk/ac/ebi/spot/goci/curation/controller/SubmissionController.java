package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.PrintWriter;
import java.io.StringWriter;
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
    @Autowired
    private ObjectMapper mapper;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    public SubmissionController(@Autowired DepositionSubmissionService submissionService,
                                @Autowired CurrentUserDetailsService currentUserDetailsService) {
        this.submissionService = submissionService;
        this.currentUserDetailsService = currentUserDetailsService;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allSubmissionsPage(Model model) {
        Map<String, Submission> submissionList = submissionService.getSubmissions();
        model.addAttribute("submissions", submissionList.values());
        return "view_submissions";
    }

    @RequestMapping(value = "/{submissionId}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String viewSubmission(Model model, @PathVariable String submissionId) {

        DepositionSubmission depositionSubmission = getSubmission(submissionId);
        Submission submission = submissionService.buildSubmission(depositionSubmission);
        model.addAttribute("submission", submission);
        model.addAttribute("submissionData", depositionSubmission);
        model.addAttribute("submissionError", submissionService.checkSubmissionErrors(depositionSubmission));
        try {
            model.addAttribute("submissionString", mapper.writeValueAsString(depositionSubmission));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "single_submission";
    }

    private Map<String, Submission> getSubmissions() {
        Map<String, Submission> submissionList = submissionService.getSubmissions();
        //}
        return submissionList;
    }

    private DepositionSubmission getSubmission(String submissionID) {
        return submissionService.getSubmission(submissionID);
    }

    @CrossOrigin
    @RequestMapping(value = "/{submissionID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String importSubmission(@PathVariable String submissionID, Model model, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        List<String> statusMessages = new ArrayList<>();
        try {
            Map<String, Submission> submissionList = submissionService.getSubmissions();
            DepositionSubmission depositionSubmission = submissionService.getSubmission(submissionID);
            Submission submission = submissionList.get(submissionID);
            SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
            statusMessages = submissionService.importSubmission(depositionSubmission, currentUser);

            submission.setStatus("IMPORTED");
            model.addAttribute("submissions", submissionList.values());
            redirectAttributes.addFlashAttribute("changesSaved", statusMessages);
        }catch(Exception e){
            e.printStackTrace();
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            statusMessages.add(stringWriter.getBuffer().toString());
        }
        return "redirect:/submissions/" + submissionID;
    }

    @CrossOrigin
    @RequestMapping(value = "/{submissionID}/testError", produces = MediaType.TEXT_HTML_VALUE, method =
            RequestMethod.POST)
    public String importSubmissionError(@PathVariable String submissionID, Model model, HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        StringWriter stringWriter = new StringWriter();
        new Throwable().printStackTrace(new PrintWriter(stringWriter));
        List<String> statusMessages = Arrays.asList(new String[]{"Error 1", "Error 2", stringWriter.getBuffer().toString()});
        redirectAttributes.addFlashAttribute("changesSaved", statusMessages);
        return "redirect:/submissions/" + submissionID;
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
