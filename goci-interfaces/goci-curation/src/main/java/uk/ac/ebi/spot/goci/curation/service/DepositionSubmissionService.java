package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.model.deposition.util.*;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyList;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.AssociationService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.SingleNucleotidePolymorphismService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.*;

@Service
public class DepositionSubmissionService {
    private final DiseaseTraitRepository diseaseTraitRepository;
    private final UnpublishReasonRepository unpublishReasonRepository;
    private final CurationStatusRepository statusRepository;
    private final CuratorRepository curatorRepository;
    private final StudyService studyService;
    private final StudyOperationsService studyOperationsService;
    private final PublicationService publicationService;
    private final Curator levelTwoCurator;
    private final CurationStatus levelOneCurationComplete;
    private final CurationStatus levelOnePlaceholderStatus;
    private final PlatformRepository platformRepository;
    private final GenotypingTechnologyRepository genotypingTechnologyRepository;
    private final NoteSubjectRepository noteSubjectRepository;
    private final CountryRepository countryRepository;
    private final HousekeepingOperationsService housekeepingRepository;
    private final StudyAncestryService ancestryService;
    private final SingleNucleotidePolymorphismRepository snpService;
    private final AssociationRepository associationService;

    @Autowired
    private RestTemplate template;

    @Value("${deposition.uri}")
    private String depositionURL;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;


    public DepositionSubmissionService(@Autowired PublicationService publicationService,
                                       @Autowired StudyOperationsService studyOperationsService,
                                       @Autowired StudyService studyService,
                                       @Autowired CuratorRepository curatorRepository,
                                       @Autowired CurationStatusRepository statusRepository,
                                       @Autowired UnpublishReasonRepository unpublishReasonRepository,
                                       @Autowired DiseaseTraitRepository diseaseTraitRepository,
                                       @Autowired PlatformRepository platformRepository,
                                       @Autowired GenotypingTechnologyRepository genotypingTechnologyRepository,
                                       @Autowired NoteSubjectRepository noteSubjectRepository,
                                       @Autowired CountryRepository countryRepository,
                                       @Autowired HousekeepingOperationsService housekeepingRepository,
                                       @Autowired StudyAncestryService ancestryService,
                                       @Autowired SingleNucleotidePolymorphismRepository snpService,
                                       @Autowired AssociationRepository associationService) {
        this.publicationService = publicationService;
        this.studyOperationsService = studyOperationsService;
        this.studyService = studyService;
        this.curatorRepository = curatorRepository;
        this.statusRepository = statusRepository;
        this.unpublishReasonRepository = unpublishReasonRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.platformRepository = platformRepository;
        this.genotypingTechnologyRepository = genotypingTechnologyRepository;
        this.noteSubjectRepository = noteSubjectRepository;
        this.countryRepository = countryRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.ancestryService = ancestryService;
        this.snpService = snpService;
        this.associationService = associationService;
        levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");
        levelOnePlaceholderStatus = statusRepository.findByStatus("Awaiting Literature");
    }

    public void importSubmission(DepositionSubmission depositionSubmission, SecureUser currentUser){
        String submissionID = depositionSubmission.getSubmissionId();
        Publication publication =publicationService.findByPumedId(depositionSubmission.getPublication().getPmid());
        Collection<Study> dbStudies =
                publicationService.findStudiesByPubmedId(depositionSubmission.getPublication().getPmid());

        List<DepositionStudyDto> studies = getStudies(submissionID).getStudies();
        List<DepositionAssociationDto> associations = getAssociations(submissionID).getAssociations();
        List<DepositionSampleDto> samples = getSamples(submissionID).getSamples();
        List<DepositionFileUploadDto> files = depositionSubmission.getFiles();
        List<DepositionNoteDto> notes = getNotes(submissionID).getNotes();

        //check submission status. if UNDER_SUMMARY_STATS_SUBMISSION, import summary stats, set state DONE
        //else import metadata, set state CURATOR_REVIEW
        if(depositionSubmission.getPublication().getStatus().equals("UNDER_SUMMARY_STATS_SUBMISSION")){
            UnpublishReason tempReason = unpublishReasonRepository.findById(1L);
            for(Study study: dbStudies){
                CurationStatus currentStatus = study.getHousekeeping().getCurationStatus();
                study.setFullPvalueSet(true);
                studyService.save(study);
                studyOperationsService.unpublishStudy(study.getId(), tempReason, currentUser);
                studyOperationsService.assignStudyStatus(study, new StatusAssignment(currentStatus.getId(),
                        currentStatus.getStatus()), currentUser);
            }
            depositionSubmission.setStatus("COMPLETE");
            Map<String, String> params = new HashMap<>();
            params.put("submissionID", submissionID);
            template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
        }else {
            if (studies != null && dbStudies.size() == 1) { //only do this for un-curated publications
                for (Study study : dbStudies) {
                    Collection<StudyNote> studyNotes
                            = study.getNotes();
                    if(studyNotes != null) {
                        for (StudyNote note : studyNotes) {
                            note.setTextNote("DELETED");
                        }
                    }else{
                        StudyNote note = new StudyNote();
                        note.setTextNote("DELETED");
                        note.setStudy(study);
                        study.setNotes(Arrays.asList(new StudyNote[]{note}));
                    }
//                    study.setStudyDesignComment("DELETED " + study.getStudyDesignComment());
                    studyService.save(study);
                    //studyService.deleteByStudyId(study.getId());
                }

                for (DepositionStudyDto studyDto : studies) {
                    String studyTag = studyDto.getStudyTag();
                    Study study = initStudy(studyDto, publication, currentUser);
                    if(associations != null){
                        //find associations in study
                        for(DepositionAssociationDto associationDto: associations){
                            if(associationDto.getStudyTag().equals(studyTag)){
//                                Association association = new Association();
//                                association.setPvalueExponent(associationDto.getPValue().scale());
//                                association.setPvalueMantissa(associationDto.getPValue().toBigInteger().intValue());
//                                SingleNucleotidePolymorphism snp = snpService.findByRsId(associationDto.getEffectAllele());
//                                association.setSnps(Arrays.asList(new SingleNucleotidePolymorphism[]{snp}));
//                                association.setStudy(study);
                            }
                        }
                    }if(samples != null){
                        //find samples in study
                        for(DepositionSampleDto sampleDto: samples){
                            if(sampleDto.getStudyTag().equals(studyTag)){
                                Ancestry ancestry = new Ancestry();
                                if(sampleDto.getStage().equals("Discovery")){
                                    ancestry.setType("initial");
                                    study.setInitialSampleSize(buildDescription(sampleDto));
                                }else if(sampleDto.getStage().equals("Replication")){
                                    ancestry.setType("replication");
                                    study.setReplicateSampleSize(buildDescription(sampleDto));
                                }
                                ancestry.setDescription(sampleDto.getAncestryDescription());
                                Country country =
                                        countryRepository.findByCountryName(sampleDto.getCountryRecruitement());
                                ancestry.setCountryOfRecruitment(Arrays.asList(new Country[]{country}));
                                ancestry.setNumberOfIndividuals(sampleDto.getSize());
                                ancestryService.addAncestry(study.getId(), ancestry, currentUser);
                            }
                        }
                    }if(notes != null){
                        //find notes in study
                        for(DepositionNoteDto noteDto: notes){
                            if(noteDto.getStudyTag().equals(studyTag)){
                                StudyNote note = new StudyNote();
                                note.setTextNote(noteDto.getNote());
                                note.setNoteSubject(noteSubjectRepository.findBySubjectIgnoreCase(noteDto.getNoteSubject()));
                                study.setNotes(Arrays.asList(new StudyNote[]{note}));
                            }
                        }
                    }
                }
            }
            depositionSubmission.setStatus("IMPORTED");
            //Map<String, String> params = new HashMap<>();
            //params.put("submissionID", submissionID);
            //template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
        }
    }

    private String buildDescription(DepositionSampleDto sampleDto){
        String description = sampleDto.getCases() + " " + sampleDto.getAncestryCategory() + " ancestry cases, "
                + sampleDto.getControls() + " " + sampleDto.getAncestryCategory() + " ancestry controls";
        return description;
    }

    public Submission updateSubmission(Submission submission, SecureUser currentUser){
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
    private uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyList getStudies(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        String response =
                template.getForObject(depositionURL + "/submissions/{submissionID}/studies", String.class, params);
        DepositionStudyListWrapper studyList =
                template.getForObject(depositionURL + "/submissions/{submissionID}/studies",
                        DepositionStudyListWrapper.class, params);
        if (studyList.getStudies() == null) {
            studyList.setStudies(new DepositionStudyList());
        }
        return studyList.getStudies();
    }

    private DepositionAssociationList getAssociations(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        String response =
                template.getForObject(depositionURL + "/submissions/{submissionID}/associations", String.class, params);
        DepositionAssociationListWrapper associationListWrapper =
                template.getForObject(depositionURL + "/submissions/{submissionID" + "}/associations",
                        DepositionAssociationListWrapper.class, params);
        if (associationListWrapper.getAssociations() == null) {
            associationListWrapper.setAssociations(new DepositionAssociationList());
        }
        return associationListWrapper.getAssociations();
    }

    private DepositionSampleList getSamples(String submissionID) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
        String response =
                template.getForObject(depositionURL + "/submissions/{submissionID}/samples", String.class, params);
        DepositionSampleListWrapper sampleListWrapper =
                template.getForObject(depositionURL + "/submissions/{submissionID" + "}/samples",
                        DepositionSampleListWrapper.class, params);
        if (sampleListWrapper.getSamplesList() == null) {
            sampleListWrapper.setSamplesList(new DepositionSampleList());
        }
        return sampleListWrapper.getSamplesList();
    }

//    private DepositionFileUploadList getUploads(String submissionID) {
//        Map<String, String> params = new HashMap<>();
//        params.put("submissionID", submissionID);
//        String response =
//                template.getForObject(depositionURL + "/submissions/{submissionID}/uploads", String.class, params);
//        DepositionFileUploadListWrapper listWrapper =
//                template.getForObject(depositionURL + "/submissions/{submissionID" + "}/uploads",
//                        DepositionFileUploadListWrapper.class, params);
//        if (listWrapper.getUploads() == null) {
//            listWrapper.setUploads(new DepositionFileUploadList());
//        }
//        return listWrapper.getUploads();
//    }

        private DepositionNoteList getNotes(String submissionID){
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", submissionID);
//        String response = template.getForObject(depositionURL + "/submissions/{submissionID}/files",
//                String.class,
//                params);
//        DepositionFileUploadListWrapper listWrapper = template.getForObject(depositionURL +
//        "/submissions/{submissionID" +
//                        "}/files",
//                DepositionFileUploadListWrapper.class, params);
//        return listWrapper.getUploads();
            return new DepositionNoteList();
    }

    private Study initStudy(DepositionStudyDto studyDto, Publication publication, SecureUser currentUser){
        Study study = studyDto.buildStudy();
        DiseaseTrait diseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getTrait());
        study.setDiseaseTrait(diseaseTrait);

        Platform platform = platformRepository.findByManufacturer(studyDto.getArrayManufacturer());
        study.setPlatforms(Arrays.asList(new Platform[]{platform}));
        GenotypingTechnology gtt =
                genotypingTechnologyRepository.findByGenotypingTechnology(studyDto.getGenotypingTechnology());
        study.setGenotypingTechnologies(Arrays.asList(new GenotypingTechnology[]{gtt}));

        study.setPublicationId(publication);
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        studyService.save(study);
        return study;
    }

}
