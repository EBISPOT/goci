package uk.ac.ebi.spot.goci.index;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.SnpDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22/12/14
 */
public interface SnpIndex extends SolrCrudRepository<SnpDocument, String> {
}
