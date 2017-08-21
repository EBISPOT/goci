package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

    // Values required for SNP Interaction associations
    private String riskFrequency;

    private Boolean genomeWide = false;

    private Boolean limitedList = false;

    @ManyToOne
    @JoinTable(name = "RISK_ALLELE_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    @JsonManagedReference
    private SingleNucleotidePolymorphism snp;

    @ManyToMany
    @JoinTable(name = "RISK_ALLELE_PROXY_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private Collection<SingleNucleotidePolymorphism> proxySnps;


    @ManyToMany(mappedBy = "strongestRiskAlleles")
    @JsonBackReference
    private Collection<Locus> loci;


    // JPA no-args constructor
    public RiskAllele() {
    }

    public RiskAllele(String riskAlleleName,
                      String riskFrequency,
                      Boolean genomeWide,
                      Boolean limitedList,
                      SingleNucleotidePolymorphism snp,
                      Collection<SingleNucleotidePolymorphism> proxySnps,
                      Collection<Locus> loci) {
        this.riskAlleleName = riskAlleleName;
        this.riskFrequency = riskFrequency;
        this.genomeWide = genomeWide;
        this.limitedList = limitedList;
        this.snp = snp;
        this.proxySnps = proxySnps;
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


    public Collection<SingleNucleotidePolymorphism> getProxySnps() {
        return proxySnps;
    }

    public void setProxySnps(Collection<SingleNucleotidePolymorphism> proxySnps) {
        this.proxySnps = proxySnps;
    }

    public Collection<Locus> getLoci() {
        return loci;
    }

    public void setLoci(Collection<Locus> loci) {
        this.loci = loci;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public void setGenomeWide(Boolean genomeWide) {
        this.genomeWide = genomeWide;
    }

    public void setLimitedList(Boolean limitedList) {
        this.limitedList = limitedList;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public Boolean getGenomeWide() {
        return genomeWide;
    }

    public Boolean getLimitedList() {
        return limitedList;
    }

    @Override public String toString() {
        return "RiskAllele{" +
                "id=" + id +
                ", riskAlleleName='" + riskAlleleName + '\'' +
                ", riskFrequency='" + riskFrequency + '\'' +
                ", genomeWide=" + genomeWide +
                ", limitedList=" + limitedList +
                ", snp=" + snp +
                ", proxySnps=" + proxySnps +
                ", loci=" + loci +
                '}';
    }
}
