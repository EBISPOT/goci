package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.HashSet;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 04/02/15
 */
public abstract class OntologyEnabledDocument<O> extends Document<O> {
    @Field("traitUri") private Set<String> traitUris;

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

    public void setTraitUris(Set<String> traitUris) {
        this.traitUris = traitUris;
    }

    public void addTraitUri(String traitUri) {
        traitUris.add(traitUri);
    }

    public Set<String> getShortForms() {
        return shortForms;
    }

    public void setShortForms(Set<String> shortForms) {
        this.shortForms = shortForms;
    }

    public void addShortForm(String shortForm) {
        shortForms.add(shortForm);
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public void addSynonym(String synonym) {
        synonyms.add(synonym);
    }

    public Set<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<String> descriptions) {
        this.descriptions = descriptions;
    }

    public void addDescription(String description) {
        descriptions.add(description);
    }

    public Set<String> getSuperclassLabels() {
        return superclassLabels;
    }

    public void setSuperclassLabels(Set<String> superclassLabels) {
        this.superclassLabels = superclassLabels;
    }

    public void addSuperclassLabel(String superclassLabel) {
        superclassLabels.add(superclassLabel);
    }

    public Set<String> getEfoLinks() {
        return efoLinks;
    }

    public void setEfoLinks(Set<String> efoLinks) {
        this.efoLinks = efoLinks;
    }

    public void addEfoLink(String efoLink) {
        efoLinks.add(efoLink);
    }
}
