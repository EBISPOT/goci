package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.dto.SubmissionViewDto;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.curation.util.UriBuilder;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionAssociationListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPageInfo;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSampleListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.net.URI;
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

    @Value("${deposition.token}")
    private String depositionToken;

    private final Pageable pageable = new PageRequest(0, 3000, null);

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
        log.info("Start getting Submissions");
        Map<String, String> pubmedMap = new HashMap<>();
        Map<String, Submission> submissionMap = getSubmissionsBasic();
        submissionMap.entrySet().stream().filter(e -> e.getValue().getPubMedID() != null).forEach(e -> pubmedMap.put(e.getValue().getPubMedID(), e.getKey()));
        log.info("Finished getting Submissions");
        return pubmedMap;
    }

    public SubmissionViewDto getSubmissions() {
        String url = String.format("%s%s", depositionIngestURL, "/submissions?page={page}");
        URI targetUrl = UriBuilder.buildUrl(url, pageable);
        return getSubmissionsWithPagination(targetUrl);
    }

    public SubmissionViewDto getSubmissionsByStatus(String status, Pageable pageable) {
        String url = String.format("%s%s", depositionIngestURL, "/submissions");
        URI targetUrl = UriBuilder.buildUrl(url, pageable, status);
        return getSubmissionsWithPagination(targetUrl);
    }

    public SubmissionViewDto getSubmissionsById(List<String> submissionIds) {
        Map<String, Submission> submissionList = new TreeMap<>();
        for (String sId : submissionIds) {
            submissionList.put(sId, buildSubmission(getSubmission(sId)));
        }

        SubmissionViewDto submissionViewDto = SubmissionViewDto.builder()
                .submissionList(submissionList)
                .page(new DepositionPageInfo(0,0,0,0))
                .build();
        submissionViewDto.setPageIndexes();
        return submissionViewDto;
    }

    public Map<String, Submission> getSubmissionsBasic() {
        String url = "/submission-envelopes";
        return getSubmissions(url);
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

    private SubmissionViewDto getSubmissionsWithPagination(URI targetUrl) {
        Map<String, Submission> submissionList = new TreeMap<>();
        DepositionSubmissionDto depositionSubmissionDto = DepositionSubmissionDto.builder().build();
        try {
            log.info(targetUrl.toString());
            depositionSubmissionDto = template.getForObject(targetUrl, DepositionSubmissionDto.class);
            depositionSubmissionDto.getWrapper().getSubmissions().forEach(s -> {
                Submission submission = buildSubmission(s);
                submissionList.put(submission.getId(), submission);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        SubmissionViewDto submissionViewDto = SubmissionViewDto.builder()
                .submissionList(submissionList)
                .page(depositionSubmissionDto.getPage())
                .build();
        submissionViewDto.setPageIndexes();
        return submissionViewDto;
    }

    public DepositionSubmission getSubmission(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        return template.getForObject(depositionIngestURL + "/submissions/{submissionID}", DepositionSubmission.class, params);
    }

    public DepositionSampleListWrapper getSubmissionSamples(Pageable pageable, String submissionId) {

        String url = String.format("%s%s%s%s", depositionIngestURL, "/submissions/", submissionId, "/samples");
        URI targetUrl = UriBuilder.buildUrl(url, pageable);
        DepositionSampleListWrapper depositionSampleListWrapper = DepositionSampleListWrapper.builder().build();
        try {
            log.info(targetUrl.toString());
            depositionSampleListWrapper = template.getForObject(targetUrl, DepositionSampleListWrapper.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return depositionSampleListWrapper;
    }

    public DepositionStudyListWrapper getSubmissionStudies(Pageable pageable, String submissionId) {

        String url = String.format("%s%s%s%s", depositionIngestURL, "/submissions/", submissionId, "/studies");
        URI targetUrl = UriBuilder.buildUrl(url, pageable);
        DepositionStudyListWrapper studyListWrapper = DepositionStudyListWrapper.builder().build();
        try {
            log.info(targetUrl.toString());
            studyListWrapper = template.getForObject(targetUrl, DepositionStudyListWrapper.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return studyListWrapper;
    }

    public DepositionAssociationListWrapper getSubmissionAssociations(Pageable pageable, String submissionId) {

        String url = String.format("%s%s%s%s", depositionIngestURL, "/submissions/", submissionId, "/associations");
        URI targetUrl = UriBuilder.buildUrl(url, pageable);
        DepositionAssociationListWrapper associationListWrapper = DepositionAssociationListWrapper.builder().build();
        try {
            log.info(targetUrl.toString());
            associationListWrapper = template.getForObject(targetUrl, DepositionAssociationListWrapper.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return associationListWrapper;
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
