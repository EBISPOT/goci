package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Trackable;

/**
 * Created by emma on 13/05/2016.
 *
 * @author emma
 *         <p>
 *         Study focused implementation of tracking behaviour
 */
@Service
public class StudyTrackingOperationServiceImpl implements TrackingOperationService {
    private EventOperationsService eventOperationsService;

    @Autowired
    public StudyTrackingOperationServiceImpl(EventOperationsService eventOperationsService) {
        this.eventOperationsService = eventOperationsService;
    }

    @Override public void create(Trackable trackable, SecureUser secureUser) {
        Event studyCreationEvent = eventOperationsService.createEvent("STUDY_CREATION", secureUser);
        trackable.addEvent(studyCreationEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser, String eventType) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser);
        trackable.addEvent(updateEvent);
    }

    @Override public void update(Trackable trackable, SecureUser secureUser, String eventType, String description) {
        Event updateEvent = eventOperationsService.createEvent(eventType, secureUser, description);
        trackable.addEvent(updateEvent);
    }

    @Override public void delete(Trackable trackable, SecureUser secureUser) {
        Event deleteEvent = eventOperationsService.createEvent("STUDY_DELETION", secureUser);
        trackable.addEvent(deleteEvent);
    }
}
