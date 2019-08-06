package uk.ac.ebi.spot.goci.curation.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.service.PublicationService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/submissions")
public class SubmissionController {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private StudyOperationsService studyOperationsService;

    @Autowired
    private CuratorRepository curatorRepository;

    @Autowired
    private CurationStatusRepository statusRepository;

    @Autowired
    private CurrentUserDetailsService currentUserDetailsService;

    @Value("${deposition.uri}")
    private String deposotionURL;

    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allSubmissionsPage(Model model) {
        List<Submission> submissionList = getSubmissions();
        model.addAttribute("submissions", submissionList);
        return "view_submissions";
    }

    private List<Submission> getSubmissions() {
        List<Submission> submissionList = new ArrayList<>();
        RestTemplate template = new RestTemplate();
        String response = template.getForObject(deposotionURL + "/submissions", String.class);
        DepositionSubmissionList list = template.getForObject(deposotionURL + "/submissions", DepositionSubmissionList.class);
        for (DepositionSubmission submission : list.getWrapper().getSubmissions()) {
            Submission testSub = new Submission();
            testSub.setId(submission.getSubmissionId());
            testSub.setPubMedID(submission.getPublication().getPmid());
            testSub.setAuthor(submission.getPublication().getFirstAuthor());
            testSub.setCurator(submission.getCreated().getUser().getName());
            testSub.setStatus(submission.getStatus());
            testSub.setTitle(submission.getPublication().getTitle());
            testSub.setCreated(submission.getCreated().getTimestamp().toString(DateTimeFormat.shortDateTime()));
            submissionList.add(testSub);
        }
        return submissionList;
    }

    private DepositionSubmission getSubmission(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        RestTemplate template = new RestTemplate();
        String response = template.getForObject(deposotionURL + "/submissions/{submissionID}", String.class, params);
        DepositionSubmission submission = template.getForObject(deposotionURL + "/submissions/{submissionID}", DepositionSubmission.class, params);
        return submission;
    }

    @CrossOrigin
    @RequestMapping(value = "/import/{submissionID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String importSubmission(@PathVariable String submissionID, @ModelAttribute Submission submission, Model model, HttpServletRequest request) {
        DepositionSubmission depositionSubmission = getSubmission(submissionID);
        Curator levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        CurationStatus levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");
        StatusAssignment newStatus = new StatusAssignment();
        newStatus.setStatusId(levelOneCurationComplete.getId());

        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        Publication publication = publicationService.findByPumedId(submission.getPubMedID());

        Collection<DepositionStudyDto> studies = depositionSubmission.getStudies();
        if (studies != null) {
            for (DepositionStudyDto studyDto : studies) {
                Study study = studyDto.buildStudy();
                study.setPublicationId(publication);
                studyOperationsService.createStudy(study, currentUser);
                //            studyOperationsService.assignStudyStatus(study,
                //                    newStatus, currentUser);
                Housekeeping housekeeping = study.getHousekeeping();
                CurationStatus status = housekeeping.getCurationStatus();
                status.setStatus("Level 1 curation done");
                housekeeping.setCurator(levelTwoCurator);
                //            studyOperationsService.updateHousekeeping(housekeeping, study, currentUser);
            }
            depositionSubmission.setStatus("IMPORTED");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);
//        RestTemplate template = new RestTemplate();
//        template.put(deposotionURL + "/submissions/{submissionID}", depositionSubmission, params);
        }
        List<Submission> submissionList = getSubmissions();
        model.addAttribute("submissions", submissionList);
        return "view_submissions";
    }
}
