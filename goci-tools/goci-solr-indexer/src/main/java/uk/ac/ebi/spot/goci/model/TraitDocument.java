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
@SolrDocument(solrCoreName = "gwas")
public class TraitDocument {
    @Id @Field private String id;
    @Field private String trait;
    @Field private String uri;
    @Field private String title;
    @Field private String resourcename;

    public TraitDocument(EfoTrait efoTrait) {
        this.id = "disease_trait".concat(efoTrait.getId().toString());
        this.trait = efoTrait.getTrait();
        this.uri = efoTrait.getUri();

        this.resourcename = efoTrait.getClass().getSimpleName();
    }


    public String getResourcename() {
        return resourcename;
    }
}
