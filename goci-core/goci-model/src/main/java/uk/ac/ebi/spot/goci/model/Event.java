package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by emma on 28/04/2016.
 *
 * @author emma
 *         <p>
 *         Model of tracking event
 */
@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToOne
    private SecureUser user;

    // JPA no-args constructor
    public Event() {
    }

    public Event(Date date, EventType eventType, SecureUser user) {
        this.date = date;
        this.eventType = eventType;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public SecureUser getUser() {
        return user;
    }

    public void setUser(SecureUser user) {
        this.user = user;
    }
}
