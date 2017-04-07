package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MultiStudyNoteForm;
import uk.ac.ebi.spot.goci.curation.model.StudyNoteForm;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyNote;

import java.util.Collection;

/**
 * Created by xinhe on 04/04/2017.
 * This is a serive to convert studyNoteForm from/back the studyNote data object.
 */
@Service
public class StudyNoteOperationService {



    public StudyNoteOperationService() {
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
        StudyNoteForm row = new StudyNoteForm(note.getId(),note.getTextNote(),note.getNoteSubject(),
                                              note.getStatus(),note.getCurator(), note.getGenericId());
        //if study is published, disable all edit
        if(note.getStudy().getHousekeeping().getIsPublished()){
            row.setCanEdit(Boolean.FALSE);
        }
        return row;
    }


    public MultiStudyNoteForm generateMultiStudyNoteForm(Collection<StudyNote> notes, Study study){
        MultiStudyNoteForm snf = new MultiStudyNoteForm();
        if(!notes.isEmpty()){
            notes.forEach(studyNote -> {
                StudyNoteForm row = convertToStudyNoteForm(studyNote);
                snf.getNoteForms().add(row);
            });
        }
        snf.setId(study.getId());
        snf.setStudy(study);
        return snf;
    }








}
