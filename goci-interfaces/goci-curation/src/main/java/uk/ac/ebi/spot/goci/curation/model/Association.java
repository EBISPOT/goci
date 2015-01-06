package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Model object representing an association
 */


@Entity
@Table(name = "GWASASSOCIATIONS")
public class Association {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "STUDYID")
    private String studyID;

    @Column(name = "GENE")
    private String authorReportedGene;

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

    // Associated SNP(s)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "GWASSNPXREF",
            joinColumns = {@JoinColumn(name = "GWASSTUDIESSNPID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "SNPID", referencedColumnName = "ID")}
    )
    private Collection<SingleNucleotidePolymorphism> snps;


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

    public Association(String studyID, String authorReportedGene, String strongestAllele, String riskFrequency, String allele, float pvalueFloat, String pvalueText, Double ORPerCopyNum, String ORType, String snpType, Integer pvalueMantissa, Integer pvalueExponent, Double ORPerCopyRecip, Double ORPerCopyStdError, String ORPerCopyRange, String ORPerCopyUnitDescr, Collection<SingleNucleotidePolymorphism> snps, Collection<EFOTrait> efoTraits) {
        this.studyID = studyID;
        this.authorReportedGene = authorReportedGene;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudyID() {
        return studyID;
    }

    public void setStudyID(String studyID) {
        this.studyID = studyID;
    }

    public String getAuthorReportedGene() {
        return authorReportedGene;
    }

    public void setAuthorReportedGene(String authorReportedGene) {
        this.authorReportedGene = authorReportedGene;
    }

    public String getStrongestAllele() {
        return strongestAllele;
    }

    public void setStrongestAllele(String strongestAllele) {
        this.strongestAllele = strongestAllele;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public void setRiskFrequency(String riskFrequency) {
        this.riskFrequency = riskFrequency;
    }

    public String getAllele() {
        return allele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }

    public float getPvalueFloat() {
        return pvalueFloat;
    }

    public void setPvalueFloat(float pvalueFloat) {
        this.pvalueFloat = pvalueFloat;
    }

    public String getPvalueText() {
        return pvalueText;
    }

    public void setPvalueText(String pvalueText) {
        this.pvalueText = pvalueText;
    }

    public Double getORPerCopyNum() {
        return ORPerCopyNum;
    }

    public void setORPerCopyNum(Double ORPerCopyNum) {
        this.ORPerCopyNum = ORPerCopyNum;
    }

    public String getORType() {
        return ORType;
    }

    public void setORType(String ORType) {
        this.ORType = ORType;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public Integer getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(Integer pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public Double getORPerCopyRecip() {
        return ORPerCopyRecip;
    }

    public void setORPerCopyRecip(Double ORPerCopyRecip) {
        this.ORPerCopyRecip = ORPerCopyRecip;
    }

    public Double getORPerCopyStdError() {
        return ORPerCopyStdError;
    }

    public void setORPerCopyStdError(Double ORPerCopyStdError) {
        this.ORPerCopyStdError = ORPerCopyStdError;
    }

    public String getORPerCopyRange() {
        return ORPerCopyRange;
    }

    public void setORPerCopyRange(String ORPerCopyRange) {
        this.ORPerCopyRange = ORPerCopyRange;
    }

    public String getORPerCopyUnitDescr() {
        return ORPerCopyUnitDescr;
    }

    public void setORPerCopyUnitDescr(String ORPerCopyUnitDescr) {
        this.ORPerCopyUnitDescr = ORPerCopyUnitDescr;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }

    public Collection<EFOTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EFOTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    @Override
    public String toString() {
        return "Association{" +
                "id=" + id +
                ", studyID='" + studyID + '\'' +
                ", authorReportedGene='" + authorReportedGene + '\'' +
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
