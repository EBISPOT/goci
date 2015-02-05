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
public class SnpDocument extends OntologyEnabledDocument<SingleNucleotidePolymorphism> {
    @Field private String rsId;
    @Field private String chromosomeName;
    @Field private int chromosomePosition;
    @Field("region") private Set<String> regions;
    @Field("mappedGene") private Set<String> genes;
    @Field private String context;
    @Field private String last_modified;

    @Field("qualifier") private Set<String> qualifiers;

    public SnpDocument(SingleNucleotidePolymorphism snp) {
        super(snp);
        this.rsId = snp.getRsId();
        this.chromosomeName = snp.getChromosomeName();
        if (snp.getChromosomePosition() != null) {
            this.chromosomePosition = Integer.parseInt(snp.getChromosomePosition());
        }
        this.regions = new HashSet<>();
        snp.getRegions().forEach(region -> regions.add(region.getName()));
        this.genes = new HashSet<>();
        snp.getGenomicContexts().forEach(context -> genes.add(context.getGene().getGeneName()));
        this.context = snp.getFunctionalClass();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (snp.getLastUpdateDate() != null) {
            this.last_modified = df.format(snp.getLastUpdateDate());
        }
        this.qualifiers = new HashSet<>();
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

    public String getContext() {
        return context;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public Set<String> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(Set<String> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public void addQualifier(String qualifier) {
        this.qualifiers.add(qualifier);
    }

    @Override
    public String toString() {
        return "SnpDocument{" +
                "id=" + getId() +
                ", rsId='" + rsId + '\'' +
                '}';
    }

}
