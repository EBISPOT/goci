package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         A model object representing a gene from Ensembl database
 */
@Entity
public class EnsemblGene {

    @Id
    @GeneratedValue
    private Long id;

    private String ensemblGeneId;

    @ManyToMany
    @JoinTable(name = "GENE_ENSEMBL_GENE",
               joinColumns = @JoinColumn(name = "ENSEMBL_GENE_ID"),
               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
    private Collection<Gene> gene;

    // JPA no-args constructor
    public EnsemblGene() {
    }

    public EnsemblGene(String ensemblGeneId, Collection<Gene> gene) {
        this.ensemblGeneId = ensemblGeneId;
        this.gene = gene;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public Collection<Gene> getGene() {
        return gene;
    }

    public void setGene(Collection<Gene> gene) {
        this.gene = gene;
    }
}
