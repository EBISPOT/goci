package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 06/07/2016.
 *
 * @author emma
 *         <p>
 *         Association focused implementation of tracking behaviour
 */
@Service
public class AssociationTrackingOperationServiceImpl implements TrackingOperationService {

    private EventOperationsService eventOperationsService;

    @Autowired
    public AssociationTrackingOperationServiceImpl(EventOperationsService eventOperationsService) {
        this.eventOperationsService = eventOperationsService;
    }

    @Override public void create(Trackable trackable, SecureUser secureUser) {
        Event creationEvent = eventOperationsService.createEvent("ASSOCIATION_CREATION", secureUser);
        trackable.addEvent(creationEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser,String eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override
    public void update(Trackable trackable, SecureUser secureUser, String eventType, String description) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser, description);
        trackable.addEvent(updateEvent);
    }

    @Override public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent("ASSOCIATION_DELETION", secureUser);
        trackable.addEvent(deleteEvent);
    }
}
