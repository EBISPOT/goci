package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AncestryEventView;
import uk.ac.ebi.spot.goci.curation.model.EventView;
import uk.ac.ebi.spot.goci.model.DeletedAncestry;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.repository.DeletedAncestryRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.service.EventTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Ancestry specific implementation of EventsViewService
 */
@Service
public class AncestryEventsViewService implements EventsViewService {

    private AncestryRepository ancestryRepository;

    private DeletedAncestryRepository deletedAncestryRepository;

    private EventTypeService eventTypeService;

    @Autowired
    public AncestryEventsViewService(DeletedAncestryRepository deletedAncestryRepository,
                                     AncestryRepository ancestryRepository,
                                     EventTypeService eventTypeService) {
        this.deletedAncestryRepository = deletedAncestryRepository;
        this.ancestryRepository = ancestryRepository;
        this.eventTypeService = eventTypeService;
    }

    @Override public List<EventView> createViews(Long studyId) {
        List<EventView> views = new ArrayList<>();

        Collection<Ancestry> ancestryCollection = ancestryRepository.findByStudyId(studyId);
        List<DeletedAncestry> deletedAncestryCollection = deletedAncestryRepository.findByStudyId(studyId);

        if (!ancestryCollection.isEmpty()) {
            ancestryCollection.forEach(ancestry -> {


                                            ancestry.getEvents().forEach(event -> {
                                                String eventName = eventTypeService.translateEventByEventType(event.getEventType());
                                                EventView eventView =
                                                        new AncestryEventView(eventName,
                                                                              event.getEventDate(),
                                                                              ancestry.getId(),
                                                                              event.getUser().getEmail(), createAncestrySummary(ancestry));
                                                views.add(eventView);
                                            });
                                        }

            );
        }

        // Add deleted ancestry info
/*        if (!deletedAncestryCollection.isEmpty()) {
            deletedAncestryCollection.forEach(ancestry -> {
                                                   ancestry.getEvents().forEach(event -> {
                                                       String eventName = translateEventEnum(event.getEventType());
                                                       EventView eventView =
                                                               new AncestryEventView(eventName,
                                                                                      event.getEventDate(),
                                                                                      ancestry.getId(),
                                                                                      event.getUser().getEmail(), null);
                                                       views.add(eventView);
                                                   });
                                               }

            );
        }*/

        return views;
    }



    private String createAncestrySummary(Ancestry ancestry) {
        String ancestrySummary = null;
        StringJoiner joiner = new StringJoiner("; ");
        joiner.add("Type: ".concat(ancestry.getType()));
        joiner.add("Ancestry: ".concat(ancestry.getAncestralGroup()));
        joiner.add("Country of recruitment: ".concat(ancestry.getCountryOfRecruitment()));
        joiner.add("Country of origin: ".concat(ancestry.getCountryOfOrigin()));
        ancestrySummary = joiner.toString();
        return ancestrySummary;
    }

}
