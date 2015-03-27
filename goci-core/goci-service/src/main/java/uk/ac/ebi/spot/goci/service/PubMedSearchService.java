package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

/**
 * A service that dispatches queries to PubMed and returns a study object
 *
 * @author Tony Burdett
 *         Date 26/10/11
 *         <p>
 *         Adapted by Emma (2015-01-16) based on code written by Tony.
 */
public interface PubMedSearchService {

    /**
     * Dispatches a PubMed summary query to obtain publication details for a PubMed ID. Details are then used to create a Study object
     *
     * @param pubmedId the PubMed ID to query for
     * @return uk.ac.ebi.spot.goci.model.Study
     * @throws uk.ac.ebi.spot.goci.service.exception.PubmedLookupException
     */
    Study findPublicationSummary(String pubmedId) throws PubmedLookupException;

}
