package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by cinzia on 27/03/2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CONTENT_TYPE")
public abstract class Note {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Study study;

    @OneToOne
    private NoteSubject noteSubject;

    @NotNull
    private String textNote;

    private Boolean status = false;

    @OneToOne
    private Curator curator;

    @NotNull
    private Long genericId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATED")
    private Date createdAt;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() { createdAt= new Date(); }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    // JPA no-args constructor
    public Note() {
    }

    public Note(Study study) {
        this.study = study;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
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

    public Boolean getStatus() { return status; }

    public void setStatus(Boolean status) { this.status = status; }

    public Curator getCurator() { return curator; }

    public void setCurator(Curator curator) { this.curator = curator; }

    public Long getGenericId() {
        return genericId;
    }

    public void setGenericId(Long genericId) {
        this.genericId = genericId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
