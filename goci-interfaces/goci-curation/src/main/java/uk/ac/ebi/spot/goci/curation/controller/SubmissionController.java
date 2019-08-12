package uk.ac.ebi.spot.goci.curation.controller;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uk.ac.ebi.spot.goci.service.StudyService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/submissions")
public class SubmissionController {

    private final PublicationService publicationService;
    private final StudyOperationsService studyOperationsService;
    private final CuratorRepository curatorRepository;
    private final CurationStatusRepository statusRepository;
    private final CurrentUserDetailsService currentUserDetailsService;
    private final Curator levelTwoCurator;
    private final CurationStatus levelOneCurationComplete;
    private final CurationStatus levelOnePlaceholderStatus;
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private RestTemplate template;

    @Value("${deposition.uri}")
    private String depositionURL;

    public SubmissionController(@Autowired PublicationService publicationService,
                                @Autowired StudyOperationsService studyOperationsService,
                                @Autowired CuratorRepository curatorRepository,
                                @Autowired CurationStatusRepository statusRepository,
                                @Autowired CurrentUserDetailsService currentUserDetailsService) {

        this.publicationService = publicationService;
        this.studyOperationsService = studyOperationsService;
        this.curatorRepository = curatorRepository;
        this.statusRepository = statusRepository;
        this.currentUserDetailsService = currentUserDetailsService;
        levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");
        levelOnePlaceholderStatus = statusRepository.findByStatus("Awaiting Literatur");

    }
    @RequestMapping(value = "/new", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    public String allSubmissionsPage(Model model) {
        List<Submission> submissionList = getSubmissions();
        model.addAttribute("submissions", submissionList);
        return "view_submissions";
    }

    private List<Submission> getSubmissions() {
        List<Submission> submissionList = new ArrayList<>();
        int i = 0;
        Map<String, Integer> params = new HashMap<>();
        params.put("page", i);
        String response = template.getForObject(depositionURL + "/submissions?page={page}", String.class, params);
        DepositionSubmissionList submissions = template.getForObject(depositionURL + "/submissions?page={page}",
                DepositionSubmissionList.class, params);
        while(i < submissions.getPage().getTotalPages()){
            for(DepositionSubmission submission: submissions.getWrapper().getSubmissions()) {
                Submission testSub = new Submission();
                testSub.setId(submission.getSubmissionId());
                testSub.setPubMedID(submission.getPublication().getPmid());
                testSub.setAuthor(submission.getPublication().getFirstAuthor());
                testSub.setCurator(submission.getCreated().getUser().getName());
                testSub.setStatus(submission.getStatus());
                testSub.setTitle(submission.getPublication().getTitle());
                testSub.setCreated(submission.getCreated().getTimestamp().toString(DateTimeFormat.shortDateTime()));
                submissionList.add(testSub);
                params.put("page", ++i);
                submissions = template.getForObject(depositionURL + "/submissions?page={page}",
                        DepositionSubmissionList.class, params);
            }
        }
        return submissionList;
    }

    private DepositionSubmission getSubmission(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        String response = template.getForObject(depositionURL + "/submissions/{submissionID}", String.class, params);
        DepositionSubmission submission = template.getForObject(depositionURL + "/submissions/{submissionID}", DepositionSubmission.class, params);
        return submission;
    }

    @CrossOrigin
    @RequestMapping(value = "/import/{submissionID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
    public String importSubmission(@PathVariable String submissionID, @ModelAttribute Submission submission, Model model, HttpServletRequest request) {
        DepositionSubmission depositionSubmission = getSubmission(submissionID);
        StatusAssignment newStatus = new StatusAssignment();
        newStatus.setStatusId(levelOneCurationComplete.getId());

        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        Publication publication = publicationService.findByPumedId(submission.getPubMedID());

        List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<DepositionAssociationDto> associations = depositionSubmission.getAssociations();
        List<DepositionSampleDto> samples = depositionSubmission.getSamples();
        List<DepositionFileUploadDto> files = depositionSubmission.getFiles();
        List<DepositionNoteDto> notes = depositionSubmission.getNotes();

        if (studies != null) {
            for (DepositionStudyDto studyDto : studies) {
                Study study = studyDto.buildStudy();
                study.setPublicationId(publication);
                study = studyOperationsService.createStudy(study, currentUser);
                            studyOperationsService.assignStudyStatus(study,
                                    newStatus, currentUser);
                Housekeeping housekeeping = study.getHousekeeping();
                CurationStatus status = housekeeping.getCurationStatus();
                status.setStatus("Level 1 curation done");
                housekeeping.setCurator(levelTwoCurator);
                studyOperationsService.updateHousekeeping(housekeeping, study, currentUser);
            }
            depositionSubmission.setStatus("IMPORTED");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);
            template.put(depositionURL + "/submissions/{submissionID}", depositionSubmission, params);
        }
        List<Submission> submissionList = getSubmissions();
        model.addAttribute("submissions", submissionList);
        return "view_submissions";
    }

    @CrossOrigin
    @RequestMapping(value = "/update/{submissionID}", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<Submission> updateSubmission(@PathVariable String submissionID,
                                                       @ModelAttribute Submission submission,
                                                       Model model, HttpServletRequest request) {
        String pubMedID = submission.getPubMedID();
        Publication publication = publicationService.findByPumedId(pubMedID);
        Collection<Study> studies = publication.getStudies();
        SecureUser currentUser = currentUserDetailsService.getUserFromRequest(request);
        for(Study study: studies){
            if(submission.getStatus().equals("STARTED")){
                Housekeeping houseKeeping = study.getHousekeeping();
                houseKeeping.setCurationStatus(levelOnePlaceholderStatus);
                studyOperationsService.updateHousekeeping(houseKeeping, study, currentUser);
            }
        }
        ResponseEntity<Submission> response = new ResponseEntity<>(submission, HttpStatus.OK);
        return response;
    }
}
