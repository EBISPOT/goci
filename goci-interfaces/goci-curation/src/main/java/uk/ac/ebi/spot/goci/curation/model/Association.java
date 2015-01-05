package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p/>
 *         Model object representing an association
 */


@Entity
@Table(name = "GWASASSOCIATIONS")
public class Association {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Column(name = "STUDYID")
    private String studyID;

    @Column(name = "STRONGESTALLELE")
    private String strongestAllele;

    @Column(name = "RISKFREQUENCY")
    private String riskFrequency;

    @Column(name = "ALLELE")
    private String allele;

    @Column(name = "PVALUEFLOAT")
    private float pvalueFloat;

    @Column(name = "PVALUETXT")
    private String pvalueText;

    @Column(name = "ORPERCOPYNUM")
    private Double ORPerCopyNum;

    @Column(name = "ORTYPE")
    private String ORType;

    @Column(name = "SNPTYPE")
    private String snpType;

    @Column(name = "PVALUE_MANTISSA")
    private Integer pvalueMantissa;

    @Column(name = "PVALUE_EXPONENT")
    private Integer pvalueExponent;

    @Column(name = "ORPERCOPYRECIP")
    private Double ORPerCopyRecip;

    @Column(name = "ORPERCOPYSTDERROR")
    private Double ORPerCopyStdError;

    @Column(name = "ORPERCOPYRANGE")
    private String ORPerCopyRange;

    @Column(name = "ORPERCOPYUNITDESCR")
    private String ORPerCopyUnitDescr;


    //TODO: DOES AN ASSOCIATION EVER HAVE MORE THAN ONE SNP
    // Associated SNPs
    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "GWASSNPXREF",
            joinColumns = {@JoinColumn(name = "GWASSTUDIESSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "SNPID", referencedColumnName = "ID")}
    )
    private SingleNucleotidePolymorphism snps;


    // Associated EFO trait
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "GWASEFOSNPXREF",
            joinColumns = {@JoinColumn(name = "GWASSTUDIESSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "TRAITID", referencedColumnName = "ID")}
    )
    private Collection<EFOTrait> efoTraits;

    // JPA no-args constructor
    public Association() {
    }

    public Association(String studyID, String strongestAllele, String riskFrequency, String allele, float pvalueFloat, String pvalueText, Double ORPerCopyNum, String ORType, String snpType, Integer pvalueMantissa, Integer pvalueExponent, Double ORPerCopyRecip, Double ORPerCopyStdError, String ORPerCopyRange, String ORPerCopyUnitDescr, SingleNucleotidePolymorphism snps, Collection<EFOTrait> efoTraits) {
        this.studyID = studyID;
        this.strongestAllele = strongestAllele;
        this.riskFrequency = riskFrequency;
        this.allele = allele;
        this.pvalueFloat = pvalueFloat;
        this.pvalueText = pvalueText;
        this.ORPerCopyNum = ORPerCopyNum;
        this.ORType = ORType;
        this.snpType = snpType;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.ORPerCopyRecip = ORPerCopyRecip;
        this.ORPerCopyStdError = ORPerCopyStdError;
        this.ORPerCopyRange = ORPerCopyRange;
        this.ORPerCopyUnitDescr = ORPerCopyUnitDescr;
        this.snps = snps;
        this.efoTraits = efoTraits;
    }

    public Long getId() {
        return id;
    }

    public String getStudyID() {
        return studyID;
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

    public Double getORPerCopyNum() {
        return ORPerCopyNum;
    }

    public String getORType() {
        return ORType;
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

    public Double getORPerCopyRecip() {
        return ORPerCopyRecip;
    }

    public Double getORPerCopyStdError() {
        return ORPerCopyStdError;
    }

    public String getORPerCopyRange() {
        return ORPerCopyRange;
    }

    public String getORPerCopyUnitDescr() {
        return ORPerCopyUnitDescr;
    }

    public SingleNucleotidePolymorphism getSnps() {
        return snps;
    }

    public Collection<EFOTrait> getEfoTraits() {
        return efoTraits;
    }

    @Override
    public String toString() {
        return "Association{" +
                "id=" + id +
                ", studyID='" + studyID + '\'' +
                ", strongestAllele='" + strongestAllele + '\'' +
                ", riskFrequency='" + riskFrequency + '\'' +
                ", allele='" + allele + '\'' +
                ", pvalueFloat=" + pvalueFloat +
                ", pvalueText='" + pvalueText + '\'' +
                ", ORPerCopyNum=" + ORPerCopyNum +
                ", ORType='" + ORType + '\'' +
                ", snpType='" + snpType + '\'' +
                ", pvalueMantissa=" + pvalueMantissa +
                ", pvalueExponent=" + pvalueExponent +
                ", ORPerCopyRecip=" + ORPerCopyRecip +
                ", ORPerCopyStdError=" + ORPerCopyStdError +
                ", ORPerCopyRange='" + ORPerCopyRange + '\'' +
                ", ORPerCopyUnitDescr='" + ORPerCopyUnitDescr + '\'' +
                ", snps=" + snps +
                ", efoTraits=" + efoTraits +
                '}';
    }
}
