package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Note;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.repository.NoteRepository;
import uk.ac.ebi.spot.goci.repository.NoteSubjectRepository;
import uk.ac.ebi.spot.goci.repository.StudyNoteRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by xinhe on 03/04/2017.
 * The service can be potentially useful for other services, for example, we might want to automatically generate notes for the mapping etc.
 */
@Service
public class StudyNoteService {
    private StudyNoteRepository studyNoteRepository;
    private NoteSubjectService noteSubjectService;
    private CuratorService curatorService;
    private NoteService noteService;
    private StudyService studyService;

    @Autowired
    public StudyNoteService(StudyNoteRepository studyNoteRepository,
                            NoteSubjectService noteSubjectService,
                            CuratorService curatorService,
                            NoteService noteService) {
        this.studyNoteRepository = studyNoteRepository;
        this.noteSubjectService = noteSubjectService;
        this.curatorService = curatorService;
        this.noteService = noteService;
        this.studyService = studyService;
    }




    public Collection<StudyNote> findByStudyId(Long studyId){
        Collection<StudyNote> studyNotes= studyNoteRepository.findByGenericId(studyId);
        return studyNotes;
    }

    public StudyNote findOne(Long id){
        return studyNoteRepository.findOne(id);
    }

    public void saveStudyNote(StudyNote studyNote){
        studyNoteRepository.save(studyNote);
    }

    public void deleteStudyNote(StudyNote studyNote) {
        studyNoteRepository.delete(studyNote);
    }

    public StudyNote createEmptyStudyNote(Study study, SecureUser user){
        StudyNote note = new StudyNote();
        note.setStudy(study);

        //defult curator will be the one who is currently adding the note
        //#xintodo this needs to be change when a forgin key is added the curator table from the user table
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        note.setCurator(curator);

        note.setStatus(false);
        note.setGenericId(study.getId());
        return note;
    }

    public StudyNote createAutomaticNote(String textNote, Study study, SecureUser user) {
        StudyNote note = createEmptyStudyNote(study, user);
        // System note subject
        note.setTextNote(textNote);
        NoteSubject subject = noteSubjectService.findAutomaticNote();
        note.setNoteSubject(subject);
        return note;
    }

    public StudyNote createGeneralNote( Study study, SecureUser user) {
        StudyNote note = createEmptyStudyNote(study, user);
        // general note subject
        NoteSubject subject = noteSubjectService.findGeneralNote();
        note.setNoteSubject(subject);
        return note;
    }

    // This method delete ALL the notes with study_id = Study (Eg. Study, Association)
    public void deleteAllNoteByStudy(Study study) {
        //only called from study, thus no need to check if study is published
        noteService.deleteAllNote(study);
    }

    public Boolean isSystemNote(StudyNote note){
        return noteSubjectService.isSystemNoteSubject(note.getNoteSubject());
    }

}

