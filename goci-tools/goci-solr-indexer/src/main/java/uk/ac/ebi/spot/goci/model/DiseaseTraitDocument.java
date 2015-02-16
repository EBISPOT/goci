package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class DiseaseTraitDocument extends EmbeddableDocument<DiseaseTrait> {
    // basic DiseaseTrait information
    @Field private String traitName;

    public DiseaseTraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.traitName = diseaseTrait.getTrait();
    }

    public String getTraitName() {
        return traitName;
    }
}
