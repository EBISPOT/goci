package uk.ac.ebi.spot.goci.model;

import java.util.Collection;

/**
 * Created by emma on 28/04/2016.
 *
 * @author emma
 *         <p>
 *         A component that performs common operations on objects that will be tracked via an event tracking table
 */
public interface Trackable {
    /**
     * Add event to an objects current collection of events
     *
     * @param event  the event to add
     */
     void addEvent(Event event);
}

