package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.EventOperationsService;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Ethnicity focused implementation of tracking behaviour
 */
@Service
public class EthnicityTrackingOperationServiceImpl implements TrackingOperationService {

    private EventOperationsService eventOperationsService;

    @Autowired
    public EthnicityTrackingOperationServiceImpl(EventOperationsService eventOperationsService) {
        this.eventOperationsService = eventOperationsService;
    }

    @Override public void create(Trackable trackable, SecureUser secureUser) {
        Event createEvent = eventOperationsService.createEvent(EventType.ETHNICITY_CREATED, secureUser);
        trackable.addEvent(createEvent);
    }

    @Override public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent(EventType.ETHNICITY_DELETED, secureUser);
        trackable.addEvent(deleteEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser, EventType eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override
    public void update(Trackable trackable, SecureUser secureUser, EventType eventType, String updateDescription) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser, updateDescription);
        trackable.addEvent(updateEvent);
    }
}
