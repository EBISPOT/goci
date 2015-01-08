package uk.ac.ebi.spot.goci.curation.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;


/**
 * Created by emma on 03/12/14.
 *
 * @author emma
 *         <p>
 *         Model representing housekeeping information stored about a study that is used during curation
 */
@Entity
@Table(name = "HOUSEKEEPING")
public class Housekeeping {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "STUDYSNPCHECKEDL1")
    private String studySnpCheckedLevelOne;

    @Column(name = "STUDYSNPCHECKEDL2")
    private String studySnpCheckedLevelTwo;

    @Column(name = "ETHNICITYCHECKEDL1")
    private String ethnicityCheckedLevelOne;

    @Column(name = "ETHNICITYCHECKEDL2")
    private String ethnicityCheckedLevelTwo;

    @Column(name = "ETHNICITYBACKFILLED")
    private String ethnicityBackFilled;

    @Column(name = "CHECKEDNCBIERROR")
    private String checkedNCBIError;

    @Column(name = "PUBLISHDATE")
    private Date publishDate;

    @Column(name = "SENDTONCBIDATE")
    private Date sendToNCBIDate;

    @Column(name = "STUDYADDEDDATE")
    private Timestamp studyAddedDate;

    @Column(name = "LASTUPDATEDATE")
    private Timestamp lastUpdateDate;

    @Column(name = "FILENAM")
    private String fileName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURATORID")
    private Curator curator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURATORSTATUSID")
    private CurationStatus curationStatus;

    @Column(name = "NOTES")
    private String notes;


    // JPA no-args constructor
    public Housekeeping() {
    }

    public Housekeeping(String studySnpCheckedLevelOne, String studySnpCheckedLevelTwo, String ethnicityCheckedLevelOne, String ethnicityCheckedLevelTwo, String ethnicityBackFilled, String checkedNCBIError, Date publishDate, Date sendToNCBIDate, Timestamp studyAddedDate, Timestamp lastUpdateDate, String fileName, Curator curator, CurationStatus curationStatus, String notes) {
        this.studySnpCheckedLevelOne = studySnpCheckedLevelOne;
        this.studySnpCheckedLevelTwo = studySnpCheckedLevelTwo;
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
        this.ethnicityBackFilled = ethnicityBackFilled;
        this.checkedNCBIError = checkedNCBIError;
        this.publishDate = publishDate;
        this.sendToNCBIDate = sendToNCBIDate;
        this.studyAddedDate = studyAddedDate;
        this.lastUpdateDate = lastUpdateDate;
        this.fileName = fileName;
        this.curator = curator;
        this.curationStatus = curationStatus;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudySnpCheckedLevelOne() {
        return studySnpCheckedLevelOne;
    }

    public void setStudySnpCheckedLevelOne(String studySnpCheckedLevelOne) {
        this.studySnpCheckedLevelOne = studySnpCheckedLevelOne;
    }

    public String getStudySnpCheckedLevelTwo() {
        return studySnpCheckedLevelTwo;
    }

    public void setStudySnpCheckedLevelTwo(String studySnpCheckedLevelTwo) {
        this.studySnpCheckedLevelTwo = studySnpCheckedLevelTwo;
    }

    public String getEthnicityCheckedLevelOne() {
        return ethnicityCheckedLevelOne;
    }

    public void setEthnicityCheckedLevelOne(String ethnicityCheckedLevelOne) {
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
    }

    public String getEthnicityCheckedLevelTwo() {
        return ethnicityCheckedLevelTwo;
    }

    public void setEthnicityCheckedLevelTwo(String ethnicityCheckedLevelTwo) {
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
    }

    public String getEthnicityBackFilled() {
        return ethnicityBackFilled;
    }

    public void setEthnicityBackFilled(String ethnicityBackFilled) {
        this.ethnicityBackFilled = ethnicityBackFilled;
    }

    public String getCheckedNCBIError() {
        return checkedNCBIError;
    }

    public void setCheckedNCBIError(String checkedNCBIError) {
        this.checkedNCBIError = checkedNCBIError;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getSendToNCBIDate() {
        return sendToNCBIDate;
    }

    public void setSendToNCBIDate(Date sendToNCBIDate) {
        this.sendToNCBIDate = sendToNCBIDate;
    }

    public Timestamp getStudyAddedDate() {
        return studyAddedDate;
    }

    public void setStudyAddedDate(Timestamp studyAddedDate) {
        this.studyAddedDate = studyAddedDate;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Curator getCurator() {
        return curator;
    }

    public void setCurator(Curator curator) {
        this.curator = curator;
    }

    public CurationStatus getCurationStatus() {
        return curationStatus;
    }

    public void setCurationStatus(CurationStatus curationStatus) {
        this.curationStatus = curationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Housekeeping{" +
                "id=" + id +
                ", studySnpCheckedLevelOne='" + studySnpCheckedLevelOne + '\'' +
                ", studySnpCheckedLevelTwo='" + studySnpCheckedLevelTwo + '\'' +
                ", ethnicityCheckedLevelOne='" + ethnicityCheckedLevelOne + '\'' +
                ", ethnicityCheckedLevelTwo='" + ethnicityCheckedLevelTwo + '\'' +
                ", ethnicityBackFilled='" + ethnicityBackFilled + '\'' +
                ", checkedNCBIError='" + checkedNCBIError + '\'' +
                ", publishDate=" + publishDate +
                ", sendToNCBIDate=" + sendToNCBIDate +
                ", studyAddedDate=" + studyAddedDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", fileName='" + fileName + '\'' +
                ", curator=" + curator +
                ", curationStatus=" + curationStatus +
                ", notes='" + notes + '\'' +
                '}';
    }
}
