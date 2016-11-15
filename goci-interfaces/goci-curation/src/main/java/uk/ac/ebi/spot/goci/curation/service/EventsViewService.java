package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.curation.model.EventView;


import java.util.List;

/**
 * Created by emma on 22/07/2016.
 *
 * @author emma
 *         <p>
 *         Interface used to create a human readable version of event tracking that can be displayed via curation
 *         interface
 */
public interface EventsViewService {

    List<EventView> createViews(Long trackableId);

}