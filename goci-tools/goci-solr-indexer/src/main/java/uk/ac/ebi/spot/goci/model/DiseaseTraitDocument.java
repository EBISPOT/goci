package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
@SolrDocument(solrCoreName = "gwas")
public class DiseaseTraitDocument {
    @Id @Field private String id;
    @Field private String trait;
    @Field private String title;
    @Field private String rsId;
    @Field private String resourcename;

    public DiseaseTraitDocument(DiseaseTrait diseaseTrait) {
        this.id = "disease_trait".concat(diseaseTrait.getId().toString());
        this.trait = diseaseTrait.getTrait();
        //        this.title = diseaseTrait.getStudy().getTitle();
        //        this.rsId = diseaseTrait.getSnp().getRsId();
        this.resourcename = diseaseTrait.getClass().getSimpleName();
    }

    public String getId() {
        return id;
    }

    public String getTrait() {
        return trait;
    }

    public String getTitle() {
        return title;
    }

    //    public String getRsId() {
    //        return rsId;
    //    }

    public String getResourcename() {
        return resourcename;
    }
}
