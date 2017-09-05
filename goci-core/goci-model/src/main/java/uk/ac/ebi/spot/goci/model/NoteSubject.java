package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

/**
 * Created by cinzia on 27/03/2017.
 */
@Entity
public class NoteSubject {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String subject;

    @OneToMany(mappedBy = "noteSubject")
    private Collection<Note> notes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    private String template;


    @PrePersist
    protected void onCreate() {
        lastUpdateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
