package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.EventView;
import uk.ac.ebi.spot.goci.curation.model.StudyEventView;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 *         <p>
 *         Study specific implementation of EventsViewService
 */
@Service
public class StudyEventsViewService implements EventsViewService {

    private StudyRepository studyRepository;

    @Autowired
    public StudyEventsViewService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    @Override public List<EventView> createViews(Long trackableId) {

        List<EventView> views = new ArrayList<>();
        Collection<Event> events = studyRepository.findOne(trackableId).getEvents();

        events.forEach(event -> {
            String eventName = translateEventEnum(event.getEventType());
            EventView eventView =
                    new StudyEventView(eventName, event.getEventDate(), trackableId, event.getUser().getEmail(), event.getEventDescription());
            views.add(eventView);
        });

        return views;
    }
}
