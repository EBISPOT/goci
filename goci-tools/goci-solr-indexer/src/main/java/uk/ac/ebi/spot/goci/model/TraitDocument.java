package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends OntologyEnabledDocument<DiseaseTrait> {
    // basic DiseaseTrait information
    @Field private String trait;

    public TraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.trait = diseaseTrait.getTrait();
    }

    public String getTrait() {
        return trait;
    }
}
