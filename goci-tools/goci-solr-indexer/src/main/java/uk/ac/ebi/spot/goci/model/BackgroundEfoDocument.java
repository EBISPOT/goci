package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Ala Abid
 * @date 26/05/21
 */
public class BackgroundEfoDocument extends OntologyEnabledDocument<EfoTrait> {
    @Field private String mappedBkgLabel;
    @Field private String mappedBkgUri;

    public BackgroundEfoDocument(EfoTrait efoTrait) {
        super(efoTrait);
        this.mappedBkgLabel = efoTrait.getTrait();
        this.mappedBkgUri = efoTrait.getUri();
        // addTraitUri(mappedBkgUri);
    }

    public String getMappedBkgLabel() {
        return mappedBkgLabel;
    }

    public String getMappedBkgUri() {
        return mappedBkgUri;
    }
}
