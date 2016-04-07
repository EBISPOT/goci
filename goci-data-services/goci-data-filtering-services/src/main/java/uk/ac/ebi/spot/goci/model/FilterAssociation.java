package uk.ac.ebi.spot.goci.model;

/**
 * Created by dwelter on 05/04/16.
 */
public class FilterAssociation {

    private Integer rowNumber;

    private String strongestAllele = null;

    private String snp = null;

    private Integer pvalueMantissa = null;

    private Integer pvalueExponent = null;

    private String chromosomeName;

    private String chromosomePosition;

    private Boolean isTopAssociation = false;

    public FilterAssociation(Integer rowNumber,
                             String strongestAllele,
                             Integer pvalueMantissa,
                             Integer pvalueExponent,
                             String chromosomeName,
                             String chromosomePosition){
        this.rowNumber = rowNumber;
        this.strongestAllele = strongestAllele;
        this.pvalueMantissa = pvalueMantissa;
        this.pvalueExponent = pvalueExponent;
        this.chromosomeName = chromosomeName;
        this.chromosomePosition = chromosomePosition;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

   public String getStrongestAllele() {
        return strongestAllele;
    }

    public void setStrongestAllele(String strongestAllele) {
        this.strongestAllele = strongestAllele;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
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


    public String getChromosomeName() {
        return chromosomeName;
    }

    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    public String getChromosomePosition() {
        return chromosomePosition;
    }

    public void setChromosomePosition(String chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public Boolean getIsTopAssociation() {
        return isTopAssociation;
    }

    public void setIsTopAssociation(Boolean isTopAssociation) {
        this.isTopAssociation = isTopAssociation;
    }
}
