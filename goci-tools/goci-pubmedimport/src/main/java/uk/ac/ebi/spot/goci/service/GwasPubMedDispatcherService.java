package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.exception.DispatcherException;
import uk.ac.ebi.spot.goci.ui.model.GwasStudy;

import java.util.Collection;
import java.util.Map;

/**
 * A service that periodically dispatches queries to PubMed and enters the resulting studies into the tracking system.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public interface GwasPubMedDispatcherService {
    /**
     * Dispatches the pubmed query associated with the service and returns the resulting PubMed IDs as a collection of
     * strings.
     *
     * @return the PubMed IDs that fulfil the search criteria
     * @throws uk.ac.ebi.spot.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Collection<String> dispatchSearchQuery() throws DispatcherException;

    /**
     * Dispatches a PubMed summary query to obtain titles for a collection of PubMed IDs. The title of each PubMed
     * article is entered into a Map, with a key of the PubMed ID that was used to retrieve it.
     *
     * @param pubmedIDs the collection of PubMed IDs to query for
     * @return a map associating each PubMed ID in the collection supplied as a parameter with its title
     * @throws uk.ac.ebi.spot.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Map<String, GwasStudy> dispatchSummaryQuery(Collection<String> pubmedIDs) throws DispatcherException;

    /**
     * Dispatches a PubMed fetch query to obtain the abstract text for a collection of PubMed IDs.  The abstract test
     * of each article is entered into a Map, with a key of the the PubMed ID that was used to retrieve it, and this
     * Map returned.
     *
     * @param pubmedIDs the collection of PubMed IDs to query for
     * @return a map associating each PubMed ID in the collection supplied as a parameter with its abstract
     * @throws uk.ac.ebi.spot.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Map<String, String> dispatchFetchQuery(Collection<String> pubmedIDs) throws DispatcherException;


    /**
     * Manually adds a PubMed ID to the dispatcher.  This will, at some point (immediately or at the next query run),
     * query PubMed for the supplied ID.  If found, a study for this PubMed record will be automatically added to the
     * tracking system.
     *
     * @param pubmedID the pubmed ID to dispatch a query for
     * @throws uk.ac.ebi.spot.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    public void addPubMedID(String pubmedID) throws DispatcherException;
}
