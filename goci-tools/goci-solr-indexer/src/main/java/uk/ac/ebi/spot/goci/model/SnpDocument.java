package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;

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

    public SnpDocument(SingleNucleotidePolymorphism snp) {
        this.id = "snp_".concat(snp.getId().toString());
        this.rsId = snp.getRsID();
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
