package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 *
 * @author emma
 *         <p/>
 *         Locus object holds links to associated risk alleles and author reported genes
 */
@Entity
public class Locus {
    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    @ManyToMany
    @JoinTable(name = "LOCUS_RISK_ALLELE",
            joinColumns = @JoinColumn(name = "LOCUS_ID"),
            inverseJoinColumns = @JoinColumn(name = "RISK_ALLELE_ID"))
    private Collection<RiskAllele> strongestRiskAlleles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "AUTHOR_REPORTED_GENE",
            joinColumns = @JoinColumn(name = "LOCUS_ID"),
            inverseJoinColumns = @JoinColumn(name = "REPORTED_GENE_ID"))
    private Collection<Gene> authorReportedGenes = new ArrayList<>();

    // JPA no-args constructor
    public Locus() {
    }

    public Locus(Collection<RiskAllele> strongestRiskAlleles, Collection<Gene> authorReportedGenes) {
        this.strongestRiskAlleles = strongestRiskAlleles;
        this.authorReportedGenes = authorReportedGenes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<RiskAllele> getStrongestRiskAlleles() {
        return strongestRiskAlleles;
    }

    public void setStrongestRiskAlleles(Collection<RiskAllele> strongestRiskAlleles) {
        this.strongestRiskAlleles = strongestRiskAlleles;
    }

    public Collection<Gene> getAuthorReportedGenes() {
        return authorReportedGenes;
    }

    public void setAuthorReportedGenes(Collection<Gene> authorReportedGenes) {
        this.authorReportedGenes = authorReportedGenes;
    }

    @Override
    public String toString() {
        return "Locus{" +
                "id=" + id +
                ", strongestRiskAlleles=" + strongestRiskAlleles +
                ", authorReportedGenes=" + authorReportedGenes +
                '}';
    }
}
