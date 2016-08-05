package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 */
public class EthnicityEventView extends EventView {
    public EthnicityEventView(String event, Date eventDate, Long trackableId, String userEmail) {
        super(event, eventDate, trackableId, userEmail);
    }
}
