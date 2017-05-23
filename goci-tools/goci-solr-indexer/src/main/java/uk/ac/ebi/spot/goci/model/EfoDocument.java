package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class EfoDocument extends OntologyEnabledDocument<EfoTrait> {
    @Field private String mappedLabel;
    @Field private String mappedUri;

    public EfoDocument(EfoTrait efoTrait) {
        super(efoTrait);
        this.mappedLabel = efoTrait.getTrait();
        this.mappedUri = efoTrait.getUri();
        addTraitUri(mappedUri);
    }

    public String getMappedLabel() {
        return mappedLabel;
    }

    public String getMappedUri() {
        return mappedUri;
    }
}
