package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
@SolrDocument(solrCoreName = "gwas")
public class SnpDocument {
    @Id @Field private String id;
    @Field private String rsId;
    @Field private String chromosomeName;
    @Field private String chromosomePosition;
    @Field("region") private Set<String> regions;
    @Field("gene") private Set<String> genes;
    @Field private String last_modified;
    @Field private String resourcename;

    public SnpDocument(SingleNucleotidePolymorphism snp) {
        this.id = "snp_".concat(snp.getId().toString());
        this.rsId = snp.getRsId();
        this.chromosomeName = snp.getChromosomeName();
        this.chromosomePosition = snp.getChromosomePosition();
        this.regions = new HashSet<>();
        snp.getRegions().forEach(region -> regions.add(region.getName()));
        this.genes = new HashSet<>();
        snp.getGenes().forEach(gene -> genes.add(gene.getGeneName()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.last_modified = df.format(snp.getLastUpdateDate());
        this.resourcename = snp.getClass().getSimpleName();
    }

    public String getId() {
        return id;
    }

    public String getRsId() {
        return rsId;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public Set<String> getRegions() {
        return regions;
    }

    public Set<String> getGenes() {
        return genes;
    }

    public String getLast_modified() {
        return last_modified;
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
