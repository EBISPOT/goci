package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MultiStudyNoteForm;
import uk.ac.ebi.spot.goci.curation.model.StudyNoteForm;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.NoteSubject;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.service.CuratorService;
import uk.ac.ebi.spot.goci.service.NoteSubjectService;
import uk.ac.ebi.spot.goci.service.StudyNoteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by xinhe on 04/04/2017.
 * This is a serive to convert studyNoteForm from/back the studyNote data object.
 */
@Service
public class StudyNoteOperationsService {

    CuratorService curatorService;
    NoteSubjectService noteSubjectService;
    StudyNoteService studyNoteService;


    public StudyNoteOperationsService() {
    }

    @Autowired
    public StudyNoteOperationsService(CuratorService curatorService,
                                      NoteSubjectService noteSubjectService,
                                      StudyNoteService studyNoteService) {
        this.curatorService = curatorService;
        this.noteSubjectService = noteSubjectService;
        this.studyNoteService = studyNoteService;
    }


    public StudyNote convertToStudyNote(StudyNoteForm studyNoteForm, Study study){
        StudyNote studyNote = new StudyNote(study);
        studyNote.setId(studyNoteForm.getId());
        studyNote.setNoteSubject(studyNoteForm.getNoteSubject());
        studyNote.setCurator(studyNoteForm.getCurator());
        studyNote.setStatus(studyNoteForm.getStatus());
        studyNote.setTextNote(studyNoteForm.getTextNote());
        return studyNote;
    }


    public StudyNoteForm convertToStudyNoteForm(StudyNote note){
        // #xintodo Should factory be use to create these instance?
        StudyNoteForm noteFrom = new StudyNoteForm(note.getId(),note.getTextNote(),
                                                   noteSubjectService.findOne(note.getNoteSubject().getId()),
                                                   note.getStatus(),curatorService.findOne(note.getCurator().getId()),
                                                   note.getGenericId(),note.getCreatedAt(),note.getUpdatedAt());
        if(isSystemNote(note)){
            noteFrom.setSystemNote(Boolean.TRUE);
            noteFrom.makeNotEditable();
        }else{
            noteFrom.setSystemNote(Boolean.FALSE);
            noteFrom.makeEditable();
        }
        return noteFrom;
    }


    public MultiStudyNoteForm generateMultiStudyNoteForm(Collection<StudyNote> notes, Study study,SecureUser user){
        MultiStudyNoteForm msnf = new MultiStudyNoteForm();
        if(!notes.isEmpty()){
            notes.forEach(studyNote -> {
                StudyNoteForm row = convertToStudyNoteForm(studyNote);
                //user can only edit there own note
                if(!canEdit(row,user))
                    row.makeNotEditable();
                if(isSystemNote(studyNote))
                    msnf.getSystemNoteForms().add(row);
                else
                    msnf.getNomalNoteForms().add(row);
            });
        }

        //if study is published, disable all edit
        if(study.getHousekeeping().getIsPublished()){
            msnf.makeNotEditable();
        }


        return msnf;
    }


    public boolean canEdit(StudyNoteForm studyNoteForm, SecureUser user){
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        return studyNoteForm.getCurator().getId() == curator.getId();
    }



    public List<StudyNoteForm> generateSystemNoteForms(Collection<StudyNote> notes){
        List<StudyNoteForm> sysNoteForms=new ArrayList<StudyNoteForm>();
        if(!notes.isEmpty()){
            filterSystemNote(notes).forEach(sysNote->{
                StudyNoteForm row = convertToStudyNoteForm(sysNote);
                sysNoteForms.add(row);
            });
        }
        return sysNoteForms;
    }

    public List<StudyNoteForm> generateNonSystemNoteForms(Collection<StudyNote> notes){
        List<StudyNoteForm> sysNoteForms=new ArrayList<StudyNoteForm>();
        if(!notes.isEmpty()){
            filterNomalNote(notes).forEach(sysNote->{
                StudyNoteForm row = convertToStudyNoteForm(sysNote);
                sysNoteForms.add(row);
            });
        }
        return sysNoteForms;
    }

    /**
     * Creat empty study note and set its subject base on the publish status of the study
     * @param study
     * @param user
     * @return
     */
    public StudyNote createEmptyStudyNote(Study study, SecureUser user){
        StudyNote note = new StudyNote();
        note.setStudy(study);

        //defult curator will be the one who is currently adding the note
        //#xintodo this needs to be change when a forgin key is added the curator table from the user table
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        note.setCurator(curator);

        note.setStatus(false);
        note.setGenericId(study.getId());

        if(study.getHousekeeping().getIsPublished()){
            // general note subject
            note.setNoteSubject(noteSubjectService.findBySubject("Post-publishing review"));
        }else{
            note.setNoteSubject(noteSubjectService.findGeneralNote());

        }
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

    public StudyNote createTagDuplicateNote(String textNote, Study study, SecureUser user) {
        StudyNote note = createEmptyStudyNote(study, user);
        note.setTextNote(textNote);
        NoteSubject subject = noteSubjectService.findTagDuplicateNote();
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



    public Boolean isSystemNote(StudyNote note){
        return noteSubjectService.isSystemNoteSubject(note.getNoteSubject());
    }

    public Boolean isPublicNote(StudyNote note){
        return note.getStatus();
    }



    public Collection<StudyNote> filterSystemNote(Collection<StudyNote> notes){
        List<StudyNote> sysNote=new ArrayList<StudyNote>();
        if(!notes.isEmpty()){
            notes.forEach(studyNote -> {
                if(isSystemNote(studyNote))
                    sysNote.add(studyNote);
            });
        }
        return sysNote;
    }

    public Collection<StudyNote> filterNomalNote(Collection<StudyNote> notes){
        List<StudyNote> nomalNote=new ArrayList<StudyNote>();
        if(!notes.isEmpty()){
            notes.forEach(studyNote -> {
                if(!isSystemNote(studyNote))
                    nomalNote.add(studyNote);
            });
        }
        return nomalNote;
    }

    public StudyNote duplicateNote(Study targetStudy, StudyNote noteToDuplicate, SecureUser user){
        Study sourceStudy = noteToDuplicate.getStudy();
        StudyNote note = createEmptyStudyNote(targetStudy,user);
        note.setCurator(noteToDuplicate.getCurator());
        note.setNoteSubject(noteToDuplicate.getNoteSubject());

        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        note.setCurator(curator);

        //we added some text to indicate that this is a duplicated note.
        //This is just a hack to distinguish dulicated note since we have study-note one to many as out note model atm
        note.setTextNote("Duplicated from study: ".concat(sourceStudy.getId().toString()).concat(" by ").concat(curator.getLastName()).concat("\n").concat(noteToDuplicate.getTextNote()));
        note.setStatus(noteToDuplicate.getStatus());
        return note;
    }


    public void updateDuplicatedNote(StudyNote copiedNote, SecureUser user) {

        String body = copiedNote.getTextNote();
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());

        Study sourceStudy = copiedNote.getStudy();

        copiedNote.setTextNote(body.concat("\n").concat("Duplicated to other studies with Pubmed ID ").concat(sourceStudy.getPublicationId().getPubmedId()).concat(" by ").concat(curator.getLastName()));

        studyNoteService.saveStudyNote(copiedNote);

    }
}
