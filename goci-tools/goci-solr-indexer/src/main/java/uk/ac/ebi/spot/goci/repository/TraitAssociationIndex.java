package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.TraitAssociationDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22/12/14
 */
public interface TraitAssociationIndex extends SolrCrudRepository<TraitAssociationDocument, String> {
}
