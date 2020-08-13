package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.StudyNoteOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.NoteRepository;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;
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
                study = singleStudyProcessingService.processStudy(studyDto, publication);
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


}
