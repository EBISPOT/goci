package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 04/02/15
 */
public abstract class OntologyEnabledDocument<O> extends EmbeddableDocument<O> {
    @Field("traitUri") Set<String> traitUris;

    @Field("shortForm") private Set<String> shortForms;
    @Field("label") private Set<String> labels;
    @Field("synonym") private Set<String> synonyms;
    @Field("description") private Set<String> descriptions;

    @Field("efoLink") private Set<String> efoLinks;

    @Field("parent") private Set<String> superclassLabels;

    public OntologyEnabledDocument(O object) {
        super(object);
        this.traitUris = new HashSet<>();
        this.shortForms = new HashSet<>();
        this.labels = new HashSet<>();
        this.synonyms = new HashSet<>();
        this.descriptions = new HashSet<>();
        this.superclassLabels = new HashSet<>();
        this.efoLinks = new HashSet<>();
    }

    public Set<String> getTraitUris() {
        return traitUris;
    }

    public void addTraitUri(String traitUri) {
        traitUris.add(traitUri);
    }

    public void addTraitUris(Collection<String> traitUris) {
        this.traitUris.addAll(traitUris);
    }

    public void addShortForm(String shortForm) {
        shortForms.add(shortForm);
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public void addSynonym(String synonym) {
        synonyms.add(synonym);
    }

    public void addDescription(String description) {
        descriptions.add(description);
    }

    public void addSuperclassLabel(String superclassLabel) {
        superclassLabels.add(superclassLabel);
    }

    public void addEfoLink(String efoLink) {
        efoLinks.add(efoLink);
    }
}
