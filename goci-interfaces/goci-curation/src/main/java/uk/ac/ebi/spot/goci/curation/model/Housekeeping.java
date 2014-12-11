package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by emma on 03/12/14.
 *
 * @author emma
 *         <p/>
 *         Model representing housekeeping information stored about a study that is used during curation
 */
@Entity
@Table(name = "HOUSEKEEPING")
public class Housekeeping {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="STUDYID")
    private Study study;

    @Column(name = "STUDYSNPCHECKEDL1")
    private String studySnpCheckedLevelOne;

    @Column(name = "STUDYSNPCHECKEDL2")
    private String studySnpCheckedLevelTwo;

    @Column(name = "PUBLISH")
    private String publish;

    @Column(name = "PENDING")
    private String pending;

    @Temporal(TemporalType.DATE)
    @Column(name = "PUBLISHDATE")
    private Date publishDate;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "ETHNICITYCHECKEDL1")
    private String ethnicityCheckedLevelOne;

    @Column(name = "ETHNICITYCHECKEDL2")
    private String ethnicityCheckedLevelTwo;

    @Column(name = "SENDTONCBI")
    private String sendToNCBI;

    @Temporal(TemporalType.DATE)
    @Column(name = "SENDTONCBIDATE")
    private Date sendToNCBIDate;

    @Column(name = "CHECKEDNCBIERROR")
    private String checkedNCBIError;

    @Column(name = "FILENAM")
    private String fileName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURATORID")
    private Curator curator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURATORSTATUSID")
    private CurationStatus curationStatus;

    @Column(name = "ETHNICITYBACKFILLED")
    private String ethnicityBackFilled;

    @Column(name = "RECHECKSNPS")
    private String recheckSNPs;

    @Column(name = "STUDYADDEDDATE")
    private Timestamp studyAddedDate;

    @Column(name = "LASTUPDATEDATE")
    private Timestamp lastUpdateDate;

    // JPA no-args constructor
    public Housekeeping() {
    }

    public Housekeeping(Study study, String studySnpCheckedLevelOne, String studySnpCheckedLevelTwo, String publish, String pending, Date publishDate, String notes, String ethnicityCheckedLevelOne, String ethnicityCheckedLevelTwo, String sendToNCBI, Date sendToNCBIDate, String checkedNCBIError, String fileName, Curator curator, CurationStatus curationStatus, String ethnicityBackFilled, String recheckSNPs, Timestamp studyAddedDate, Timestamp lastUpdateDate) {
        this.study = study;
        this.studySnpCheckedLevelOne = studySnpCheckedLevelOne;
        this.studySnpCheckedLevelTwo = studySnpCheckedLevelTwo;
        this.publish = publish;
        this.pending = pending;
        this.publishDate = publishDate;
        this.notes = notes;
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
        this.sendToNCBI = sendToNCBI;
        this.sendToNCBIDate = sendToNCBIDate;
        this.checkedNCBIError = checkedNCBIError;
        this.fileName = fileName;
        this.curator = curator;
        this.curationStatus = curationStatus;
        this.ethnicityBackFilled = ethnicityBackFilled;
        this.recheckSNPs = recheckSNPs;
        this.studyAddedDate = studyAddedDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getId() {
        return id;
    }

    public Study getStudy() {
        return study;
    }

    public String getStudySnpCheckedLevelOne() {
        return studySnpCheckedLevelOne;
    }

    public String getStudySnpCheckedLevelTwo() {
        return studySnpCheckedLevelTwo;
    }

    public String getPublish() {
        return publish;
    }

    public String getPending() {
        return pending;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public String getNotes() {
        return notes;
    }

    public String getEthnicityCheckedLevelOne() {
        return ethnicityCheckedLevelOne;
    }

    public String getEthnicityCheckedLevelTwo() {
        return ethnicityCheckedLevelTwo;
    }

    public String getSendToNCBI() {
        return sendToNCBI;
    }

    public Date getSendToNCBIDate() {
        return sendToNCBIDate;
    }

    public String getCheckedNCBIError() {
        return checkedNCBIError;
    }

    public String getFileName() {
        return fileName;
    }

    public Curator getCurator() {
        return curator;
    }

    public CurationStatus getCurationStatus() {
        return curationStatus;
    }

    public String getEthnicityBackFilled() {
        return ethnicityBackFilled;
    }

    public String getRecheckSNPs() {
        return recheckSNPs;
    }

    public Timestamp getStudyAddedDate() {
        return studyAddedDate;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public String toString() {
        return "Housekeeping{" +
                "id=" + id +
                ", study=" + study +
                ", studySnpCheckedLevelOne='" + studySnpCheckedLevelOne + '\'' +
                ", studySnpCheckedLevelTwo='" + studySnpCheckedLevelTwo + '\'' +
                ", publish='" + publish + '\'' +
                ", pending='" + pending + '\'' +
                ", publishDate=" + publishDate +
                ", notes='" + notes + '\'' +
                ", ethnicityCheckedLevelOne='" + ethnicityCheckedLevelOne + '\'' +
                ", ethnicityCheckedLevelTwo='" + ethnicityCheckedLevelTwo + '\'' +
                ", sendToNCBI='" + sendToNCBI + '\'' +
                ", sendToNCBIDate=" + sendToNCBIDate +
                ", checkedNCBIError='" + checkedNCBIError + '\'' +
                ", fileName='" + fileName + '\'' +
                ", curator=" + curator +
                ", curationStatus=" + curationStatus +
                ", ethnicityBackFilled='" + ethnicityBackFilled + '\'' +
                ", recheckSNPs='" + recheckSNPs + '\'' +
                ", studyAddedDate=" + studyAddedDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}
