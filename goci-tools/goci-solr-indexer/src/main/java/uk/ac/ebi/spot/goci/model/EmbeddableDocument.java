package uk.ac.ebi.spot.goci.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
public abstract class EmbeddableDocument<O> extends OntologyEnabledDocument<O> {
    public EmbeddableDocument(O object) {
        super(object);
    }

    public void embed(Document document) {
        Map<String, Object> propertyValueMap = new HashMap<>();

        // inspect all getter properties on the document being embedded


    }
}
