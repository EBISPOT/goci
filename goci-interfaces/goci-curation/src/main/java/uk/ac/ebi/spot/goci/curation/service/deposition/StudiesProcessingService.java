package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.NoteRepository;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;
import uk.ac.ebi.spot.goci.service.EnsemblRestTemplateService;
import uk.ac.ebi.spot.goci.service.EventOperationsService;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StudiesProcessingService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyNoteOperationsService noteOperationsService;

    @Autowired
    private NoteSubjectRepository noteSubjectRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SingleStudyProcessingService singleStudyProcessingService;

    @Autowired
    private EventOperationsService eventOperationsService;

    @Autowired
    private DepositionSampleService depositionSampleService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private DepositionAssociationService depositionAssociationService;

    @Autowired
    private DepositionStudiesImportService depositionStudiesImportService;

    @Autowired
    private EnsemblRestTemplateService ensemblRestTemplateService;

    public boolean processStudies(String submissionId, SecureUser currentUser, Publication publication, Curator curator, ImportLog importLog) {
        Stream<SubmissionImportStudy> submissionImportStudyStream = depositionStudiesImportService.streamBySubmissionId(submissionId);
        submissionImportStudyStream.forEach(submissionImportStudy -> process(submissionId, submissionImportStudy, currentUser, publication, curator, importLog));
        submissionImportStudyStream.close();

        long count = depositionStudiesImportService.countUnsuccessful(submissionId);
        getLog().info("Found {} unsuccessfully processed studies", count);
        return count == 0;
    }

    private void process(String submissionId, SubmissionImportStudy submissionImportStudy, SecureUser currentUser, Publication publication, Curator curator, ImportLog importLog) {
        submissionImportStudy = depositionStudiesImportService.enrich(submissionImportStudy);
        if (submissionImportStudy.getDepositionStudyDto() == null) {
            getLog().error("Unable to process study [{}] - to study object found.", submissionImportStudy.getId());
            ImportLogStep importStep = importLog.addStep(new ImportLogStep("Creating study from proxy [" + submissionImportStudy.getId() + "]", submissionId));
            importLog.addError("Unable to process study [" + submissionImportStudy.getId() + "] - to study object found.", "Creating study from proxy [" + submissionImportStudy.getId() + "]");
            importLog.updateStatus(importStep.getId(), ImportLog.FAIL);

            submissionImportStudy.setFinalized(true);
            submissionImportStudy.setSuccess(false);
            depositionStudiesImportService.save(submissionImportStudy);
            return;
        }

        DepositionStudyDto studyDto = submissionImportStudy.getDepositionStudyDto();
        getLog().info("[{}] Processing study: {} | {}.", submissionId, studyDto.getStudyTag(), studyDto.getAccession());
        ImportLogStep importStep = importLog.addStep(new ImportLogStep("Creating study [" + studyDto.getAccession() + "]", submissionId));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer studyNote = new StringBuffer(sdf.format(new Date()) + "\n");

        String studyTag = studyDto.getStudyTag();
        studyNote.append("created " + studyTag + "\n");

        Study study;
        List<EfoTrait> efoTraits;
        try {
            Pair<Study, List<EfoTrait>> pair = singleStudyProcessingService.processStudy(studyDto, publication);
            study = pair.getLeft();
            efoTraits = pair.getRight();
        } catch (Exception e) {
            getLog().error("Unable to create study [{} | {}]: {}", studyDto.getStudyTag(), studyDto.getAccession(), e.getMessage(), e);
            importLog.addError("Unable to create study [" + studyDto.getStudyTag() + " | " + studyDto.getAccession() + "]: " + e.getMessage(), "Creating study [" + studyDto.getAccession() + "]");
            importLog.updateStatus(importStep.getId(), ImportLog.FAIL);

            submissionImportStudy.setFinalized(true);
            submissionImportStudy.setSuccess(false);
            depositionStudiesImportService.save(submissionImportStudy);
            return;
        }
        Collection<Study> pubStudies = publication.getStudies();
        if (pubStudies == null) {
            pubStudies = new ArrayList<>();
        }
        pubStudies.add(study);
        publication.setStudies(pubStudies);
        studyService.save(study);
        publicationService.save(publication);
        if (studyDto.getAssociations() != null) {
            getLog().info("Found {} associations in the submission retrieved from the Deposition App.", studyDto.getAssociations().size());
            String eRelease = ensemblRestTemplateService.getRelease();
            for (DepositionAssociationDto associationDto : studyDto.getAssociations()) {
                Pair<Boolean, String> pair = depositionAssociationService.saveAssociation(currentUser, study, associationDto, efoTraits, eRelease, importLog);
                if (pair.getLeft()) {
                    studyNote.append(pair.getRight() + "\n");
                } else {
                    submissionImportStudy.setFinalized(true);
                    submissionImportStudy.setSuccess(false);
                    depositionStudiesImportService.save(submissionImportStudy);
                    return;
                }
            }
        }
        if (studyDto.getSamples() != null) {
            getLog().info("Found {} samples in the submission retrieved from the Deposition App.", studyDto.getSamples().size());
            for (DepositionSampleDto sampleDto : studyDto.getSamples()) {
                studyNote.append(depositionSampleService.saveSample(study, sampleDto, importLog) + "\n");
            }
        }

        getLog().info("Creating events ...");
        Event event = eventOperationsService.createEvent("STUDY_CREATION", currentUser, "Import study " + "creation");
        List<Event> events = new ArrayList<>();
        events.add(event);
        study.setEvents(events);
        getLog().info("Adding notes ...");
        this.addStudyNote(study, studyDto.getStudyTag(), studyNote.toString(), "STUDY_CREATION", curator,
                "Import study creation", currentUser);
        if (studyDto.getNotes() != null) {
            //find notes in study
            for (DepositionNoteDto noteDto : studyDto.getNotes()) {
                if (noteDto.getStudyTag().equals(studyTag)) {
                    this.addStudyNote(study, studyDto.getStudyTag(), noteDto.getNote(), noteDto.getStatus(),
                            curator, noteDto.getNoteSubject(), currentUser);
                }
            }
        }
        studyService.save(study);
        importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);

        submissionImportStudy.setFinalized(true);
        submissionImportStudy.setSuccess(true);
        depositionStudiesImportService.save(submissionImportStudy);
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


}
