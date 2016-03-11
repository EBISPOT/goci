package uk.ac.ebi.spot.goci.model;

import java.util.List;

/**
 * Created by dwelter on 08/03/16.
 */

public class LocalArrayInfo {
    private List<String> manufacturer;

    private Integer snpCount;

    private String qualifier;

    private boolean imputed;

    private boolean pooled;

    private String comment;

    private int study;

    private String platformString;

    //    TO DO: add cross-links to study & manufacturer + create Manufacturer class

    // JPA no-args constructor
    public LocalArrayInfo(){

    }

    public LocalArrayInfo(List<String> manufacturer,
                          Integer snpCount,
                          String qualifier,
                          boolean imputed,
                          boolean pooled,
                          String comment,
                          int study,
                          String platformString){
        this.manufacturer = manufacturer;
        this.snpCount = snpCount;
        this.qualifier = qualifier;
        this.imputed = imputed;
        this.pooled = pooled;
        this.comment = comment;
        this.study = study;
        this.platformString = platformString;
    }



    public List<String> getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(List<String> manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getSnps() {
        return snpCount;
    }

    public void setSnps(Integer snps) {
        this.snpCount = snps;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public boolean isImputed() {
        return imputed;
    }

    public void setImputed(boolean imputed) {
        this.imputed = imputed;
    }

    public boolean isPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStudy() {
        return study;
    }

    public void setStudy(int study) {
        this.study = study;
    }

    public String getPlatformString() {
        return platformString;
    }

    public void setPlatformString(String platformString) {
        this.platformString = platformString;
    }
}
