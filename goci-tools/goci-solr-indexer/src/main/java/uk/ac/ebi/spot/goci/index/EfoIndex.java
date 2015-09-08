package uk.ac.ebi.spot.goci.index;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.EfoDocument;

/**
 * Created by dwelter on 24/08/15.
 */
public interface EfoIndex extends SolrCrudRepository<EfoDocument, String> {
}
