package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.spot.goci.model.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/01/15
 */
public abstract class ObjectDocumentMapper<O, D extends Document<O>> {
    @Autowired ObjectConverter objectConverter;

    private Class<D> documentType;
    private SolrCrudRepository<D, String> index;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public ObjectDocumentMapper(Class<D> documentType, SolrCrudRepository<D, String> index) {
        this.documentType = documentType;
        this.index = index;
    }

    public D map(O object) {
        D document = objectConverter.convert(object, documentType);
        index.save(document);
        return document;
    }

    public List<D> map(List<O> objects) {
        if (objects.size() > 0) {
            List<D> documents = new ArrayList<>();
            objects.stream()
                    .map(object -> objectConverter.convert(object, documentType))
                    .filter(doc -> doc != null)
                    .forEach(documents::add);
            index.save(documents);
            return documents;
        }
        else {
            getLog().warn("Attempting to map empty collection [" + objects.toString() + "]");
            return Collections.emptyList();
        }
    }
}
