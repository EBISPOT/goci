package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MultiStudyNoteForm;
import uk.ac.ebi.spot.goci.curation.model.StudyNoteForm;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;
import uk.ac.ebi.spot.goci.service.CuratorService;
import uk.ac.ebi.spot.goci.service.NoteSubjectService;
import uk.ac.ebi.spot.goci.service.StudyNoteService;

import java.util.Collection;

/**
 * Created by xinhe on 04/04/2017.
 * This is a serive to convert studyNoteForm from/back the studyNote data object.
 */
@Service
public class StudyNoteOperationService {

    CuratorService curatorService;
    NoteSubjectService noteSubjectService;
    StudyNoteService studyNoteService;

    public StudyNoteOperationService() {
    }

    @Autowired
    public StudyNoteOperationService(CuratorService curatorService,
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
                                                   note.getGenericId());
        if(studyNoteService.isSystemNote(note)){
            noteFrom.setSystemNote(Boolean.TRUE);
            noteFrom.makeNotEditable();
        }else{
            noteFrom.setSystemNote(Boolean.FALSE);
            noteFrom.makeEditable();
        }


        return noteFrom;
    }


    public MultiStudyNoteForm generateMultiStudyNoteForm(Collection<StudyNote> notes, Study study){
        MultiStudyNoteForm msnf = new MultiStudyNoteForm();
        if(!notes.isEmpty()){
            notes.forEach(studyNote -> {
                StudyNoteForm row = convertToStudyNoteForm(studyNote);
                msnf.getNoteForms().add(row);
            });
        }
        //if study is published, disable all edit
        if(study.getHousekeeping().getIsPublished()){
            msnf.makeNotEditable();
        }else{

        }
        return msnf;
    }








}
