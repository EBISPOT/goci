package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.StudyDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 22/12/14
 */
public interface StudyIndex extends SolrCrudRepository<StudyDocument, String> {
}
