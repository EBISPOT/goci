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
    @Field("label") private Set<String> label;
    @Field("synonym") private Set<String> synonym;
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

    public Set<String> getLabel() {
        return label;
    }

    public void setLabel(Set<String> label) {
        this.label = label;
    }

    public Set<String> getSynonym() {
        return synonym;
    }

    public void setSynonym(Set<String> synonym) {
        this.synonym = synonym;
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
