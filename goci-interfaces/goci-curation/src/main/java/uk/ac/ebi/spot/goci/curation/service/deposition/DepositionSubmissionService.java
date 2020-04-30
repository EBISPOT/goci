package uk.ac.ebi.spot.goci.curation.service.deposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
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

@Service
public class DepositionSubmissionService {
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
        //String url = "/submission-envelopes";
        String url = "/submissions";
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
        String response =
                template.getForObject(depositionIngestURL + "/submissions/{submissionID}", String.class, params);
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
//            testSub.setPubMedID(depositionSubmission.getBodyOfWork().getPmids();
            if(depositionSubmission.getBodyOfWork().getFirstAuthor() != null) {
                if (depositionSubmission.getBodyOfWork().getFirstAuthor().getGroup() != null) {
                    testSub.setAuthor(depositionSubmission.getBodyOfWork().getFirstAuthor().getGroup());
                } else {
                    testSub.setAuthor(depositionSubmission.getBodyOfWork().getFirstAuthor().getFirstName() + ' ' +
                            depositionSubmission.getBodyOfWork().getFirstAuthor().getLastName());
                }
            }
            testSub.setTitle(depositionSubmission.getBodyOfWork().getTitle());
            testSub.setPublicationStatus(depositionSubmission.getBodyOfWork().getStatus());
            testSub.setDoi(depositionSubmission.getBodyOfWork().getDoi());
            if (testSub.getSubmissionType().equals(Submission.SubmissionType.UNKNOWN)) {
                testSub.setStatus("REVIEW");
            }
        }else if(depositionSubmission.getPublication() != null) {
            testSub.setPubMedID(depositionSubmission.getPublication().getPmid());
            testSub.setAuthor(depositionSubmission.getPublication().getFirstAuthor());
            testSub.setTitle(depositionSubmission.getPublication().getTitle());
            testSub.setPublicationStatus(depositionSubmission.getPublication().getStatus());
            testSub.setDoi(depositionSubmission.getPublication().getDoi());
            if (testSub.getSubmissionType().equals(Submission.SubmissionType.UNKNOWN)) {
                testSub.setStatus("REVIEW");
            }
        }
        return testSub;
    }



    public List<String> importSubmission(DepositionSubmission depositionSubmission, SecureUser currentUser) {
        List<String> statusMessages = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Submission.SubmissionType submissionType = getSubmissionType(depositionSubmission);
        Curator curator = curatorRepository.findByEmail(currentUser.getEmail());
        String submissionID = depositionSubmission.getSubmissionId();
        Publication publication = publicationService.findByPumedId(depositionSubmission.getPublication().getPmid());
        if(depositionSubmission.getPublication().getCorrespondingAuthor() != null){
            PublicationExtension author = new PublicationExtension();
            author.setCorrespondingAuthorEmail(depositionSubmission.getPublication().getCorrespondingAuthor().getEmail());
            if(depositionSubmission.getPublication().getCorrespondingAuthor().getGroup() != null) {
                author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getGroup());
            }else{
                author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getFirstName() + ' ' + depositionSubmission.getPublication().getCorrespondingAuthor().getLastName());
            }
            authorRepository.save(author);
            List<PublicationExtension> authorList = new ArrayList<>();
            authorList.add(author);
            publication.setCorrespondingAuthors(authorList);
            publicationService.save(publication);
        }
        Collection<Study> dbStudies =
                publicationService.findStudiesByPubmedId(depositionSubmission.getPublication().getPmid());

        List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<DepositionAssociationDto> associations = depositionSubmission.getAssociations();
        List<DepositionSampleDto> samples = depositionSubmission.getSamples();
        //List<DepositionFileUploadDto> files = depositionSubmission.getFiles();
        List<DepositionNoteDto> notes = depositionSubmission.getNotes();

        //check submission status. if PUBLISHED, import summary stats, set state DONE
        //else import metadata, set state CURATOR_REVIEW
        if (submissionType == Submission.SubmissionType.SUM_STATS) { //if submission type is SUM_STATS only
            depositionStudyService.publishSummaryStats(studies, dbStudies, currentUser);
            depositionSubmission.setStatus("CURATION_COMPLETE");
            depositionSubmission.getPublication().setStatus("PUBLISHED_WITH_SS");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);
            template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
            statusMessages.add("imported summary stats");
        } else {
            if (studies != null){// && dbStudies.size() == 1) { //only do this for un-curated publications
                depositionStudyService.deleteStudies(dbStudies, curator, currentUser);

                for (DepositionStudyDto studyDto : studies) {
                    StringBuffer studyNote = new StringBuffer(sdf.format(new Date()) + "\n");
                    String studyTag = studyDto.getStudyTag();
                    studyNote.append("created " + studyTag + "\n");
                    Study study = depositionStudyService.initStudy(studyDto, publication, currentUser);
                    Collection<Study> pubStudies = publication.getStudies();
                    if(pubStudies == null){
                        pubStudies = new ArrayList<>();
                    }
                    pubStudies.add(study);
                    publication.setStudies(pubStudies);
                    studyService.save(study);
                    if (associations != null) {
                        studyNote.append(depositionAssociationService.saveAssociations(currentUser, studyTag, study,
                                associations));
                    }
                    if (samples != null) {
                        studyNote.append(depositionSampleService.saveSamples(currentUser, studyTag, study, samples));
                    }

                    Event event = eventOperationsService.createEvent("STUDY_CREATION", currentUser, "Import study " +
                            "creation");
                    List<Event> events = new ArrayList<>();
                    events.add(event);
                    study.setEvents(events);
                    depositionStudyService.addStudyNote(study, studyDto.getStudyTag(), studyNote.toString(), "STUDY_CREATION",
                            curator,
                            "Import study creation", currentUser);
                    if (notes != null) {
                        //find notes in study
                        for (DepositionNoteDto noteDto : notes) {
                            if (noteDto.getStudyTag().equals(studyTag)) {
                                depositionStudyService.addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(),
                                        noteDto.getStatus(), curator, noteDto.getNoteSubject(), currentUser);
                            }
                        }
                    }
                    studyService.save(study);
                    statusMessages.add(studyNote.toString());
                }
                cleanupPrePublishedStudies(studies);
            }
            publicationService.save(publication);
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
                if(studyDto.getSummaryStatisticsFile() != null){
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
        if(submission.getBodyOfWork() != null) {
            return Submission.SubmissionType.PRE_PUBLISHED;
        }
        else if(submission.getBodyOfWork() == null && submission.getPublication() == null) {
            return Submission.SubmissionType.UNKNOWN;
        }
        String publicationStatus = submission.getPublication().getStatus();
        boolean hasSumStats = false;
        boolean hasMetadata = false;
        boolean hasAssociations = false;
        if(publicationStatus.equals("UNDER_SUBMISSION")){
            hasMetadata = true;
        }
        else if(publicationStatus.equals("UNDER_SUMMARY_STATS_SUBMISSION")){
            hasSumStats = true;
        }
        if(submission.getStudies() != null) {
            for (DepositionStudyDto studyDto : submission.getStudies()) {
                if (studyDto.getSummaryStatisticsFile() != null) {
                    hasSumStats = true;
                }
            }
        }
        if(submission.getAssociations() != null) {
            for (DepositionAssociationDto associationDto : submission.getAssociations()) {
                if (associationDto.getStudyTag() != null) {
                    hasAssociations = true;
                }
            }
        }
        if(hasMetadata && hasSumStats && hasAssociations){
            return Submission.SubmissionType.METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS;
        }
        if(hasMetadata && hasSumStats && !hasAssociations){
            return Submission.SubmissionType.METADATA_AND_SUM_STATS;
        }
        if(hasMetadata && !hasSumStats && hasAssociations){
            return Submission.SubmissionType.METADATA_AND_TOP_ASSOCIATIONS;
        }
        if(hasMetadata && !hasSumStats && !hasAssociations){
            return Submission.SubmissionType.METADATA;
        }
        if(!hasMetadata && hasSumStats && !hasAssociations){
            return Submission.SubmissionType.SUM_STATS;
        }
        return Submission.SubmissionType.UNKNOWN;
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

//    private DepositionStudyList getStudies(String submissionID) {
//        Map<String, String> params = new HashMap<>();
//        params.put("submissionID", submissionID);
//        String response =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID}/studies", String.class,
//                        params);
//        DepositionStudyListWrapper studyList =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID}/studies",
//                        DepositionStudyListWrapper.class, params);
//        if (studyList.getStudies() == null) {
//            studyList.setStudies(new DepositionStudyList());
//        }
//        return studyList.getStudies();
//    }
//
//    private DepositionAssociationList getAssociations(String submissionID) {
//        Map<String, String> params = new HashMap<>();
//        params.put("submissionID", submissionID);
//        String response =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID}/associations", String.class,
//                        params);
//        DepositionAssociationListWrapper associationListWrapper =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID" + "}/associations",
//                        DepositionAssociationListWrapper.class, params);
//        if (associationListWrapper.getAssociations() == null) {
//            associationListWrapper.setAssociations(new DepositionAssociationList());
//        }
//        return associationListWrapper.getAssociations();
//    }
//
//    private DepositionSampleList getSamples(String submissionID) {
//        Map<String, String> params = new HashMap<>();
//        params.put("submissionID", submissionID);
//        String response =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID}/samples", String.class,
//                        params);
//        DepositionSampleListWrapper sampleListWrapper =
//                template.getForObject(depositionIngestURL + "/submissions/{submissionID" + "}/samples",
//                        DepositionSampleListWrapper.class, params);
//        if (sampleListWrapper.getSamplesList() == null) {
//            sampleListWrapper.setSamplesList(new DepositionSampleList());
//        }
//        return sampleListWrapper.getSamplesList();
//    }
//
//    private DepositionNoteList getNotes(String submissionID) {
//        Map<String, String> params = new HashMap<>();
//        params.put("submissionID", submissionID);
//        return new DepositionNoteList();
//    }
}
