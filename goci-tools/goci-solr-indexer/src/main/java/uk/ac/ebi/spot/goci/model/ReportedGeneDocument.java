package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 14/02/15
 */
public class ReportedGeneDocument extends Document<Gene> {
    @Field private String reportedGene;

    public ReportedGeneDocument(Gene gene) {
        super(gene);
        this.reportedGene = gene.getGeneName();
    }

    public String getReportedGene() {
        return reportedGene;
    }
}
