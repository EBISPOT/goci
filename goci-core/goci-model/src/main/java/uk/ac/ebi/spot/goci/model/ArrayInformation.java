package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
* Created by dwelter on 08/03/16.
*/

@Entity
public class ArrayInformation {

    @Id
    @GeneratedValue
    private Long id;

//    private Collection<Platform> platform;

    private Integer snpCount;

    private String qualifier;

    private boolean imputed;

    private boolean pooled;

    private String comment;


    @OneToOne(mappedBy = "arrayInformation")
    private Study study;

    // JPA no-args constructor
    public ArrayInformation(){

    }

    public ArrayInformation(//Collection<Platform> platform,
                            Integer snpCount,
                            String qualifier,
                            boolean imputed,
                            boolean pooled,
                            String comment){
//        this.platform = platform;
        this.snpCount = snpCount;
        this.qualifier = qualifier;
        this.imputed = imputed;
        this.pooled = pooled;
        this.comment = comment;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Collection<Platform> getPlatform() {
//        return platform;
//    }
//
//    public void setPlatform(Collection<Platform> platform) {
//        this.platform = platform;
//    }

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

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
