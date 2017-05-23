package uk.ac.ebi.spot.goci.service.model;

import java.util.Collection;
import java.util.List;

/**
 * Created by catherineleroy on 17/02/2016.
 */
public class SnpInfo {

    //rs10011926       intron_variant          ENSG00000170522         ELOVL6          0
//    rs10012307       nearest_gene_five_prime_end     ENSG00000189184         PCDH18          -927065
//    rs10012750       nearest_gene_five_prime_end     ENSG00000179059         ZFP42   -301812
//    rs10777332       nearest_gene_five_prime_end     ENSG00000257242,ENSG00000279037,ENSG00000257242,ENSG00000280112,ENSG00000280112,ENSG00000279037         C12orf79,C12orf79,C12orf79,C12orf79,C12orf79,C12o

    private String isInEnsmbl;
    private String rsId;
    private String soTerm;
    private List<String> ensemblId;
    private List<String> ensemblName;
    private String distance;
    private boolean isTargetArray;
    private String pubMedId;
    private String efoTrait;
    private String pval;
    private int sampleSize;
    private long snpCount;

    public SnpInfo(){
        
    }

    public SnpInfo(String rsId,String isInEnsmbl, String soTerm, List<String> ensemblId, List<String> ensemblName, String distance, boolean isTargetArray) {
        this.rsId = rsId;
        this.soTerm = soTerm;
        this.ensemblId = ensemblId;
        this.ensemblName = ensemblName;
        this.distance = distance;
        this.isInEnsmbl = isInEnsmbl;
        this.isTargetArray = isTargetArray;
    }

    public String getRsId() {
        return rsId;
    }

    public void setRsId(String rsId) {
        this.rsId = rsId;
    }

    public String getSoTerm() {
        return soTerm;
    }

    public void setSoTerm(String soTerm) {
        this.soTerm = soTerm;
    }

    public List<String> getEnsemblIds() {
        return ensemblId;
    }

    public void setEnsemblIds(List<String> ensemblId) {
        this.ensemblId = ensemblId;
    }

    public List<String> getEnsemblName() {
        return ensemblName;
    }

    public void setEnsemblName(List<String> ensemblName) {
        this.ensemblName = ensemblName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIsInEnsmbl() {
        return isInEnsmbl;
    }

    public void setIsInEnsmbl(String isInEnsmbl) {
        this.isInEnsmbl = isInEnsmbl;
    }

    public boolean isTargetArray() {
        return isTargetArray;
    }

    public void setTargetArray(boolean targetArray) {
        isTargetArray = targetArray;
    }

    public String getPubMedId() {
        return pubMedId;
    }

    public void setPubMedId(String pubMedId) {
        this.pubMedId = pubMedId;
    }

    public String getEfoTrait() {
        return efoTrait;
    }

    public void setEfoTrait(String efoTrait) {
        this.efoTrait = efoTrait;
    }

    public String getPval() {
        return pval;
    }

    public void setPval(String pval) {
        this.pval = pval;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public long getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(long snpCount) {
        this.snpCount = snpCount;
    }
}
