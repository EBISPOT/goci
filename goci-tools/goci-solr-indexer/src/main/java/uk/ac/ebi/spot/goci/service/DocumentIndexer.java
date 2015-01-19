package uk.ac.ebi.spot.goci.service;

import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
public class DocumentIndexer<D> {
    private SolrCrudRepository<D, String> index;

    public DocumentIndexer(SolrCrudRepository<D, String> index) {
        this.index = index;
    }

    public void index(D document) {
        index.save(document);
    }

    public void index(Iterable<D> documents) {
        index.save(documents);
    }
}
