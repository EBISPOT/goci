package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class MappedGeneDocument extends Document<Gene> {
    @Field private String mappedGene;

    public MappedGeneDocument(Gene gene) {
        super(gene);
        this.mappedGene = gene.getGeneName();
    }

    public String getMappedGene() {
        return mappedGene;
    }
}
