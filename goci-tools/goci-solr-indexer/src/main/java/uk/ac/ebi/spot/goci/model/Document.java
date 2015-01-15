package uk.ac.ebi.spot.goci.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.beans.Introspector;
import java.lang.reflect.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 15/01/15
 */
@SolrDocument(solrCoreName = "gwas")
public abstract class Document<O> {
    @Id @org.apache.solr.client.solrj.beans.Field private String id;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Document(String id, O object) {
        this.id = id;
    }

    public Document(O object) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Field f : object.getClass().getDeclaredFields()) {
                if (f.getAnnotation(javax.persistence.Id.class) != null) {
                    // this is the id field, extract the value
                    f.setAccessible(true);
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(f.get(object).toString());
                }
            }
            // if sb contains text, set it
            if (sb.length() > 0) {
                this.id = Introspector.decapitalize(object.getClass().getSimpleName())
                        .concat(":")
                        .concat(sb.toString());
            }
            else {
                getLog().warn("Trying to generate a solr document from an object with no @Id field " +
                                      "(" + object.getClass().getSimpleName() + ")");
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to extract @Id field value from this object, " +
                                               "please manually set the Id using Document(id, object);");
        }
    }

    public String getId() {
        return id;
    }

    @Override public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                '}';
    }
}
