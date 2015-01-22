package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;


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

    @ManyToOne
    @JoinTable(name = "STUDY_DISEASE_TRAIT",
            joinColumns = @JoinColumn(name = "STUDY_ID"),
            inverseJoinColumns = @JoinColumn(name = "DISEASE_TRAIT_ID"))
    private DiseaseTrait diseaseTrait;

    @ManyToMany
    @JoinTable(name = "STUDY_EFO_TRAIT",
            joinColumns = @JoinColumn(name = "STUDY_ID"),
            inverseJoinColumns = @JoinColumn(name = "EFO_TRAIT_ID"))
    private Collection<EfoTrait> efoTraits;

    @ManyToMany
    @JoinTable(name = "STUDY_SNP",
            joinColumns = @JoinColumn(name = "STUDY_ID"),
            inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms;

    @OneToOne
    private Housekeeping housekeeping;

    // JPA no-args constructor
    public Study() {
    }

    public Study(String author,
                 Date studyDate,
                 String publication,
                 String title,
                 String initialSampleSize,
                 String replicateSampleSize,
                 String platform,
                 String pubmedId,
                 String cnv,
                 String gxe,
                 String gxg,
                 DiseaseTrait diseaseTrait,
                 Collection<EfoTrait> efoTraits,
                 Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms,
                 Housekeeping housekeeping) {
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
        this.singleNucleotidePolymorphisms = singleNucleotidePolymorphisms;
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

    public Collection<EfoTrait> getEfoTraits() {
        return efoTraits;
    }

    public void setEfoTraits(Collection<EfoTrait> efoTraits) {
        this.efoTraits = efoTraits;
    }

    public Collection<SingleNucleotidePolymorphism> getSingleNucleotidePolymorphisms() {
        return singleNucleotidePolymorphisms;
    }

    public void setSingleNucleotidePolymorphisms(Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms) {
        this.singleNucleotidePolymorphisms = singleNucleotidePolymorphisms;
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
                ", efoTraits=" + efoTraits +
                ", housekeeping=" + housekeeping +
                '}';
    }
}
