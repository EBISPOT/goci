package uk.ac.ebi.spot.goci.curation.model;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhe on 03/04/2017.
 */
public class MultiStudyNoteForm  {


    @Valid
    private List<StudyNoteForm> noteForms = new ArrayList<StudyNoteForm>();

    private Boolean canEdit = true;
    private Boolean editingMode = false;
    private Integer editIndex;

    public MultiStudyNoteForm() {

    }

    public MultiStudyNoteForm(List<StudyNoteForm> noteForms,
                              Boolean canEdit,
                              Boolean editingMode,
                              Integer editIndex) {
        this.noteForms = noteForms;
        this.canEdit = canEdit;
        this.editingMode = editingMode;
        this.editIndex = editIndex;
    }

    public List<StudyNoteForm> getNoteForms() {
        return noteForms;
    }

    public void setNoteForms(List<StudyNoteForm> noteForms) {
        this.noteForms = noteForms;
    }

    public Boolean getEditingMode() {
        return editingMode;
    }

    public void setEditingMode(Boolean editingMode) {
        this.editingMode = editingMode;
    }


    public Integer getEditIndex() {
        return editIndex;
    }

    public void setEditIndex(Integer editIndex) {
        this.editIndex = editIndex;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public void startEdit(Integer index){
        setEditingMode(Boolean.TRUE);
        setEditIndex(index);
        this.getNoteForms().forEach(studyNoteForm -> {
            studyNoteForm.makeNotEditable();
        });
        this.getNoteForms().get(index).startEdit();
    }

    public void finishEdit(){
        setEditingMode(Boolean.FALSE);
        this.getNoteForms().forEach(studyNoteForm -> {
            studyNoteForm.makeEditable();
        });
    }

    public void makeNotEditable(){
        setCanEdit(Boolean.FALSE);
        //disable edit for all study note
        noteForms.forEach(noteForm->{
            noteForm.makeNotEditable();
        });
    }
}
