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
public class SnpDocument {
    @Id
    private String id;
    @Field
    private String rsId;
    @Field
    private String resourcename;

    public SnpDocument(Snp snp) {
        this.id = "snp_".concat(snp.getId().toString());
        this.rsId = snp.getRsId();
        this.resourcename = snp.getClass().getSimpleName();
    }

    public String getId() {
        return id;
    }

    public String getRsId() {
        return rsId;
    }

    public String getResourcename() {
        return resourcename;
    }

    @Override
    public String toString() {
        return "SnpDocument{" +
                "id=" + id +
                ", rsId='" + rsId + '\'' +
                '}';
    }

}
