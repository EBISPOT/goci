package uk.ac.ebi.spot.goci.curation.service.deposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;
import uk.ac.ebi.spot.goci.service.EventOperationsService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

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

    @Autowired
    @Qualifier("JodaMapper")
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate template;

    //@Value("${deposition.uri}")
    //private String depositionIngestURL;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;


    public DepositionSubmissionService(@Autowired PublicationService publicationService,
                                       @Autowired StudyService studyService,
                                       @Autowired StudyOperationsService studyOperationsService,
                                       @Autowired DepositionAssociationService depositionAssociationService,
                                       @Autowired DepositionSampleService depositionSampleService,
                                       @Autowired DepositionStudyService depositionStudyService,
                                       @Autowired CurationStatusRepository statusRepository,
                                       @Autowired CuratorRepository curatorRepository,
                                       @Autowired EventOperationsService eventOperationsService) {
        this.publicationService = publicationService;
        this.studyService = studyService;
        this.studyOperationsService = studyOperationsService;
        this.depositionAssociationService = depositionAssociationService;
        this.depositionSampleService = depositionSampleService;
        this.depositionStudyService = depositionStudyService;
        this.curatorRepository = curatorRepository;
        this.eventOperationsService = eventOperationsService;
        levelOnePlaceholderStatus = statusRepository.findByStatus("Awaiting Literature");
    }

    public void importSubmission(DepositionSubmission depositionSubmission, SecureUser currentUser) {
        //TODO: update notes with import log
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Submission.SubmissionType submissionType = getSubmissionType(depositionSubmission);
        Curator curator = curatorRepository.findByEmail(currentUser.getEmail());
        String submissionID = depositionSubmission.getSubmissionId();
        Publication publication = publicationService.findByPumedId(depositionSubmission.getPublication().getPmid());
        Collection<Study> dbStudies =
                publicationService.findStudiesByPubmedId(depositionSubmission.getPublication().getPmid());

        List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<DepositionAssociationDto> associations = depositionSubmission.getAssociations();
        List<DepositionSampleDto> samples = depositionSubmission.getSamples();
        List<DepositionFileUploadDto> files = depositionSubmission.getFiles();
        List<DepositionNoteDto> notes = depositionSubmission.getNotes();

        //check submission status. if PUBLISHED, import summary stats, set state DONE
        //else import metadata, set state CURATOR_REVIEW
        if (submissionType == Submission.SubmissionType.SUM_STATS) { //if submission type is SUM_STATS only
            depositionStudyService.publishSummaryStats(dbStudies, currentUser);
            depositionSubmission.setStatus("COMPLETE");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);
            template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
        } else {
            if (studies != null){// && dbStudies.size() == 1) { //only do this for un-curated publications
                depositionStudyService.deleteStudies(dbStudies, curator, currentUser);

                StringBuffer studyNote = new StringBuffer(sdf.format(new Date()) + "\n");
                for (DepositionStudyDto studyDto : studies) {
                    String studyTag = studyDto.getStudyTag();
                    studyNote.append("created " + studyTag + "\n");
                    Study study = depositionStudyService.initStudy(studyDto, publication, currentUser);
                    studyService.save(study);
                    if (notes != null) {
                        //find notes in study
                        for (DepositionNoteDto noteDto : notes) {
                            if (noteDto.getStudyTag().equals(studyTag)) {
                                depositionStudyService.addStudyNote(study, noteDto, currentUser, curator);
                            }
                        }
                    }
                    studyService.save(study);
                    if (associations != null) {
                        studyNote.append(depositionAssociationService.saveAssociations(currentUser, studyTag, study,
                                associations));
                    }
                    if (samples != null) {
                        studyNote.append(depositionSampleService.saveSamples(currentUser, studyTag, study, samples));
                    }

                    eventOperationsService.createEvent("STUDY_CREATION", currentUser, "Import study creation");
                    studyService.save(study);
                    if(submissionType.toString().contains("SUM_STATS")){
                        depositionStudyService.publishSummaryStats(study, currentUser);
                    }
                }
            }
            depositionSubmission.setDateSubmitted(new LocalDate());
            depositionSubmission.setStatus("CURATION_COMPLETE");
            try {
                String message = mapper.writeValueAsString(depositionSubmission);
                template.put(depositionIngestURL + "/submissions/" + submissionID, depositionSubmission);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
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

    public Submission.SubmissionType getSubmissionType(DepositionSubmission submission){
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
