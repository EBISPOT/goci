package uk.ac.ebi.spot.goci.ui.model;

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
@Table(name = "GWASSTUDIES")
public class Study {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "STUDYDATE")
    private Date studyDate;

    @Column(name = "PUBLICATION")
    private String publication;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "INITSAMPLESIZE")
    private String initialSampleSize;

    @Column(name = "REPLICSAMPLESIZE")
    private String replicateSampleSize;

    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "PMID")
    private String pubmedID;

    @Column(name = "CNV")
    private String cnv;

    @Column(name = "GXE")
    private String gxe;

    @Column(name = "GXG")
    private String gxg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISEASEID")
    private DiseaseTrait diseaseTrait;

    // Associated EFO trait
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "GWASEFOSTUDYXREF",
            joinColumns = {@JoinColumn(name = "STUDYID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "TRAITID", referencedColumnName = "ID")}
    )
    private Collection<EFOTrait> efoTraits;

    // Associated Housekeeping attribute
    @JoinColumn(name = "HOUSEKEEPINGID")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Housekeeping housekeeping;

    // JPA no-args constructor
    public Study() {
    }

    public Study(String author, Date studyDate, String publication, String title, String initialSampleSize, String replicateSampleSize, String platform, String pubmedID, String cnv, String gxe, String gxg, DiseaseTrait diseaseTrait, Collection<EFOTrait> efoTraits, Housekeeping housekeeping) {
        this.author = author;
        this.studyDate = studyDate;
        this.publication = publication;
        this.title = title;
        this.initialSampleSize = initialSampleSize;
        this.replicateSampleSize = replicateSampleSize;
        this.platform = platform;
        this.pubmedID = pubmedID;
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

    public String getPubmedID() {
        return pubmedID;
    }

    public void setPubmedID(String pubmedID) {
        this.pubmedID = pubmedID;
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
                ", pubmedID='" + pubmedID + '\'' +
                ", cnv='" + cnv + '\'' +
                ", gxe='" + gxe + '\'' +
                ", gxg='" + gxg + '\'' +
                ", diseaseTrait=" + diseaseTrait +
                ", efoTraits=" + efoTraits +
                ", housekeeping=" + housekeeping +
                '}';
    }
}
