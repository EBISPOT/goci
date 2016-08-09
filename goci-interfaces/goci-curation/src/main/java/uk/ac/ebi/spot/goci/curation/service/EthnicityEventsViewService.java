package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.EthnicityEventView;
import uk.ac.ebi.spot.goci.curation.model.EventView;
import uk.ac.ebi.spot.goci.model.DeletedEthnicity;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.repository.DeletedEthnicityRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Ethnicity specific implementation of EventsViewService
 */
@Service
public class EthnicityEventsViewService implements EventsViewService {

    private EthnicityRepository ethnicityRepository;

    private DeletedEthnicityRepository deletedEthnicityRepository;

    @Autowired
    public EthnicityEventsViewService(DeletedEthnicityRepository deletedEthnicityRepository,
                                      EthnicityRepository ethnicityRepository) {
        this.deletedEthnicityRepository = deletedEthnicityRepository;
        this.ethnicityRepository = ethnicityRepository;
    }

    @Override public List<EventView> createViews(Long studyId) {
        List<EventView> views = new ArrayList<>();

        Collection<Ethnicity> ethnicityCollection = ethnicityRepository.findByStudyId(studyId);
        List<DeletedEthnicity> deletedEthnicityCollection = deletedEthnicityRepository.findByStudyId(studyId);

        if (!ethnicityCollection.isEmpty()) {
            ethnicityCollection.forEach(ethnicity -> {


                                            ethnicity.getEvents().forEach(event -> {
                                                String eventName = translateEventEnum(event.getEventType());
                                                EventView eventView =
                                                        new EthnicityEventView(eventName,
                                                                               event.getEventDate(),
                                                                               ethnicity.getId(),
                                                                               event.getUser().getEmail(), createEthnicitySummary(ethnicity));
                                                views.add(eventView);
                                            });
                                        }

            );
        }

        // Add deleted ethnicity info
/*        if (!deletedEthnicityCollection.isEmpty()) {
            deletedEthnicityCollection.forEach(ethnicity -> {
                                                   ethnicity.getEvents().forEach(event -> {
                                                       String eventName = translateEventEnum(event.getEventType());
                                                       EventView eventView =
                                                               new EthnicityEventView(eventName,
                                                                                      event.getEventDate(),
                                                                                      ethnicity.getId(),
                                                                                      event.getUser().getEmail(), null);
                                                       views.add(eventView);
                                                   });
                                               }

            );
        }*/

        return views;
    }

    private String createEthnicitySummary(Ethnicity ethnicity) {
        String ethnicitySummary = null;
        StringJoiner joiner = new StringJoiner("; ");
        joiner.add("Type: ".concat(ethnicity.getType()));
        joiner.add("Ancestry: ".concat(ethnicity.getEthnicGroup()));
        joiner.add("Country of recruitment: ".concat(ethnicity.getCountryOfRecruitment()));
        joiner.add("Country of origin: ".concat(ethnicity.getCountryOfOrigin()));
        ethnicitySummary = joiner.toString();
        return ethnicitySummary;
    }
}
