package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.repository.EventRepository;

import java.util.Date;

/**
 * Created by emma on 06/05/2016.
 *
 * @author emma
 *         <p>
 *         Service class to handle common operations regarding events
 */
@Service
public class EventOperationsService {

    private EventRepository eventRepository;

    @Autowired
    public EventOperationsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Create an event
     *
     * @param eventType Event type to create
     * @param user      User Initiating event
     * @return Event object
     */
    public synchronized Event createEvent(String eventType, SecureUser user) {
        // Create and save event
        Event event = new Event();
        event.setEventDate(new Date());
        event.setEventType(eventType);
        event.setUser(user);
        eventRepository.save(event);
        return event;
    }

    /**
     * Create an event with a description
     *
     * @param eventType        Event type to create
     * @param user             User Initiating event
     * @param eventDescription Description of update event
     * @return Event object
     */
    public synchronized Event createEvent(String eventType, SecureUser user, String eventDescription) {
        // Create and save event
        Event event = new Event();
        event.setEventDate(new Date());
        event.setEventType(eventType);
        event.setUser(user);
        event.setEventDescription(eventDescription);
        eventRepository.save(event);
        return event;
    }
}