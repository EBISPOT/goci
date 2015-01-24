package uk.ac.ebi.spot.goci.index;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.AssociationDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
public interface AssociationIndex extends SolrCrudRepository<AssociationDocument, String> {
}
