package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 */
@Entity
public class RiskAllele {

    @Id
    @GeneratedValue
    private Long id;

    private String riskAlleleName;

    @ManyToOne
    @JoinTable(name = "RISK_ALLELE_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private SingleNucleotidePolymorphism snp;

    @ManyToOne
    @JoinTable(name = "RISK_ALLELE_PROXY_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private SingleNucleotidePolymorphism proxySnp;


    @ManyToMany(mappedBy = "strongestRiskAlleles")
    private Collection<Locus> loci;


    // JPA no-args constructor
    public RiskAllele() {
    }

    public RiskAllele(String riskAlleleName,
                      SingleNucleotidePolymorphism snp,
                      SingleNucleotidePolymorphism proxySnp, Collection<Locus> loci) {
        this.riskAlleleName = riskAlleleName;
        this.snp = snp;
        this.proxySnp = proxySnp;
        this.loci = loci;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiskAlleleName() {
        return riskAlleleName;
    }

    public void setRiskAlleleName(String riskAlleleName) {
        this.riskAlleleName = riskAlleleName;
    }

    public SingleNucleotidePolymorphism getSnp() {
        return snp;
    }

    public void setSnp(SingleNucleotidePolymorphism snp) {
        this.snp = snp;
    }

    public SingleNucleotidePolymorphism getProxySnp() {
        return proxySnp;
    }

    public void setProxySnp(SingleNucleotidePolymorphism proxySnp) {
        this.proxySnp = proxySnp;
    }

    public Collection<Locus> getLoci() {
        return loci;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    @Override public String toString() {
        return "RiskAllele{" +
                "id=" + id +
                ", riskAlleleName='" + riskAlleleName + '\'' +
                ", snp=" + snp +
                ", proxySnp=" + proxySnp +
                ", loci=" + loci +
                '}';
    }
}
