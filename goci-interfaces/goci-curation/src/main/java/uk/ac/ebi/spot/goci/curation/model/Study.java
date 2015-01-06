package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;



/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 *         <p>
 *         Model of a GWAS study
 */

@Entity
public class Study {
    @Id
    @GeneratedValue
    private Long id;

    private String author;

    private Date studyDate;

    private String publication;

    private String title;

    private String initialSampleSize;

    private String replicateSampleSize;

    private String platform;

    private String pubmedId;

    private String cnv;

    private String gxe;

    private String gxg;

    @OneToOne
    private DiseaseTrait diseaseTrait;

    @ManyToMany
    private Collection<EFOTrait> efoTraits;

    @OneToOne
    private Housekeeping housekeeping;

    // JPA no-args constructor
    public Study() {
    }

    public Study(String author, Date studyDate, String publication, String title, String initialSampleSize, String replicateSampleSize, String platform, String pubmedId, String cnv, String gxe, String gxg, DiseaseTrait diseaseTrait, Collection<EFOTrait> efoTraits, Housekeeping housekeeping) {
        this.author = author;
        this.studyDate = studyDate;
        this.publication = publication;
        this.title = title;
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.platform = platform;
        this.pubmedId = pubmedId;
        this.cnv = cnv;
        this.gxe = gxe;
        this.gxg = gxg;
        this.diseaseTrait = diseaseTrait;
        this.efoTraits = efoTraits;
        this.housekeeping = housekeeping;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitialSampleSize() {
        return initialSampleSize;
    }

    public void setInitialSampleSize(String initialSampleSize) {
        this.initialSampleSize = initialSampleSize;
    }

    public String getReplicateSampleSize() {
        return replicateSampleSize;
    }

    public void setReplicateSampleSize(String replicateSampleSize) {
        this.replicateSampleSize = replicateSampleSize;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getCnv() {
        return cnv;
    }

    public void setCnv(String cnv) {
        this.cnv = cnv;
    }

    public String getGxe() {
        return gxe;
    }

    public void setGxe(String gxe) {
        this.gxe = gxe;
    }

    public String getGxg() {
        return gxg;
    }

    public void setGxg(String gxg) {
        this.gxg = gxg;
    }

    public DiseaseTrait getDiseaseTrait() {
        return diseaseTrait;
    }

    public void setDiseaseTrait(DiseaseTrait diseaseTrait) {
        this.diseaseTrait = diseaseTrait;
    }

    public Collection<EFOTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EFOTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Housekeeping getHousekeeping() {
        return housekeeping;
    }

    public void setHousekeeping(Housekeeping housekeeping) {
        this.housekeeping = housekeeping;
    }

    @Override
    public String toString() {
        return "Study{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", studyDate=" + studyDate +
                ", publication='" + publication + '\'' +
                ", title='" + title + '\'' +
                ", initialSampleSize='" + initialSampleSize + '\'' +
                ", replicateSampleSize='" + replicateSampleSize + '\'' +
                ", platform='" + platform + '\'' +
                ", pubmedId='" + pubmedId + '\'' +
                ", cnv='" + cnv + '\'' +
                ", gxe='" + gxe + '\'' +
                ", gxg='" + gxg + '\'' +
                ", diseaseTrait=" + diseaseTrait +
                ", efoTraits=" + efoTraits +
                ", housekeeping=" + housekeeping +
                '}';
    }
}
