package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    private String authorReportedGene;

    private String strongestAllele;

    private String riskFrequency;

    private String allele;

    private Float pvalueFloat;

    private String pvalueText;

    private Float orPerCopyNum;

    private String orType;

    private String snpType;

    private String multiSnpHaplotype;

    private String snpInteraction;

    private Integer pvalueMantissa;

    private Integer pvalueExponent;

    private Float orPerCopyRecip;

    private Float orPerCopyStdError;

    private String orPerCopyRange;

    private String orPerCopyUnitDescr;

    @OneToOne
    private Study study;

    @ManyToMany
    @JoinTable(name = "ASSOCIATION_SNP",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private Collection<SingleNucleotidePolymorphism> snps;

    @OneToMany
    @JoinTable(name = "ASSOCIATION_REPORTED_GENE",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "REPORTED_GENE_ID"))
    private Collection<Gene> reportedGenes;

    @OneToMany
    @JoinTable(name = "ASSOCIATION_EFO_TRAIT",
               joinColumns = @JoinColumn(name = "ASSOCIATION_ID"),
               inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits;

    // JPA no-args constructor
    public Association() {
    }

    public Association(String strongestAllele,
                       String riskFrequency,
                       String allele,
                       String authorReportedGene,
                       float pvalueFloat,
                       String pvalueText,
                       float orPerCopyNum,
                       String ORType,
                       String snpType,
                       String multiSnpHaplotype,
                       String snpInteraction,
                       int pvalueMantissa,
                       int pvalueExponent,
                       float orPerCopyRecip,
                       float orPerCopyStdError,
                       String orPerCopyRange,
                       String orPerCopyUnitDescr,
                       Study study,
                       Collection<SingleNucleotidePolymorphism> snps,
                       Collection<Gene> reportedGenes,
                       Collection<EfoTrait> efoTraits) {
        this.authorReportedGene = authorReportedGene;
        this.strongestAllele = strongestAllele;
        this.riskFrequency = riskFrequency;
        this.allele = allele;
        this.pvalueFloat = pvalueFloat;
        this.pvalueText = pvalueText;
        this.orPerCopyNum = orPerCopyNum;
        this.orType = ORType;
        this.snpType = snpType;
        this.multiSnpHaplotype = multiSnpHaplotype;
        this.snpInteraction = snpInteraction;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.orPerCopyRecip = orPerCopyRecip;
        this.orPerCopyStdError = orPerCopyStdError;
        this.orPerCopyRange = orPerCopyRange;
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
        this.study = study;
        this.snps = snps;
        this.reportedGenes = reportedGenes;
        this.efoTraits = efoTraits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public void setOrPerCopyNum(float orPerCopyNum) {
        this.orPerCopyNum = orPerCopyNum;
    }

    public String getOrType() {
        return orType;
    }

    public void setOrType(String orType) {
        this.orType = orType;
    }

    public String getSnpType() {
        return snpType;
    }

    public void setSnpType(String snpType) {
        this.snpType = snpType;
    }

    public String getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public void setMultiSnpHaplotype(String multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public String getSnpInteraction() {
        return snpInteraction;
    }

    public void setSnpInteraction(String snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public int getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(int pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public int getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(int pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }

    public float getOrPerCopyRecip() {
        return orPerCopyRecip;
    }

    public void setOrPerCopyRecip(float orPerCopyRecip) {
        this.orPerCopyRecip = orPerCopyRecip;
    }

    public float getOrPerCopyStdError() {
        return orPerCopyStdError;
    }

    public void setOrPerCopyStdError(float orPerCopyStdError) {
        this.orPerCopyStdError = orPerCopyStdError;
    }

    public String getOrPerCopyRange() {
        return orPerCopyRange;
    }

    public void setOrPerCopyRange(String orPerCopyRange) {
        this.orPerCopyRange = orPerCopyRange;
    }

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public void setOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescr = orPerCopyUnitDescr;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Collection<SingleNucleotidePolymorphism> getSnps() {
        return snps;
    }

    public void setSnps(Collection<SingleNucleotidePolymorphism> snps) {
        this.snps = snps;
    }

    public Collection<Gene> getReportedGenes() {
        return reportedGenes;
    }

    public void setReportedGenes(Collection<Gene> reportedGenes) {
        this.reportedGenes = reportedGenes;
    }

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
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
                ", multiSnpHaplotype='" + multiSnpHaplotype + '\'' +
                ", snpInteraction='" + snpInteraction + '\'' +
                ", pvalueMantissa=" + pvalueMantissa +
                ", pvalueExponent=" + pvalueExponent +
                ", orPerCopyRecip=" + orPerCopyRecip +
                ", orPerCopyStdError=" + orPerCopyStdError +
                ", orPerCopyRange='" + orPerCopyRange + '\'' +
                ", orPerCopyUnitDescr='" + orPerCopyUnitDescr + '\'' +
                ", snps=" + snps +
                ", efoTraits=" + efoTraits +
                '}';
    }
}
