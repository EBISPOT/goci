package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 */
public class StudyEventView extends EventView {

    private String eventDescription;

    public StudyEventView(String event,
                          Date eventDate,
                          Long trackableId,
                          String userEmail,
                          String eventDescription) {
        super(event, eventDate, trackableId, userEmail);
        this.eventDescription = eventDescription;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
