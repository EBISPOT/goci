package uk.ac.ebi.spot.goci.curation.model;

import com.sun.org.apache.xpath.internal.operations.Bool;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.NoteSubject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * Created by xinhe on 03/04/2017.
 */
public class StudyNoteForm  {

    private Long id;

    @NotNull
    @Size(min=1)
    //Thymeleaf will not return null for an text input, instead it returns an empty string of size 0.
    private String textNote;
    @NotNull
    private NoteSubject noteSubject;
    @NotNull
    private Boolean status;
    @NotNull
    private Curator curator;

    private Long genericId;

    private Boolean isSystemNote = Boolean.FALSE;
    private Boolean canEdit = Boolean.TRUE;
    private Boolean canRemove = Boolean.TRUE;
    private Boolean canSave = Boolean.FALSE;
    private Boolean canDiscard = Boolean.FALSE;
    private Boolean editing = Boolean.FALSE;



    public StudyNoteForm() {
    }

    public StudyNoteForm(Long id,
                         String textNote,
                         NoteSubject noteSubject,
                         Boolean status,
                         Curator curator, Long genericId) {
        this.id = id;
        this.textNote = textNote;
        this.noteSubject = noteSubject;
        this.status = status;
        this.curator = curator;
        this.genericId = genericId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }

    public NoteSubject getNoteSubject() {
        return noteSubject;
    }

    public void setNoteSubject(NoteSubject noteSubject) {
        this.noteSubject = noteSubject;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Curator getCurator() {
        return curator;
    }

    public void setCurator(Curator curator) {
        this.curator = curator;
    }

    public Long getGenericId() {
        return genericId;
    }

    public void setGenericId(Long genericId) {
        this.genericId = genericId;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(Boolean canRemove) {
        this.canRemove = canRemove;
    }

    public Boolean getCanSave() {
        return canSave;
    }

    public void setCanSave(Boolean canSave) {
        this.canSave = canSave;
    }

    public Boolean getCanDiscard() {
        return canDiscard;
    }

    public void setCanDiscard(Boolean canDiscard) {
        this.canDiscard = canDiscard;
    }

    public Boolean getEditing() {
        return editing;
    }

    public void setEditing(Boolean editing) {
        this.editing = editing;
    }

    public void setSystemNote(Boolean systemNote) {
        isSystemNote = systemNote;
    }

    public Boolean isSystemNoteFrom(){
        return isSystemNote;
    }

    public void startEdit() {
        this.setEditing(Boolean.TRUE);
        this.setCanDiscard(Boolean.TRUE);
        this.setCanSave(Boolean.TRUE);
        this.setCanEdit(Boolean.FALSE);
        this.setCanRemove(Boolean.FALSE);
    }

    public void finishEdit(){
        makeEditable();
    }


    public void makeNotEditable(){
        this.setCanEdit(Boolean.FALSE);
        this.setCanRemove(Boolean.FALSE);
        this.setCanSave(Boolean.FALSE);
        this.setCanDiscard(Boolean.FALSE);
        this.setEditing(Boolean.FALSE);
    }

    public void makeEditable(){
        this.setCanEdit(Boolean.TRUE);
        this.setCanRemove(Boolean.TRUE);
        this.setCanSave(Boolean.FALSE);
        this.setCanDiscard(Boolean.FALSE);
        this.setEditing(Boolean.FALSE);
    }


}
