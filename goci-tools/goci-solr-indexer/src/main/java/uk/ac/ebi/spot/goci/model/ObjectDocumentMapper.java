package uk.ac.ebi.spot.goci.model;

import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
public class ObjectDocumentMapper<O, D> {
    private boolean progressEnabled = false;

    private ObjectConverter<O, D> objectConverter;
    private DocumentIndexer<D> documentIndexer;

    public ObjectDocumentMapper(Class<D> documentType, SolrCrudRepository<D, String> index) {
        this.objectConverter = new ObjectConverter<>(documentType);
        this.documentIndexer = new DocumentIndexer<>(index);
    }

    public ObjectDocumentMapper(Class<D> documentType, SolrCrudRepository<D, String> index, boolean enableProgress) {
        this(documentType, index);
        this.progressEnabled = enableProgress;
    }


    public void map(O object) {
        D document = objectConverter.convert(object);
        documentIndexer.index(document);
        if (progressEnabled) {
            System.out.print(".");
        }
    }
}
