package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

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
public class SnpDocument extends Document<SingleNucleotidePolymorphism> {
    @Field private String rsId;
    @Field private String chromosomeName;
    @Field private int chromosomePosition;
    @Field("region") private Set<String> regions;
    @Field("gene") private Set<String> genes;
    @Field private String last_modified;
    @Field private String resourcename;

    public SnpDocument(SingleNucleotidePolymorphism snp) {
        super(snp);
        if (snp.getRsId() != null) {
            this.rsId = snp.getRsId();
        }
        if (snp.getChromosomeName() != null) {
            this.chromosomeName = snp.getChromosomeName();
        }
        if (snp.getChromosomePosition() != null) {
            this.chromosomePosition = Integer.parseInt(snp.getChromosomePosition());
        }
        this.regions = new HashSet<>();
        snp.getRegions().forEach(region -> regions.add(region.getName()));
        this.genes = new HashSet<>();
        snp.getGenes().forEach(gene -> genes.add(gene.getGeneName()));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (snp.getLastUpdateDate() != null) {
            this.last_modified = df.format(snp.getLastUpdateDate());
        }
        this.resourcename = snp.getClass().getSimpleName();
    }

    public String getRsId() {
        return rsId;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public int getChromosomePosition() {
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
                "id=" + getId() +
                ", rsId='" + rsId + '\'' +
                '}';
    }

}
