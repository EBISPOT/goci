package uk.ac.ebi.spot.goci.curation.model;

import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.NoteSubject;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by xinhe on 03/04/2017.
 */
public class StudyNoteForm  {

    private Long id;
    @NotNull
    private String textNote;
    @NotNull
    private NoteSubject noteSubject;
    @NotNull
    private Boolean status;
    @NotNull
    private Curator curator;

    private Long genericId;


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

}
