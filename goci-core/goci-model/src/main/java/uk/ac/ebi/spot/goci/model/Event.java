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
    private Date eventDate;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToOne
    private SecureUser secureUser;

    // JPA no-args constructor
    public Event() {
    }

    public Event(Date eventDate, EventType eventType, SecureUser secureUser) {
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.secureUser = secureUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public SecureUser getSecureUser() {
        return secureUser;
    }

    public void setSecureUser(SecureUser secureUser) {
        this.secureUser = secureUser;
    }
}
