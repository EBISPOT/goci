package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends Document<EfoTrait> {
    @Field private String trait;
    @Field private String traitUri;

    @Field private String shortForm;
    @Field("label") private Set<String> labels;
    @Field("synonym") private Set<String> synonyms;
    @Field("description") private Set<String> description;

    @Field("parent") private Set<String> superclassLabels;
    @Field("child") private Set<String> subclassLabels;

    @Field("*_rel") private Map<String, Set<String>> relations = new HashMap<>();

    public TraitDocument(EfoTrait efoTrait) {
        super(efoTrait);
        this.trait = efoTrait.getTrait();
        this.traitUri = efoTrait.getUri();
    }

    public String getTrait() {
        return trait;
    }

    public String getTraitUri() {
        return traitUri;
    }

    public String getShortForm() {
        return shortForm;
    }

    public void setShortForm(String shortForm) {
        this.shortForm = shortForm;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<String> getDescription() {
        return description;
    }

    public void setDescription(Set<String> description) {
        this.description = description;
    }

    public Set<String> getSuperclassLabels() {
        return superclassLabels;
    }

    public void setSuperclassLabels(Set<String> superclassLabels) {
        this.superclassLabels = superclassLabels;
    }

    public Set<String> getSubclassLabels() {
        return subclassLabels;
    }

    public void setSubclassLabels(Set<String> subclassLabels) {
        this.subclassLabels = subclassLabels;
    }

    public Map<String, Set<String>> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, Set<String>> relations) {
        this.relations = relations;
    }
}
