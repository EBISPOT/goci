package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by emma on 28/04/2016.
 *
 * @author emma/cinzia
 *         <p>
 * @Note: This enum class is deprecated. DB table: EVENT_TYPE
 */

@Entity
public class EventType {

    @Id
    @GeneratedValue
    private Long id;

    private String action;

    private String eventType;

    private String translatedEvent;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTranslatedEvent() {
        return translatedEvent;
    }

    public void setTranslatedEvent(String translatedEvent) {
        this.translatedEvent = translatedEvent;
    }
}
