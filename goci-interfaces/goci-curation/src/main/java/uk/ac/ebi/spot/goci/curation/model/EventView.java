package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 22/07/2016.
 * @author emma
 */
public abstract class EventView {

    private Long trackableId;

    private String event;

    private Date eventDate;

    private String userEmail;

    public EventView(String event, Date eventDate, Long trackableId, String userEmail) {
        this.event = event;
        this.eventDate = eventDate;
        this.trackableId = trackableId;
        this.userEmail = userEmail;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getTrackableId() {
        return trackableId;
    }

    public void setTrackableId(Long trackableId) {
        this.trackableId = trackableId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
