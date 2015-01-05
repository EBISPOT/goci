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
public class TraitAssociationDocument {
    @Id @Field
    private String id;
    @Field
    private String trait;
    @Field
    private String title;
    @Field
    private String rsId;
    @Field
    private String resourcename;

    public TraitAssociationDocument(TraitAssociation traitAssociation) {
        this.id = "trait_association_".concat(traitAssociation.getId().toString());
        this.trait = traitAssociation.getTrait();
        this.title = traitAssociation.getStudy().getTitle();
        this.rsId = traitAssociation.getSnp().getRsId();
        this.resourcename = traitAssociation.getClass().getSimpleName();
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

    public String getRsId() {
        return rsId;
    }

    public String getResourcename() {
        return resourcename;
    }
}
