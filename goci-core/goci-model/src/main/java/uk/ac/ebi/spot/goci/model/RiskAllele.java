package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 26/01/15.
 */
@Entity
public class RiskAllele {

    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private String riskAlleleName;

    @ManyToOne
    @JoinTable(name = "RISK_ALLELE_SNP",
            joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
            inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private SingleNucleotidePolymorphism snp;


    // JPA no-args constructor
    public RiskAllele() {
    }

    public RiskAllele(String riskAlleleName, SingleNucleotidePolymorphism snp) {
        this.riskAlleleName = riskAlleleName;
        this.snp = snp;
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

    @Override
    public String toString() {
        return "RiskAllele{" +
                "id=" + id +
                ", riskAlleleName='" + riskAlleleName + '\'' +
                ", snp=" + snp +
                '}';
    }
}
