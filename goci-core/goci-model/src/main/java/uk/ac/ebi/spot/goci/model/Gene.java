package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         A model object representing a gene and its attributes including associated single nucleotide polymorphisms
 */

@Entity
public class Gene {
    @Id
    @GeneratedValue
    private Long id;

    private String geneName;

    @OneToMany
    @JoinTable(name = "GENE_ENTREZ_GENE",
               joinColumns = @JoinColumn(name = "GENE_ID"),
               inverseJoinColumns = @JoinColumn(name = "ENTREZ_GENE_ID"))
    @JsonManagedReference
    private Collection<EntrezGene> entrezGeneIds = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "GENE_ENSEMBL_GENE",
               joinColumns = @JoinColumn(name = "GENE_ID"),
               inverseJoinColumns = @JoinColumn(name = "ENSEMBL_GENE_ID"))
    @JsonManagedReference
    private Collection<EnsemblGene> ensemblGeneIds = new ArrayList<>();

    @ManyToMany(mappedBy = "authorReportedGenes")
    @JsonBackReference
    @RestResource(exported = false)
    private Collection<Locus> authorReportedFromLoci;

    @OneToMany(mappedBy = "gene")
    @JsonBackReference
    @RestResource(exported = false)
    private Collection<GenomicContext> genomicContexts;

//    @ManyToMany(mappedBy = "genes")
//    @ManyToMany
//    @JoinTable(name = "ASSOCIATION_GENE_VIEW",
//               joinColumns = @JoinColumn(name = "GENE_ID"),
//               inverseJoinColumns = @JoinColumn(name = "ASSOCIATION_ID"))
//    @RestResource(exported = false)
//    @JsonBackReference
//    private Collection<Association> associations;


//    @ManyToMany(mappedBy = "genes")
//    @JsonBackReference
//    @RestResource(exported = false)
//    private Collection<SingleNucleotidePolymorphism> snps;

    // JPA no-args constructor
    public Gene() {
    }

    // Light constructor
    public Gene(String geneName,
                Collection<EntrezGene> entrezGeneIds,
                Collection<EnsemblGene> ensemblGeneIds) {
        this.geneName = geneName;
        this.entrezGeneIds = entrezGeneIds;
        this.ensemblGeneIds = ensemblGeneIds;
    }

    public Gene(String geneName,
                Collection<EntrezGene> entrezGeneIds,
                Collection<EnsemblGene> ensemblGeneIds,
                Collection<Locus> authorReportedFromLoci,
                Collection<GenomicContext> genomicContexts) {
        this.geneName = geneName;
        this.entrezGeneIds = entrezGeneIds;
        this.ensemblGeneIds = ensemblGeneIds;
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

    public Collection<EntrezGene> getEntrezGeneIds() {
        return entrezGeneIds;
    }

    public void setEntrezGeneIds(Collection<EntrezGene> entrezGeneIds) {
        this.entrezGeneIds = entrezGeneIds;
    }

    public Collection<Locus> getAuthorReportedFromLoci() {
        return authorReportedFromLoci;
    }

    public void setAuthorReportedFromLoci(Collection<Locus> authorReportedFromLoci) {
        this.authorReportedFromLoci = authorReportedFromLoci;
    }

    public Collection<EnsemblGene> getEnsemblGeneIds() {
        return ensemblGeneIds;
    }

    public void setEnsemblGeneIds(Collection<EnsemblGene> ensemblGeneIds) {
        this.ensemblGeneIds = ensemblGeneIds;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
    }

//    public Collection<Association> getAssociations() {
//        return associations;
//    }
//
//    public void setAssociations(Collection<Association> associations) {
//        this.associations = associations;
//    }
//
//    public Collection<SingleNucleotidePolymorphism> getSnps() {
//        return snps;
//    }
//
//    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
//        this.snps = snps;
//    }
}
