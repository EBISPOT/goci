package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends EmbeddableDocument<DiseaseTrait> {
    // basic DiseaseTrait information
    @Field private String traitName;

    public TraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.traitName = diseaseTrait.getTrait();
    }

    public String getTraitName() {
        return traitName;
    }
}
