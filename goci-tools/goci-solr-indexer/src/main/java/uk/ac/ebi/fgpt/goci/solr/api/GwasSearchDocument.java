package uk.ac.ebi.fgpt.goci.solr.api;

import java.net.URI;
import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 15/12/14
 */
class GwasSearchDocument {
    String type;
    String title;
    String description;

    String id;
    String resourceId;

    Collection<URI> ontologyUris;
    Collection<String> ontologyLabels;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Collection<URI> getOntologyUris() {
        return ontologyUris;
    }

    public Collection<String> getOntologyLabels() {
        return ontologyLabels;
    }

    enum Type {
        STUDY,
        SNP,
        GENE,
        TRAIT
    }
}
