package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created by catherineleroy on 08/07/2015.
 */
@Entity
public class ArrayInfo {

    @Id
    @GeneratedValue
    private Long id;


    private Long snpCount;
    private Boolean imputed;
    private String arrayName;
    private String snpCountQualifier;
    private String platform;
    private Boolean pooled;
    private Long haplotypeSnpCount;
    private int snpPerHaplotypeCount;


    public ArrayInfo(){};

    public ArrayInfo(Long snpCount, Boolean imputed, String arrayName, String snpCountQualifier, String platform, Long studyId, Boolean pooled, Boolean isCnv, Long haplotypeSnpCount, int snpPerHaplotypeCount) {
        this.snpCount = snpCount;
        this.imputed = imputed;
        this.arrayName = arrayName;
        this.snpCountQualifier = snpCountQualifier;
        this.platform = platform;
        this.pooled = pooled;
        this.haplotypeSnpCount = haplotypeSnpCount;
        this.snpPerHaplotypeCount = snpPerHaplotypeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(Long snpCount) {
        this.snpCount = snpCount;
    }

    public Boolean getImputed() {
        return imputed;
    }

    public void setImputed(Boolean imputed) {
        this.imputed = imputed;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public String getSnpCountQualifier() {
        return snpCountQualifier;
    }

    public void setSnpCountQualifier(String snpCountQualifier) {
        this.snpCountQualifier = snpCountQualifier;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Boolean getPooled() {
        return pooled;
    }

    public void setPooled(Boolean pooled) {
        this.pooled = pooled;
    }

    public Long getHaplotypeSnpCount() {
        return haplotypeSnpCount;
    }

    public void setHaplotypeSnpCount(Long haplotypeSnpCount) {
        this.haplotypeSnpCount = haplotypeSnpCount;
    }

    public int getSnpPerHaplotypeCount() {
        return snpPerHaplotypeCount;
    }

    public void setSnpPerHaplotypeCount(int snpPerHaplotypeCount) {
        this.snpPerHaplotypeCount = snpPerHaplotypeCount;
    }



    @Override public String toString() {
        return "ArrayInfo{" +
                ", snpCount='" + snpCount + '\'' +
                ", imputed='" + imputed + '\'' +
                ", arrayName='" + arrayName + '\'' +
                ", snpCountQualifier='" + snpCountQualifier + '\'' +
                ", platform='" + platform + '\'' +
                ", pooled='" + pooled + '\'' +
                ", haplotypeSnpCount='" + haplotypeSnpCount + '\'' +
                ", snpPerHaplotypeCount='" + snpPerHaplotypeCount + '\'' +
                '}';


    }

}
