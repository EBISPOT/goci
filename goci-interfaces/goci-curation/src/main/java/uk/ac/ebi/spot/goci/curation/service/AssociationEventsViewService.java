package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationEventView;
import uk.ac.ebi.spot.goci.curation.model.EventView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.EventTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 *         <p>
 *         Association specific implementation of EventsViewService
 */
@Service
public class AssociationEventsViewService implements EventsViewService {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private StudyRepository studyRepository;
    private EventTypeService eventTypeService;

    @Autowired
    public AssociationEventsViewService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                        StudyRepository studyRepository,
                                        EventTypeService eventTypeService) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.studyRepository = studyRepository;
        this.eventTypeService = eventTypeService;
    }

    @Override public List<EventView> createViews(Long studyId) {

        List<EventView> views = new ArrayList<>();

        Collection<Association> associations = studyRepository.findOne(studyId).getAssociations();

        if (!associations.isEmpty()) {

            // For each association gather up the events into a collection of views
            associations.forEach(association -> {

                Collection<Event> events = association.getEvents();
                Long associationId = association.getId();
                Collection<SingleNucleotidePolymorphism> snps =
                        singleNucleotidePolymorphismRepository.findByRiskAllelesLociAssociationId(associationId);

                // Create a summary of SNPs in the association
                String associationSummary;
                StringJoiner snpJoiner = new StringJoiner(", ");
                snps.forEach(singleNucleotidePolymorphism -> {
                    snpJoiner.add(singleNucleotidePolymorphism.getRsId());
                });
                associationSummary = snpJoiner.toString();

                events.forEach(event -> {
                    String eventName = eventTypeService.translateEventByEventType(event.getEventType());
                    EventView eventView =
                            new AssociationEventView(eventName,
                                                     event.getEventDate(),
                                                     associationId,
                                                     event.getUser().getEmail(),
                                                     associationSummary);
                    views.add(eventView);
                });
            });
        }
        return views;
    }


}
