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
    @Column(name = "CREATED", updatable = false)
    private Date createdAt;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

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

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        String sp = "   ";
        sb.append(this.textNote).append(sp)
                .append("[").append(this.noteSubject.getSubject())
                .append(" / ").append(this.curator.getLastName())
                .append(" / ").append(this.updatedAt)
                .append("]");
        return sb.toString();
    }

    public String toStringForEamil() {
        StringBuilder sb = new StringBuilder();
        String sp = "\n";
        sb.append("[").append(this.noteSubject.getSubject())
                .append(" / ").append(this.curator.getLastName())
                .append(" / ").append(this.updatedAt)
                .append("]").append(sp).append(this.textNote);
        return sb.toString();
    }
}
