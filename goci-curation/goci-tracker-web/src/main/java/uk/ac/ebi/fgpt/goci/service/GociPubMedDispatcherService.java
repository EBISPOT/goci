package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.exception.DispatcherException;

import java.util.Collection;
import java.util.Map;

/**
 * A service that periodically dispatches queries to PubMed and enters the resulting studies into the tracking system.
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public interface GociPubMedDispatcherService {
    /**
     * Dispatches the pubmed query associated with the service and returns the resulting PubMed IDs as a collection of
     * strings.
     *
     * @return the PubMed IDs that fulfil the search criteria
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Collection<String> dispatchSearchQuery() throws DispatcherException;

    /**
     * Dispatches a PubMed summary query to obtain titles for a collection of PubMed IDs. The title of each PubMed
     * article is entered into a Map, with a key of the PubMed ID that was used to retrieve it.
     *
     * @param pubmedIDs the collection of PubMed IDs to query for
     * @return a map associating each PubMed ID in the collection supplied as a parameter with its title
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Map<String, String> dispatchSummaryQuery(Collection<String> pubmedIDs) throws DispatcherException;

    /**
     * Dispatches a PubMed fetch query to obtain the abstract text for a collection of PubMed IDs.  The abstract test
     * of each article is entered into a Map, with a key of the the PubMed ID that was used to retrieve it, and this
     * Map returned.
     *
     * @param pubmedIDs the collection of PubMed IDs to query for
     * @return a map associating each PubMed ID in the collection supplied as a parameter with its abstract
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    Map<String, String> dispatchFetchQuery(Collection<String> pubmedIDs) throws DispatcherException;

    /**
     * Starts the dispatcher service, ensuring that it periodically dispatches pubmed queries according to it's
     * configured schedule.  Studies are automatically added to the tracking system each time they are detected by the
     * dispatcher.
     *
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    void startDispatcher() throws DispatcherException;

    /**
     * Stops the dispatcher service.  No further queries will be dispatched.
     *
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    void stopDispatcher() throws DispatcherException;

    /**
     * Manually adds a PubMed ID to the dispatcher.  This will, at some point (immediately or at the next query run),
     * query PubMed for the supplied ID.  If found, a study for this PubMed record will be automatically added to the
     * tracking system.
     *
     * @param pubmedID the pubmed ID to dispatch a query for
     * @throws uk.ac.ebi.fgpt.goci.exception.DispatcherException
     *          if a problem was encountered whilst querying PubMed
     */
    public void addPubMedID(String pubmedID) throws DispatcherException;
}
