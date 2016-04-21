package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.EnsemblGene;
import uk.ac.ebi.spot.goci.model.EntrezGene;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Locus;

import java.util.Collection;

/**
 * Created by emma on 07/04/2016.
 *
 * @author emma
 *         <p>
 *         Builder class for gene object
 */
public class GeneBuilder {

    private Gene gene = new Gene();

    public GeneBuilder setId(Long id) {
        gene.setId(id);
        return this;
    }

    public GeneBuilder setGeneName(String geneName) {
        gene.setGeneName(geneName);
        return this;
    }

    public GeneBuilder setEntrezGeneIds(Collection<EntrezGene> entrezGeneIds) {
        gene.setEntrezGeneIds(entrezGeneIds);
        return this;
    }

    public GeneBuilder setAuthorReportedFromLoci(Collection<Locus> authorReportedFromLoci) {
        gene.setAuthorReportedFromLoci(authorReportedFromLoci);
        return this;
    }

    public GeneBuilder setEnsemblGeneIds(Collection<EnsemblGene> ensemblGeneIds) {
        gene.setEnsemblGeneIds(ensemblGeneIds);
        return this;
    }

    public GeneBuilder setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        gene.setGenomicContexts(genomicContexts);
        return this;
    }

    public Gene build() {
        return gene;
    }
}