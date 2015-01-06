package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an association
 */


@Entity
public class Association {
    @Id
    @GeneratedValue
    @NotNull
    private Long id;

    private String strongestAllele;

    private String riskFrequency;

    private String allele;

    private float pvalueFloat;

    private String pvalueText;

    private Double orPerCopyNum;

    private String orType;

    private String snpType;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Double orPerCopyRecip;

    private Double orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    @OneToOne
    private Study study;

    @OneToOne
    private SingleNucleotidePolymorphism snp;

    @OneToMany
    @JoinTable(name = "ASSOCIATION_REPORTED_GENE",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "REPORTED_GENE_ID"))
    private Collection<Gene> reportedGenes;

    @OneToMany
    @JoinTable(name = "ASSOCIATION_EFO_TRAITS",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EFOTrait> efoTraits;

    // JPA no-args constructor
    public Association() {
    }

    public Association(String strongestAllele,
                       String riskFrequency,
                       String allele,
                       float pvalueFloat,
                       String pvalueText,
                       Double ORPerCopyNum,
                       String ORType,
                       String snpType,
                       Integer pvalueMantissa,
                       Integer pvalueExponent,
                       Double ORPerCopyRecip,
                       Double ORPerCopyStdError,
                       String ORPerCopyRange,
                       String ORPerCopyUnitDescr,
                       Study study,
                       SingleNucleotidePolymorphism snp,
                       Collection<Gene> reportedGenes,
                       Collection<EFOTrait> efoTraits) {
        this.strongestAllele = strongestAllele;
        this.riskFrequency = riskFrequency;
        this.allele = allele;
        this.pvalueFloat = pvalueFloat;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = ORPerCopyNum;
        this.orType = ORType;
        this.snpType = snpType;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = ORPerCopyRecip;
        this.orPerCopyStdError = ORPerCopyStdError;
        this.orPerCopyRange = ORPerCopyRange;
        this.orPerCopyUnitDescr = ORPerCopyUnitDescr;
        this.study = study;
        this.snp = snp;
        this.reportedGenes = reportedGenes;
        this.efoTraits = efoTraits;
    }

    public Long getId() {
        return id;
    }

    public String getStrongestAllele() {
        return strongestAllele;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public String getAllele() {
        return allele;
    }

    public float getPvalueFloat() {
        return pvalueFloat;
    }

    public String getPvalueText() {
        return pvalueText;
    }

    public Double getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public String getOrType() {
        return orType;
    }

    public String getSnpType() {
        return snpType;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public Double getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public Double getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public String getOrPerCopyRange() {
        return orPerCopyRange;
    }

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public Study getStudy() {
        return study;
    }

    public SingleNucleotidePolymorphism getSnp() {
        return snp;
    }

    public Collection<Gene> getReportedGenes() {
        return reportedGenes;
    }

    public Collection<EFOTrait> getEfoTraits() {
        return efoTraits;
    }

    @Override
    public String toString() {
        return "Association{" +
                "id=" + id +
                ", study='" + study + '\'' +
                ", strongestAllele='" + strongestAllele + '\'' +
                ", riskFrequency='" + riskFrequency + '\'' +
                ", allele='" + allele + '\'' +
                ", pvalueFloat=" + pvalueFloat +
                ", pvalueText='" + pvalueText + '\'' +
                ", orPerCopyNum=" + orPerCopyNum +
                ", orType='" + orType + '\'' +
                ", snpType='" + snpType + '\'' +
                ", pvalueMantissa=" + pvalueMantissa +
                ", pvalueExponent=" + pvalueExponent +
                ", orPerCopyRecip=" + orPerCopyRecip +
                ", orPerCopyStdError=" + orPerCopyStdError +
                ", orPerCopyRange='" + orPerCopyRange + '\'' +
                ", orPerCopyUnitDescr='" + orPerCopyUnitDescr + '\'' +
                ", snp=" + snp +
                ", efoTraits=" + efoTraits +
                '}';
    }
}
