package uk.ac.ebi.spot.goci.model;

import java.util.List;

/**
 * Created by dwelter on 05/04/16.
 */
public class FilterAssociation {

    private String strongestAllele = null;

    private Double pvalueMantissa = null;

    private Integer pvalueExponent = null;

    private double pvalue;

    private String chromosomeName;

    private Integer chromosomePosition;

    private Boolean isTopAssociation = false;

    private Boolean isAmbigious = false;

    private List<String> otherInformation;

    private Boolean precisionConcern = false;

    private String ldBlock = null;

    public FilterAssociation(String strongestAllele,
                             Double pvalueMantissa,
                             Integer pvalueExponent,
                             String chromosomeName,
                             String chromosomePosition){
        this.strongestAllele = strongestAllele;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = Integer.parseInt(chromosomePosition);
    }


   public String getStrongestAllele() {
        return strongestAllele;
    }

    public void setStrongestAllele(String strongestAllele) {
        this.strongestAllele = strongestAllele;
    }

    public Double getPvalueMantissa() {
        return pvalueMantissa;
    }

    public void setPvalueMantissa(Double pvalueMantissa) {
        this.pvalueMantissa = pvalueMantissa;
    }

    public Integer getPvalueExponent() {
        return pvalueExponent;
    }

    public void setPvalueExponent(Integer pvalueExponent) {
        this.pvalueExponent = pvalueExponent;
    }


    public String getChromosomeName() {
        return chromosomeName;
    }

    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    public Integer getChromosomePosition() {
        return chromosomePosition;
    }

    public void setChromosomePosition(String chromosomePosition) {
        this.chromosomePosition = Integer.parseInt(chromosomePosition);
    }

    public void setChromosomePosition(Integer chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public Boolean getIsTopAssociation() {
        return isTopAssociation;
    }

    public void setIsTopAssociation(Boolean isTopAssociation) {
        this.isTopAssociation = isTopAssociation;
    }

    public double getPvalue() {
        return pvalue;
    }

    public void setPvalue(double pvalue) {
        this.pvalue = pvalue;
    }

    public List<String> getOtherInformation() {
        return otherInformation;
    }

    public void setOtherInformation(List<String> otherInformation) {
        this.otherInformation = otherInformation;
    }

    public Boolean getPrecisionConcern() {
        return precisionConcern;
    }

    public void setPrecisionConcern(Boolean precisionConcern) {
        this.precisionConcern = precisionConcern;
    }

    public String getLdBlock() {
        return ldBlock;
    }

    public void setLdBlock(String ldBlock) {
        this.ldBlock = ldBlock;
    }

    public Boolean getIsAmbigious() {
        return isAmbigious;
    }

    public void setIsAmbigious(Boolean ambigious) {
        isAmbigious = ambigious;
    }
}
