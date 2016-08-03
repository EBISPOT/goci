package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 */
public class StudyEventView extends EventView {
    public StudyEventView(String event, Date eventDate, Long trackableId, String userEmail) {
        super(event, eventDate, trackableId, userEmail);
    }
}
