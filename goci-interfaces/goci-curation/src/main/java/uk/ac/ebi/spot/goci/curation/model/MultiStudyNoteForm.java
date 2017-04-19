package uk.ac.ebi.spot.goci.curation.model;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinhe on 03/04/2017.
 */
public class MultiStudyNoteForm  {



    @Valid
    private List<StudyNoteForm> nomalNoteForms = new ArrayList<StudyNoteForm>();

    @Valid
    private List<StudyNoteForm> systemNoteForms = new ArrayList<StudyNoteForm>();

    private Boolean canEdit = true;
    private Boolean editingMode = false;
    private Integer editIndex;

    public MultiStudyNoteForm() {

    }

    public MultiStudyNoteForm(List<StudyNoteForm> nomalNoteForms,
                              List<StudyNoteForm> systemNoteForms,
                              Boolean canEdit,
                              Boolean editingMode,
                              Integer editIndex) {
        this.nomalNoteForms = nomalNoteForms;
        this.systemNoteForms = systemNoteForms;
        this.canEdit = canEdit;
        this.editingMode = editingMode;
        this.editIndex = editIndex;
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

    public List<StudyNoteForm> getNomalNoteForms() {
        return nomalNoteForms;
    }

    public void setNomalNoteForms(List<StudyNoteForm> nomalNoteForms) {
        this.nomalNoteForms = nomalNoteForms;
    }

    public List<StudyNoteForm> getSystemNoteForms() {
        return systemNoteForms;
    }

    public void setSystemNoteForms(List<StudyNoteForm> systemNoteForms) {
        this.systemNoteForms = systemNoteForms;
    }


    public void startEdit(Integer index){
        setEditingMode(Boolean.TRUE);
        setEditIndex(index);
        nomalNoteForms.forEach(studyNoteForm -> {
            studyNoteForm.makeNotEditable();
        });
        this.getNomalNoteForms().get(index).startEdit();
    }

    public void finishEdit(){
        setEditingMode(Boolean.FALSE);
        nomalNoteForms.forEach(studyNoteForm -> {
            studyNoteForm.makeEditable();
        });
    }

    public void makeNotEditable(){
        setCanEdit(Boolean.FALSE);
        //disable edit for all study note
        nomalNoteForms.forEach(noteForm->{
            noteForm.makeNotEditable();
        });
    }
}
