package uk.ac.ebi.spot.goci.model;

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
     * @param event the event to add to study
     */
    void addEvent(Event event);
}