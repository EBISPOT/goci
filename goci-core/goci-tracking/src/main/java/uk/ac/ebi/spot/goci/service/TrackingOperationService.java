package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 13/05/2016.
 *
 * @author emma
 *         <p>
 *         A component that manages operations on objects that require event tracking
 */
public interface TrackingOperationService {
    /**
     * Method to create a creation event
     *
     * @param trackable  Trackable object that requires event tracking
     * @param secureUser User initiating event
     */
    void create(Trackable trackable, SecureUser secureUser);

    /**
     * Method to create an update event
     *
     * @param trackable  Trackable object that requires event tracking
     * @param secureUser User initiating event
     */
    void delete(Trackable trackable, SecureUser secureUser);

    /**
     * Method to create an update event
     *
     * @param trackable  Trackable object that requires event tracking
     * @param secureUser User initiating event
     */
    void update(Trackable trackable, SecureUser secureUser, String eventType);

    /**
     * Method to create an update event
     *
     * @param trackable         Trackable object that requires event tracking
     * @param secureUser        User initiating event
     * @param updateDescription Description of update event
     */
    void update(Trackable trackable, SecureUser secureUser, String eventType, String updateDescription);

}
