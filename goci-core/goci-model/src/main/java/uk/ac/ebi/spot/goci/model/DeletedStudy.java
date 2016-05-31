package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 31/05/2016.
 *
 * @author emma
 *         <p>
 *         Model object to store details of a deleted study
 */
@Entity
public class DeletedStudy {
    @Id
    private Long id;

    private String title;

    private String pubmedId;

    @OneToMany
    @JoinTable(name = "DELETED_STUDY_EVENT",
               joinColumns = @JoinColumn(name = "DELETED_STUDY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();

    // JPA no-args constructor
    public DeletedStudy() {
    }

    public DeletedStudy(Long id,
                        String title,
                        String pubmedId,
                        Collection<Event> events) {
        this.id = id;
        this.title = title;
        this.pubmedId = pubmedId;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }
}
