package uk.ac.ebi.spot.goci.curation.service.deposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.EventOperationsService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepositionSubmissionService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private final StudyService studyService;
    private final StudyOperationsService studyOperationsService;
    private final PublicationService publicationService;
    private final CurationStatus levelOnePlaceholderStatus;
    private final DepositionAssociationService depositionAssociationService;
    private final DepositionSampleService depositionSampleService;
    private final DepositionStudyService depositionStudyService;
    private final CuratorRepository curatorRepository;
    private final EventOperationsService eventOperationsService;
    private final PublicationExtensionRepository authorRepository;
    private final BodyOfWorkRepository bodyOfWorkRepository;
    private final UnpublishedStudyRepository unpublishedRepository;
    private final UnpublishedAncestryRepository unpublishedAncestryRepo;

    @Autowired
    @Qualifier("JodaMapper")
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate template;

    //@Value("${deposition.uri}")
    //private String depositionIngestURL;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    @Value("${deposition.token}")
    private String depositionToken;

    @Value("classpath:submissions.json")
    private Resource submissionFile;

    public DepositionSubmissionService(@Autowired PublicationService publicationService,
                                       @Autowired StudyService studyService,
                                       @Autowired StudyOperationsService studyOperationsService,
                                       @Autowired DepositionAssociationService depositionAssociationService,
                                       @Autowired DepositionSampleService depositionSampleService,
                                       @Autowired DepositionStudyService depositionStudyService,
                                       @Autowired CurationStatusRepository statusRepository,
                                       @Autowired CuratorRepository curatorRepository,
                                       @Autowired EventOperationsService eventOperationsService,
                                       @Autowired PublicationExtensionRepository authorRepository,
                                       @Autowired BodyOfWorkRepository bodyOfWorkRepository,
                                       @Autowired UnpublishedStudyRepository unpublishedRepository,
                                       @Autowired UnpublishedAncestryRepository unpublishedAncestryRepo) {
        this.publicationService = publicationService;
        this.studyService = studyService;
        this.studyOperationsService = studyOperationsService;
        this.depositionAssociationService = depositionAssociationService;
        this.depositionSampleService = depositionSampleService;
        this.depositionStudyService = depositionStudyService;
        this.curatorRepository = curatorRepository;
        this.eventOperationsService = eventOperationsService;
        this.authorRepository = authorRepository;
        this.bodyOfWorkRepository = bodyOfWorkRepository;
        this.unpublishedRepository = unpublishedRepository;
        this.unpublishedAncestryRepo = unpublishedAncestryRepo;

        levelOnePlaceholderStatus = statusRepository.findByStatus("Awaiting Literature");
    }

    public Map<String, String> getSubmissionPubMedIds(){
        Map<String, String> pubmedMap = new HashMap<>();
        Map<String, Submission> submissionMap = getSubmissionsBasic();
        submissionMap.entrySet().stream().filter(e->e.getValue().getPubMedID() != null).forEach(e->pubmedMap.put(e.getValue().getPubMedID(), e.getKey()));
        return pubmedMap;
    }

    public Map<String, Submission> getSubmissionsBasic(){
        String url = "/submission-envelopes";
        //String url = "/submissions";
        return getSubmissions(url);
//        return new TreeMap<>();
    }

    public Map<String, Submission> getSubmissions() {
        String url = "/submissions?page={page}";
        return getSubmissions(url);
    }

    private Map<String, Submission> getSubmissions(String url){

        Map<String, Submission> submissionList = new TreeMap<>();
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);

            //        try {
            //            DepositionSubmission[] submissions =
            //                    mapper.readValue(submissionFile.getInputStream(), DepositionSubmission[].class);
            //            for (DepositionSubmission submission : submissions) {
            //                Submission testSub = buildSubmission(submission);
            //                submissionList.put(testSub.getId(), testSub);
            //                params.put("page", ++i);
            //            }
            //        } catch (IOException e) {
            //            e.printStackTrace();
            //        }
            DepositionSubmission[] submissions =
                    template.getForObject(depositionIngestURL + url, DepositionSubmission[].class, params);
            Arrays.stream(submissions).forEach(s->{
                Submission testSub = buildSubmission(s);
                submissionList.put(testSub.getId(), testSub);
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return submissionList;
    }

    public DepositionSubmission getSubmission(String submissionID){
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
//        String response =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", String.class, params);
        DepositionSubmission submission =
                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", DepositionSubmission.class,
                        params);

        return submission;

    }

    public Submission buildSubmission(DepositionSubmission depositionSubmission){
        Submission testSub = new Submission();
        testSub.setId(depositionSubmission.getSubmissionId());
        testSub.setCurator(depositionSubmission.getCreated().getUser().getName());
        testSub.setStatus(depositionSubmission.getStatus());
        testSub.setCreated(depositionSubmission.getCreated().getTimestamp().toString(DateTimeFormat.shortDateTime()));
        testSub.setSubmissionType(getSubmissionType(depositionSubmission));
        if(depositionSubmission.getBodyOfWork() != null){
            BodyOfWorkDto bodyOfWork = depositionSubmission.getBodyOfWork();
            if(bodyOfWork.getPmids() != null && bodyOfWork.getPmids().size() != 0){
                testSub.setPubMedID(String.join(",",bodyOfWork.getPmids()));
            }
            if(bodyOfWork.getFirstAuthor() != null) {
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
        }else if(depositionSubmission.getPublication() != null) {
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
        return testSub;
    }



    public List<String> importSubmission(DepositionSubmission depositionSubmission, SecureUser currentUser) {
        List<String> statusMessages = new ArrayList<>();

        getLog().info("[IMPORT] Evaluating submission type for: {}", depositionSubmission.getSubmissionId());
        Submission.SubmissionType submissionType = getSubmissionType(depositionSubmission);
        getLog().info("[IMPORT] Found submission type for: {}", submissionType.name());

        Curator curator = curatorRepository.findByEmail(currentUser.getEmail());
        String submissionID = depositionSubmission.getSubmissionId();

        getLog().info("[IMPORT] Retrieving publication: {}", depositionSubmission.getPublication().getPmid());
        Publication publication = publicationService.findByPumedId(depositionSubmission.getPublication().getPmid());
        getLog().info("[IMPORT] Found publication: {}", publication.getPubmedId());

        if(depositionSubmission.getPublication().getCorrespondingAuthor() != null){
            getLog().info("[IMPORT] Creating Publication extensions for corresponding authors ...");
            PublicationExtension author = new PublicationExtension();
            author.setCorrespondingAuthorEmail(depositionSubmission.getPublication().getCorrespondingAuthor().getEmail());
            if(depositionSubmission.getPublication().getCorrespondingAuthor().getGroup() != null) {
                author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getGroup());
            }else{
                author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getFirstName() + ' ' + depositionSubmission.getPublication().getCorrespondingAuthor().getLastName());
            }
            authorRepository.save(author);
            getLog().info("[IMPORT] Publication extension created: {}", author.getId());
            List<PublicationExtension> authorList = new ArrayList<>();
            authorList.add(author);
            publication.setCorrespondingAuthors(authorList);
            publicationService.save(publication);
            getLog().info("[IMPORT] Publication [{}] saved.", publication.getPubmedId());
        }

        getLog().info("[IMPORT] Looking for studies in the local DB ...");
        Collection<Study> dbStudies =
                publicationService.findStudiesByPubmedId(depositionSubmission.getPublication().getPmid());
        List<Long> dbStudyIds = dbStudies.stream().map(Study::getId).collect(Collectors.toList());
        getLog().info("[IMPORT] Found {} studies: {}", dbStudies.size(), dbStudyIds);

        List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<String> gcsts = studies.stream().map(DepositionStudyDto::getAccession).collect(Collectors.toList());
        getLog().info("[IMPORT] Found {} studies in the submission retrieved from the Deposition App: {}", studies.size(), gcsts);

        //check submission status. if PUBLISHED, import summary stats, set state DONE
        //else import metadata, set state CURATOR_REVIEW
        if (submissionType == Submission.SubmissionType.SUM_STATS) { //if submission type is SUM_STATS only
            getLog().info("[IMPORT] Found SUM_STATS submission.", studies.size());

            getLog().info("[IMPORT] Moving summary stats from unpublished to published.");
            depositionStudyService.publishSummaryStats(studies, dbStudies, currentUser);
            getLog().info("[IMPORT] Moving summary stats done.");

            depositionSubmission.setStatus("CURATION_COMPLETE");
            depositionSubmission.getPublication().setStatus("PUBLISHED_WITH_SS");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);

            getLog().info("[IMPORT] Sending request to update submission in the Deposition App.");
            template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
            statusMessages.add("imported summary stats");
            getLog().info("[IMPORT] Summary stats imported. Process finalized.");
        } else {
            if (studies != null){// && dbStudies.size() == 1) { //only do this for un-curated publications
                getLog().info("[IMPORT] Deleting proxy studies created when the publication was initially imported.");
                depositionStudyService.deleteStudies(dbStudies, curator, currentUser);
                publicationService.save(publication);

                for (DepositionStudyDto studyDto : studies) {
                    getLog().info("[IMPORT] Processing study: {} | {}.", studyDto.getStudyTag(), studyDto.getAccession());
                    statusMessages.add(processStudy(depositionSubmission, studyDto, currentUser, publication, curator));
                }
                getLog().info("[IMPORT] Deleting unpublished studies and body of works.");
                cleanupPrePublishedStudies(studies);
            }
            publicationService.save(publication);
            getLog().info("[IMPORT] Setting new submission statuses.");
            depositionSubmission.setDateSubmitted(new LocalDate());
            depositionSubmission.setStatus("CURATION_COMPLETE");
            depositionSubmission.getPublication().setStatus("CURATION_STARTED");
            try {
                String message = mapper.writeValueAsString(depositionSubmission);
                template.put(depositionIngestURL + "/submissions/" + submissionID, depositionSubmission);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return statusMessages;
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

    public String checkSubmissionErrors(DepositionSubmission submission){
        Submission.SubmissionType type = getSubmissionType(submission);
        if(type.equals(Submission.SubmissionType.UNKNOWN)){
            boolean hasSumStats = false;
            boolean hasMetadata = false;
            boolean hasAssociations = false;
            for(DepositionStudyDto studyDto: submission.getStudies()){
                if(studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")){
                    hasSumStats = true;
                }
            }
            for(DepositionSampleDto sampleDto: submission.getSamples()){
                if(sampleDto.getStage() != null){
                    hasMetadata = true;
                }
            }
            for(DepositionAssociationDto associationDto: submission.getAssociations()){
                if(associationDto.getStudyTag() != null){
                    hasAssociations = true;
                }
            }
            return "Has SumStats: " + hasSumStats + ", has metadata: " + hasMetadata + ", has associations: " + hasAssociations;
        }
        return null;
    }

    public Submission.SubmissionType getSubmissionType(DepositionSubmission submission){
        if(submission.getBodyOfWork() != null && submission.getPublication() == null) {
            return Submission.SubmissionType.PRE_PUBLISHED;
        }
        else if(submission.getBodyOfWork() == null && submission.getPublication() == null) {
            return Submission.SubmissionType.UNKNOWN;
        }
        else if(submission.getPublication() != null) {
            String publicationStatus = submission.getPublication().getStatus();
            boolean hasSumStats = false;
            boolean hasMetadata = false;
            boolean hasAssociations = false;
            if (publicationStatus.equals("UNDER_SUBMISSION")) {
                hasMetadata = true;
            } else if (publicationStatus.equals("UNDER_SUMMARY_STATS_SUBMISSION")) {
                hasSumStats = true;
            }
            if (submission.getStudies() != null) {
                for (DepositionStudyDto studyDto : submission.getStudies()) {
                    if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") &&
                            !studyDto.getSummaryStatisticsFile().equals("NR")) {
                        hasSumStats = true;
                    }
                }
            }
            if (submission.getAssociations() != null) {
                for (DepositionAssociationDto associationDto : submission.getAssociations()) {
                    if (associationDto.getStudyTag() != null) {
                        hasAssociations = true;
                    }
                }
            }
            if (hasMetadata && hasSumStats && hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS;
            }
            if (hasMetadata && hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_SUM_STATS;
            }
            if (hasMetadata && !hasSumStats && hasAssociations) {
                return Submission.SubmissionType.METADATA_AND_TOP_ASSOCIATIONS;
            }
            if (hasMetadata && !hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.METADATA;
            }
            if (!hasMetadata && hasSumStats && !hasAssociations) {
                return Submission.SubmissionType.SUM_STATS;
            }
        }
        return Submission.SubmissionType.UNKNOWN;
    }

    @Transactional
    String processStudy(DepositionSubmission depositionSubmission, DepositionStudyDto studyDto, SecureUser currentUser,
                      Publication publication, Curator curator){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer studyNote = new StringBuffer(sdf.format(new Date()) + "\n");
        try {
            List<DepositionAssociationDto> associations = depositionSubmission.getAssociations();
            List<DepositionSampleDto> samples = depositionSubmission.getSamples();
            //List<DepositionFileUploadDto> files = depositionSubmission.getFiles();
            List<DepositionNoteDto> notes = depositionSubmission.getNotes();
            String studyTag = studyDto.getStudyTag();
            studyNote.append("created " + studyTag + "\n");

            Study study = depositionStudyService.initStudy(studyDto, publication, currentUser);
            Collection<Study> pubStudies = publication.getStudies();
            if (pubStudies == null) {
                pubStudies = new ArrayList<>();
            }
            pubStudies.add(study);
            publication.setStudies(pubStudies);
            studyService.save(study);
            if (associations != null) {
                getLog().info("[IMPORT] Found {} associations in the submission retrieved from the Deposition App.", associations.size());
                studyNote.append(depositionAssociationService
                        .saveAssociations(currentUser, studyTag, study, associations));
            }
            if (samples != null) {
                getLog().info("[IMPORT] Found {} samples in the submission retrieved from the Deposition App.", samples.size());
                studyNote.append(depositionSampleService.saveSamples(currentUser, studyTag, study, samples));
            }

            getLog().info("[IMPORT] Creating events ...");
            Event event = eventOperationsService.createEvent("STUDY_CREATION", currentUser, "Import study " + "creation");
            List<Event> events = new ArrayList<>();
            events.add(event);
            study.setEvents(events);
            getLog().info("[IMPORT] Adding notes ...");
            depositionStudyService
                    .addStudyNote(study, studyDto.getStudyTag(), studyNote.toString(), "STUDY_CREATION", curator,
                            "Import study creation", currentUser);
            if (notes != null) {
                //find notes in study
                for (DepositionNoteDto noteDto : notes) {
                    if (noteDto.getStudyTag().equals(studyTag)) {
                        depositionStudyService.addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(), noteDto.getStatus(),
                                curator, noteDto.getNoteSubject(), currentUser);
                    }
                }
            }
            getLog().info("[IMPORT] Final save ...");
            studyService.save(study);
            getLog().info("[IMPORT] All done ...");
        }catch(Exception e){
            studyNote.append("error creating study: " + e.getMessage());
            getLog().error("Unable to process study [{}]: {}", studyDto.getAccession(), e.getMessage(), e);
        }
        return studyNote.toString();
    }

    private void cleanupPrePublishedStudies(List<DepositionStudyDto> studyDtoList){
        studyDtoList.forEach(studyDto -> {
            UnpublishedStudy unpublishedStudy = unpublishedRepository.findByAccession(studyDto.getAccession());
            if(unpublishedStudy != null){
                Collection<BodyOfWork> bodyOfWorks = unpublishedStudy.getBodiesOfWork();
                Collection<UnpublishedAncestry> ancestries = unpublishedStudy.getAncestries();
                unpublishedAncestryRepo.delete(ancestries);
                Set<UnpublishedStudy> referencedStudies = new HashSet<>();
                bodyOfWorks.forEach(bodyOfWork -> {
                    bodyOfWork.getStudies().forEach(study->{
                        referencedStudies.add(study);
                    });
                });
                if(referencedStudies.size() <= 1){
                    bodyOfWorkRepository.delete(bodyOfWorks);
                }
                unpublishedRepository.delete(unpublishedStudy);
            }
        });
    }
}
