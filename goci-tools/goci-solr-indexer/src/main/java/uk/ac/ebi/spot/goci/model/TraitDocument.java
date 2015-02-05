package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends OntologyEnabledDocument<DiseaseTrait> {
    @Field private String trait;

    public TraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.trait = diseaseTrait.getTrait();
    }

    public String getTrait() {
        return trait;
    }
}
