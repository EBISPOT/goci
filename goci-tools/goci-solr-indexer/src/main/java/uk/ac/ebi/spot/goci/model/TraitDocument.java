package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends Document<EfoTrait> {
    @Field private String trait;
    @Field private String uri;
    @Field private String resourcename;

    public TraitDocument(EfoTrait efoTrait) {
        super(efoTrait);
        this.trait = efoTrait.getTrait();
        this.uri = efoTrait.getUri();
        this.resourcename = efoTrait.getClass().getSimpleName();
    }

    public String getTrait() {
        return trait;
    }

    public String getUri() {
        return uri;
    }

    public String getResourcename() {
        return resourcename;
    }
}
