package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         A model object representing a gene and its attributes including associated single nucleotide polymorphisms
 */

@Entity
public class Gene {
    @Id
    @GeneratedValue
    private Long id;

    private String geneName;

    private String entrezGeneId;

    private String ensemblGeneId;

    @ManyToMany(mappedBy = "authorReportedGenes")
    private Collection<Locus> authorReportedFromLoci;

    @OneToMany(mappedBy = "gene")
    private Collection<GenomicContext> genomicContexts;

    // JPA no-args constructor
    public Gene() {
    }

    // Light constructor
    public Gene(String geneName,
                String entrezGeneId,
                String ensemblGeneId) {
        this.geneName = geneName;
        this.entrezGeneId = entrezGeneId;
        this.ensemblGeneId = ensemblGeneId;
    }

    public Gene(String geneName,
                String entrezGeneId,
                String ensemblGeneId,
                Collection<Locus> authorReportedFromLoci,
                Collection<GenomicContext> genomicContexts) {
        this.geneName = geneName;
        this.entrezGeneId = entrezGeneId;
        this.ensemblGeneId = ensemblGeneId;
        this.authorReportedFromLoci = authorReportedFromLoci;
        this.genomicContexts = genomicContexts;
    }

    public Gene(String geneName) {
        this.geneName = geneName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public Collection<Locus> getAuthorReportedFromLoci() {
        return authorReportedFromLoci;
    }

    public void setAuthorReportedFromLoci(Collection<Locus> authorReportedFromLoci) {
        this.authorReportedFromLoci = authorReportedFromLoci;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

    @Override public String toString() {
        return "Gene{" +
                "id=" + id +
                ", geneName='" + geneName + '\'' +
                ", entrezGeneId='" + entrezGeneId + '\'' +
                ", ensemblGeneId='" + ensemblGeneId + '\'' +
                ", authorReportedFromLoci=" + authorReportedFromLoci +
                ", genomicContexts=" + genomicContexts +
                '}';
    }
}
