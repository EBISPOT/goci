package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Ancestry focused implementation of tracking behaviour
 */
@Service
public class AncestryTrackingOperationServiceImpl implements TrackingOperationService {

    private EventOperationsService eventOperationsService;

    @Autowired
    public AncestryTrackingOperationServiceImpl(EventOperationsService eventOperationsService) {
        this.eventOperationsService = eventOperationsService;
    }

    @Override public void create(Trackable trackable, SecureUser secureUser) {
        Event createEvent = eventOperationsService.createEvent("ANCESTRY_CREATED", secureUser);
        trackable.addEvent(createEvent);
    }

    @Override public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent("ANCESTRY_DELETED", secureUser);
        trackable.addEvent(deleteEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser, String eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override
    public void update(Trackable trackable, SecureUser secureUser, String eventType, String description) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser, description);
        trackable.addEvent(updateEvent);
    }
}
