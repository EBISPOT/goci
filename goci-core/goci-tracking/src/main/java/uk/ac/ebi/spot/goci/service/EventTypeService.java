package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.repository.EventRepository;
import uk.ac.ebi.spot.goci.repository.EventTypeRepository;

/**
 * Created by emma on 23/05/2016.
 *
 * @author emma/cinzia
 *         <p>
 *         Service class to determine that type of event based on user selection
 *         Massive refactor to avoid ENUM problem
 */
@Service
public class EventTypeService {

    private EventTypeRepository eventTypeRepository;

    @Autowired
    public EventTypeService(EventTypeRepository eventTypeRepository) { this.eventTypeRepository = eventTypeRepository; }

    public String determineEventTypeFromStatus(CurationStatus status) {
        String eventTypeString = "";

        try {
            String action = status.getStatus().toString();
            EventType eventTypeRow = eventTypeRepository.findByAction(action);
            eventTypeString = eventTypeRow.getEventType();
        } catch (Exception exception) {
            EventType eventTypeDefaultRow = eventTypeRepository.findByAction("Study Unknown");
            eventTypeString = eventTypeDefaultRow.getEventType();
        }
        return eventTypeString;
    }

    /**
     * Determine event type based on curator
     *
     * @param curator curator to determine event type from
     * @return eventType
     */
    public String determineEventTypeFromCurator(Curator curator) {
        String eventTypeString = "";

        try {
            String action = curator.getLastName().toString();
            EventType eventTypeRow = eventTypeRepository.findByAction(action);
            eventTypeString = eventTypeRow.getEventType();
        } catch (Exception exception) {
            EventType eventTypeDefaultRow = eventTypeRepository.findByAction("Curator Unknown");
            eventTypeString = eventTypeDefaultRow.getEventType();
        }
        return eventTypeString;
    }

    public String translateEventByEventType(String eventType) {
        String translation = "";
        try {
            translation = eventTypeRepository.findByEventType(eventType).getTranslatedEvent();
        } catch (Exception exception) {
            translation = "Translation Event Not Available. Please contact the administrator.";
        }
        return translation;
    }

}
