package uk.ac.ebi.spot.goci.curation.service.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.EventOperationsService;
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
        Event studyCreationEvent = eventOperationsService.createEvent(EventType.ASSOCIATION_CREATION, secureUser);
        trackable.addEvent(studyCreationEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser, EventType eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent(EventType.ASSOCIATION_DELETION, secureUser);
        trackable.addEvent(deleteEvent);
    }
}
