package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.util.*;

@Service
public class DepositionSubmissionService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private final StudyOperationsService studyOperationsService;
    private final PublicationService publicationService;
    private final CurationStatus levelOnePlaceholderStatus;

    @Autowired
    private RestTemplate template;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    @Value("${deposition.ingest.uri.v2}")
    private String depositionIngestURLV2;

    @Value("${deposition.token}")
    private String depositionToken;

    private SubmissionImportProgressService submissionImportProgressService;

    public DepositionSubmissionService(@Autowired PublicationService publicationService,
                                       @Autowired StudyOperationsService studyOperationsService,
                                       @Autowired CurationStatusRepository statusRepository,
                                       @Autowired SubmissionImportProgressService submissionImportProgressService) {
        this.publicationService = publicationService;
        this.studyOperationsService = studyOperationsService;
        this.submissionImportProgressService = submissionImportProgressService;

        levelOnePlaceholderStatus = statusRepository.findByStatus("Awaiting Literature");
    }

    public Map<String, String> getSubmissionPubMedIds() {
        Map<String, String> pubmedMap = new HashMap<>();
        Map<String, Submission> submissionMap = getSubmissionsBasic();
        submissionMap.entrySet().stream().filter(e -> e.getValue().getPubMedID() != null).forEach(e -> pubmedMap.put(e.getValue().getPubMedID(), e.getKey()));
        return pubmedMap;
    }

    public Map<String, Submission> getSubmissionsBasic() {
        String url = "/submission-envelopes";
        return getSubmissions(url);
    }

    public Map<String, Submission> getSubmissions() {
        String url = "/submissions?page={page}";
        return getSubmissions(url);
    }

    public Map<String, Submission> getSubmissionsByStatus(String status) {
        String url = "/submissions?status=" + status;
        return getSubmissions(url);
    }

    public Map<String, Submission> getReadyToImportSubmissions() {
        String url = "/submissions?status=READY_TO_IMPORT";
        return getSubmissions(url);
    }

    public Map<String, Submission> getOtherSubmissions() {
        String url = "/submissions?status=OTHER";
        return getSubmissions(url);
    }

    public Map<String, Submission> getSubmissionsById(List<String> submissionIds) {
        Map<String, Submission> submissionList = new TreeMap<>();
        for (String sId : submissionIds) {
            submissionList.put(sId, buildSubmission(getSubmission(sId)));
        }
        return submissionList;
    }

    private Map<String, Submission> getSubmissions(String url) {

        Map<String, Submission> submissionList = new TreeMap<>();
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);
            DepositionSubmission[] submissions =
                    template.getForObject(depositionIngestURL + url, DepositionSubmission[].class, params);
            Arrays.stream(submissions).forEach(s -> {
                Submission testSub = buildSubmission(s);
                submissionList.put(testSub.getId(), testSub);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return submissionList;
    }

    public DepositionSubmission getSubmission(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        DepositionSubmission submission =
                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", DepositionSubmission.class,
                        params);

        return submission;
    }

    public DepositionSubmission getSubmissionForImport(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        DepositionSubmission submission =
                template.getForObject(depositionIngestURLV2 + "/submissions/{submissionID}", DepositionSubmission.class,
                        params);

        return submission;
    }

    public List<DepositionStudyDto> getStudiesForSubmission(String submissionID, int page) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionId", submissionID);
        params.put("page", Integer.toString(page));
        DepositionStudyDto[] studies = template.getForObject(depositionIngestURLV2 + "/studies?submissionId={submissionId}&page={page}", DepositionStudyDto[].class, params);
        return Arrays.asList(studies);
    }

    public DepositionProvenance getProvenance(String pmid) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("pmid", pmid);
            DepositionProvenance provenance = template.getForObject(depositionIngestURL + "/provenance?pmid={pmid}", DepositionProvenance.class, params);
            return provenance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Submission buildSubmission(DepositionSubmission depositionSubmission) {
        Submission testSub = new Submission();
        testSub.setId(depositionSubmission.getSubmissionId());
        testSub.setCurator(depositionSubmission.getCreated().getUser().getName());
        testSub.setStatus(depositionSubmission.getStatus());
        testSub.setCreated(depositionSubmission.getCreated().getTimestamp().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        testSub.setImportStatus(Submission.ImportStatus.NOT_READY);
        testSub.setSubmissionType(DepositionUtil.getSubmissionType(depositionSubmission));
        if (depositionSubmission.getBodyOfWork() != null) {
            BodyOfWorkDto bodyOfWork = depositionSubmission.getBodyOfWork();
            if (bodyOfWork.getPmids() != null && bodyOfWork.getPmids().size() != 0) {
                testSub.setPubMedID(String.join(",", bodyOfWork.getPmids()));
            }
            if (bodyOfWork.getFirstAuthor() != null) {
                if (bodyOfWork.getFirstAuthor().getGroup() != null) {
                    testSub.setAuthor(bodyOfWork.getFirstAuthor().getGroup());
                } else {
                    testSub.setAuthor(bodyOfWork.getFirstAuthor().getFirstName() + ' ' +
                            bodyOfWork.getFirstAuthor().getLastName());
                }
            }
            testSub.setTitle(bodyOfWork.getTitle());
            testSub.setPublicationStatus(bodyOfWork.getStatus());
            testSub.setDoi(bodyOfWork.getPreprintServerDOI());
            if (testSub.getSubmissionType().equals(Submission.SubmissionType.UNKNOWN)) {
                testSub.setStatus("REVIEW");
            }
        } else if (depositionSubmission.getPublication() != null) {
            DepositionPublication publication = depositionSubmission.getPublication();
            testSub.setPubMedID(publication.getPmid());
            testSub.setAuthor(publication.getFirstAuthor());
            testSub.setTitle(publication.getTitle());
            testSub.setPublicationStatus(publication.getStatus());
            testSub.setDoi(publication.getDoi());
            if (testSub.getSubmissionType().equals(Submission.SubmissionType.UNKNOWN)) {
                testSub.setStatus("REVIEW");
            }
        }
        boolean importInProgress = submissionImportProgressService.importInProgress(depositionSubmission.getSubmissionId());
        if (importInProgress) {
            testSub.setStatus("IMPORT_IN_PROGRESS");
        }
        if (testSub.getStatus().equalsIgnoreCase("SUBMITTED") &&
                !testSub.getSubmissionType().equals(Submission.SubmissionType.PRE_PUBLISHED) &&
                !testSub.getSubmissionType().equals(Submission.SubmissionType.UNKNOWN)) {
            testSub.setImportStatus(Submission.ImportStatus.READY);
        }
        return testSub;
    }

    public Submission updateSubmission(Submission submission, SecureUser currentUser) {
        String pubMedID = submission.getPubMedID();
        Publication publication = publicationService.findByPumedId(pubMedID);
        Collection<Study> studies = publication.getStudies();
        for (Study study : studies) {
            if (submission.getStatus().equals("STARTED")) {
                Housekeeping houseKeeping = study.getHousekeeping();
                houseKeeping.setCurationStatus(levelOnePlaceholderStatus);
                studyOperationsService.updateHousekeeping(houseKeeping, study, currentUser);
            }
        }
        return submission;
    }

    public String checkSubmissionErrors(DepositionSubmission submission) {
        Submission.SubmissionType type = DepositionUtil.getSubmissionType(submission);
        if (type.equals(Submission.SubmissionType.UNKNOWN)) {
            boolean hasSumStats = false;
            boolean hasMetadata = false;
            boolean hasAssociations = false;
            for (DepositionStudyDto studyDto : submission.getStudies()) {
                if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
                    hasSumStats = true;
                }
            }
            for (DepositionSampleDto sampleDto : submission.getSamples()) {
                if (sampleDto.getStage() != null) {
                    hasMetadata = true;
                }
            }
            for (DepositionAssociationDto associationDto : submission.getAssociations()) {
                if (associationDto.getStudyTag() != null) {
                    hasAssociations = true;
                }
            }
            return "Has SumStats: " + hasSumStats + ", has metadata: " + hasMetadata + ", has associations: " + hasAssociations;
        }
        return null;
    }

    public Map<String, Submission> getSubmissionsForPMID(String pmid) {
        Map<String, Submission> submissionList = new TreeMap<>();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("pmid", pmid);
            DepositionSubmission[] submissions =
                    template.getForObject(depositionIngestURL + "/submissions?pmid={pmid}", DepositionSubmission[].class, params);
            Arrays.stream(submissions).forEach(s -> {
                Submission testSub = buildSubmission(s);
                submissionList.put(testSub.getId(), testSub);
            });

            return submissionList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return submissionList;
    }
}
