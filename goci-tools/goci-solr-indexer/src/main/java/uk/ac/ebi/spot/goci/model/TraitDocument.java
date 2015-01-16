package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends Document<EfoTrait> {
    @Field private String trait;
    @Field private String traitUri;

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
}
