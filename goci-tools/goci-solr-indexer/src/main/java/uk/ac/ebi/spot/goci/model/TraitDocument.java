package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class TraitDocument extends OntologyEnabledDocument<DiseaseTrait> {
    @Field private String trait;
    @Field private Collection<String> rsIds;
    @Field private Collection<String> chromosomePositions;
    @Field private Collection<String> regions;

    public TraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.trait = diseaseTrait.getTrait();
    }

    public String getTrait() {
        return trait;
    }

    public void addRsId(String rsId) {
        rsIds.add(rsId);
    }

    public void addChromosomePosition(String chromosomePosition) {
        chromosomePositions.add(chromosomePosition);
    }

    public void addRegion(String region) {
        regions.add(region);

    }

    public Collection<String> getRsIds() {
        return rsIds;
    }

    public void setRsIds(Collection<String> rsIds) {
        this.rsIds = rsIds;
    }

    public Collection<String> getChromosomePositions() {
        return chromosomePositions;
    }

    public void setChromosomePositions(Collection<String> chromosomePositions) {
        this.chromosomePositions = chromosomePositions;
    }

    public Collection<String> getRegions() {
        return regions;
    }

    public void setRegions(Collection<String> regions) {
        this.regions = regions;
    }

    public void addMappedGene(Gene gene) {
        //TO DO - ADD MAPPED GENE STUFF

    }
}
