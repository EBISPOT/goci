package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 04/08/2016.
 *
 * @author emma
 *         <p>
 *         Model object representing a deleted ancestry
 */
@Entity
public class DeletedAncestry {

    @Id
    private Long id;

    // Use study ID rather than study object,
    // this hopefully preserve event information even if the study is deleted
    private Long studyId;

    @OneToMany
    @JoinTable(name = "DELETED_ANCESTRY_EVENT",
               joinColumns = @JoinColumn(name = "DELETED_ANCESTRY_ID"),
               inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events = new ArrayList<>();


    public DeletedAncestry() {
    }

    public DeletedAncestry(Long id, Long studyId, Collection<Event> events) {
        this.id = id;
        this.studyId = studyId;
        this.events = events;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
}
