package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.curation.service.HousekeepingOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.EventOperationsService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class StudiesProcessingService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    DiseaseTraitRepository diseaseTraitRepository;
    @Autowired
    PlatformRepository platformRepository;
    @Autowired
    GenotypingTechnologyRepository genotypingTechnologyRepository;
    @Autowired
    HousekeepingOperationsService housekeepingRepository;
    @Autowired
    CuratorRepository curatorRepository;
    @Autowired
    CurationStatusRepository statusRepository;
    @Autowired
    UnpublishReasonRepository unpublishReasonRepository;
    @Autowired
    StudyService studyService;
    @Autowired
    StudyOperationsService studyOperationsService;
    @Autowired
    StudyNoteOperationsService noteOperationsService;
    @Autowired
    NoteSubjectRepository noteSubjectRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    EfoTraitRepository efoTraitRepository;
    @Autowired
    StudyExtensionRepository studyExtensionRepository;
    @Autowired
    EventOperationsService eventOperationsService;

    @Autowired
    private DepositionSampleService depositionSampleService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private DepositionAssociationService depositionAssociationService;

    @Transactional
    public boolean processStudies(DepositionSubmission depositionSubmission, SecureUser currentUser, Publication publication, Curator curator, ImportLog importLog) {
        for (DepositionStudyDto studyDto : depositionSubmission.getStudies()) {
            getLog().info("[{}] Processing study: {} | {}.", depositionSubmission.getSubmissionId(), studyDto.getStudyTag(), studyDto.getAccession());

            ImportLogStep importStep = importLog.addStep(new ImportLogStep("Creating study [" + studyDto.getAccession() + "]", depositionSubmission.getSubmissionId()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            StringBuffer studyNote = new StringBuffer(sdf.format(new Date()) + "\n");
            List<DepositionAssociationDto> associations = depositionSubmission.getAssociations();
            List<DepositionSampleDto> samples = depositionSubmission.getSamples();

            List<DepositionNoteDto> notes = depositionSubmission.getNotes();
            String studyTag = studyDto.getStudyTag();
            studyNote.append("created " + studyTag + "\n");

            Study study;
            try {
                study = this.initStudy(studyDto, publication);
            } catch (Exception e) {
                getLog().error("Unable to create study [{} | {}]: {}", studyDto.getStudyTag(), studyDto.getAccession(), e.getMessage(), e);
                importLog.addError("Unable to create study [" + studyDto.getStudyTag() + " | " + studyDto.getAccession() + "]: " + e.getMessage(), "Creating study [" + studyDto.getAccession() + "]");
                importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                return false;
            }
            Collection<Study> pubStudies = publication.getStudies();
            if (pubStudies == null) {
                pubStudies = new ArrayList<>();
            }
            pubStudies.add(study);
            publication.setStudies(pubStudies);
            studyService.save(study);
            publicationService.save(publication);
            if (associations != null) {
                getLog().info("Found {} associations in the submission retrieved from the Deposition App.", associations.size());
                studyNote.append(depositionAssociationService.saveAssociations(currentUser, studyTag, study, associations, importLog));
            }
            if (samples != null) {
                getLog().info("Found {} samples in the submission retrieved from the Deposition App.", samples.size());
                studyNote.append(depositionSampleService.saveSamples(studyTag, study, samples, importLog));
            }

            getLog().info("Creating events ...");
            Event event = eventOperationsService.createEvent("STUDY_CREATION", currentUser, "Import study " + "creation");
            List<Event> events = new ArrayList<>();
            events.add(event);
            study.setEvents(events);
            getLog().info("Adding notes ...");
            this.addStudyNote(study, studyDto.getStudyTag(), studyNote.toString(), "STUDY_CREATION", curator,
                    "Import study creation", currentUser);
            if (notes != null) {
                //find notes in study
                for (DepositionNoteDto noteDto : notes) {
                    if (noteDto.getStudyTag().equals(studyTag)) {
                        this.addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(), noteDto.getStatus(),
                                curator, noteDto.getNoteSubject(), currentUser);
                    }
                }
            }
            studyService.save(study);
            importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
        }

        getLog().info("All done ...");
        return true;
    }

    private void addStudyNote(Study study, String studyTag, String noteText, String noteStatus, Curator noteCurator,
                              String noteSubject,
                              SecureUser currentUser) {
        StudyNote note = noteOperationsService.createEmptyStudyNote(study, currentUser);
        if (studyTag != null) {
            note.setTextNote(studyTag + "\n" + noteText);
        } else {
            note.setTextNote(noteText);
        }
        if (noteStatus != null) {
            note.setStatus(Boolean.parseBoolean(noteStatus));
        }
        if (noteCurator != null) {
            note.setCurator(noteCurator);
        }
        if (noteSubject != null) {
            NoteSubject subject = noteSubjectRepository.findBySubjectIgnoreCase(noteSubject);
            if (subject == null) {
                subject = noteSubjectRepository.findBySubjectIgnoreCase("System note");
            }
            note.setNoteSubject(subject);
        }
        note.setStudy(study);
        noteRepository.save(note);
        study.addNote(note);
        studyService.save(study);
    }

    private Study initStudy(DepositionStudyDto studyDto, Publication publication) {
        Curator levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        CurationStatus levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");

        getLog().info("Initializing study: {} | {}", publication.getPubmedId(), studyDto.getAccession());
        Study study = studyDto.buildStudy();
        DiseaseTrait diseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getTrait());
        study.setDiseaseTrait(diseaseTrait);

        String manufacturerString = studyDto.getArrayManufacturer();
        if (manufacturerString != null) {
            List<Platform> platformList = new ArrayList<>();
            String[] manufacturers = manufacturerString.split("\\||,");
            getLog().info("Manufacturers provided: {}", manufacturerString);
            for (String manufacturer : manufacturers) {
                Platform platform = platformRepository.findByManufacturer(manufacturer.trim());
                platformList.add(platform);
            }
            getLog().info("Manufacturers mapped: {}", platformList);
            study.setPlatforms(platformList);
        }
        List<GenotypingTechnology> gtList = new ArrayList<>();
        String genotypingTech = studyDto.getGenotypingTechnology();
        if (genotypingTech != null) {
            String[] technologies = genotypingTech.split("\\||,");
            getLog().info("Genotyping technology provided: {}", genotypingTech);
            for (String technology : technologies) {
                GenotypingTechnology gtt = genotypingTechnologyRepository.findByGenotypingTechnology(
                        DepositionTransform.transformGenotypingTechnology(technology.trim()));
                gtList.add(gtt);
            }
        }
        getLog().info("Genotyping technology mapped: {}", gtList);
        study.setGenotypingTechnologies(gtList);

        study.setPublicationId(publication);
        getLog().info("Creating house keeping ...");
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        getLog().info("House keeping created: {}", housekeeping.getId());
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
            getLog().info("Full p-value set to TRUE.");
        }
        Integer variantCount = studyDto.getVariantCount();
        if (variantCount != -1) {
            study.setSnpCount(variantCount);
        }
        List<EfoTrait> efoTraitList = new ArrayList<>();
        String efoTrait = studyDto.getEfoTrait();
        if (efoTrait != null) {
            String[] efoTraits = efoTrait.split("\\||,");
            getLog().info("EFO traits provided: {}", efoTraits);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait.trim());
                efoTraitList.add(dbTrait);
            }
        }
        List<EfoTrait> mappedTraitList = new ArrayList<>();
        getLog().info("EFO traits mapped: {}", efoTraitList);
        study.setEfoTraits(efoTraitList);
        String mappedBackgroundTrait = studyDto.getBackgroundEfoTrait();
        if (mappedBackgroundTrait != null) {
            String[] efoTraits = mappedBackgroundTrait.split("\\||,");
            getLog().info("Background EFO traits provided: {}", mappedBackgroundTrait);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait);
                mappedTraitList.add(dbTrait);
            }
        }
        getLog().info("Background EFO traits mapped: {}", mappedTraitList);
        study.setMappedBackgroundTraits(mappedTraitList);
        DiseaseTrait backgroundTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getBackgroundTrait());
        study.setBackgroundTrait(backgroundTrait);

        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
        }
        study.setStudyDesignComment(studyDto.getArrayInformation());
        getLog().info("Saving study ...");
        studyService.save(study);
        getLog().info("[IMPORT] Study saved: {}", study.getId());

        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setStudyDescription(studyDto.getStudyDescription());
        studyExtension.setCohort(studyDto.getCohort());
        studyExtension.setCohortSpecificReference(studyDto.getCohortId());
        studyExtension.setStatisticalModel(studyDto.getStatisticalModel());
        studyExtension.setSummaryStatisticsFile(studyDto.getSummaryStatisticsFile());
        studyExtension.setSummaryStatisticsAssembly(studyDto.getSummaryStatisticsAssembly());
        studyExtension.setStudy(study);

        getLog().info("Saving study extension...");
        studyExtensionRepository.save(studyExtension);
        getLog().info("[IMPORT] Study extension saved: {}", studyExtension.getId());
        study.setStudyExtension(studyExtension);
        studyService.save(study);

        return study;
    }

}
