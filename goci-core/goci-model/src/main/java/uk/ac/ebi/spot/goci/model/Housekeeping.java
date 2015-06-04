package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;


/**
 * Created by emma on 03/12/14.
 *
 * @author emma
 *         <p/>
 *         Model representing housekeeping information stored about a study that is used during curation
 */
@Entity
public class Housekeeping {
    @Id
    @GeneratedValue
    private Long id;

    private Boolean studySnpCheckedLevelOne = false;

    private Boolean studySnpCheckedLevelTwo = false;

    private Boolean ethnicityCheckedLevelOne = false;

    private Boolean ethnicityCheckedLevelTwo  = false;

    private Boolean ethnicityBackFilled  = false;

    private Boolean checkedNCBIError = false;

    private Boolean snpsRechecked = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date catalogPublishDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendToNCBIDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date studyAddedDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date catalogUnpublishDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    private String fileName;

    private String notes;

    @OneToOne
    private Curator curator;

    @OneToOne
    private CurationStatus curationStatus;

    // JPA no-args constructor
    public Housekeeping() {
    }

    public Housekeeping(Boolean studySnpCheckedLevelOne, Boolean studySnpCheckedLevelTwo, Boolean ethnicityCheckedLevelOne, Boolean ethnicityCheckedLevelTwo, Boolean ethnicityBackFilled, Boolean checkedNCBIError, Boolean snpsRechecked, Date catalogPublishDate, Date sendToNCBIDate, Date studyAddedDate, Date catalogUnpublishDate, Date lastUpdateDate, String fileName, String notes, Curator curator, CurationStatus curationStatus) {
        this.studySnpCheckedLevelOne = studySnpCheckedLevelOne;
        this.studySnpCheckedLevelTwo = studySnpCheckedLevelTwo;
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
        this.ethnicityBackFilled = ethnicityBackFilled;
        this.checkedNCBIError = checkedNCBIError;
        this.snpsRechecked = snpsRechecked;
        this.catalogPublishDate = catalogPublishDate;
        this.sendToNCBIDate = sendToNCBIDate;
        this.studyAddedDate = studyAddedDate;
        this.catalogUnpublishDate = catalogUnpublishDate;
        this.lastUpdateDate = lastUpdateDate;
        this.fileName = fileName;
        this.notes = notes;
        this.curator = curator;
        this.curationStatus = curationStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStudySnpCheckedLevelOne() {
        return studySnpCheckedLevelOne;
    }

    public void setStudySnpCheckedLevelOne(Boolean studySnpCheckedLevelOne) {
        this.studySnpCheckedLevelOne = studySnpCheckedLevelOne;
    }

    public Boolean getStudySnpCheckedLevelTwo() {
        return studySnpCheckedLevelTwo;
    }

    public void setStudySnpCheckedLevelTwo(Boolean studySnpCheckedLevelTwo) {
        this.studySnpCheckedLevelTwo = studySnpCheckedLevelTwo;
    }

    public Boolean getEthnicityCheckedLevelOne() {
        return ethnicityCheckedLevelOne;
    }

    public void setEthnicityCheckedLevelOne(Boolean ethnicityCheckedLevelOne) {
        this.ethnicityCheckedLevelOne = ethnicityCheckedLevelOne;
    }

    public Boolean getEthnicityCheckedLevelTwo() {
        return ethnicityCheckedLevelTwo;
    }

    public void setEthnicityCheckedLevelTwo(Boolean ethnicityCheckedLevelTwo) {
        this.ethnicityCheckedLevelTwo = ethnicityCheckedLevelTwo;
    }

    public Boolean getEthnicityBackFilled() {
        return ethnicityBackFilled;
    }

    public void setEthnicityBackFilled(Boolean ethnicityBackFilled) {
        this.ethnicityBackFilled = ethnicityBackFilled;
    }

    public Boolean getCheckedNCBIError() {
        return checkedNCBIError;
    }

    public void setCheckedNCBIError(Boolean checkedNCBIError) {
        this.checkedNCBIError = checkedNCBIError;
    }

    public Boolean getSnpsRechecked() {
        return snpsRechecked;
    }

    public void setSnpsRechecked(Boolean snpsRechecked) {
        this.snpsRechecked = snpsRechecked;
    }

    public Date getCatalogPublishDate() {
        return catalogPublishDate;
    }

    public void setCatalogPublishDate(Date catalogPublishDate) {
        this.catalogPublishDate = catalogPublishDate;
    }

    public Date getSendToNCBIDate() {
        return sendToNCBIDate;
    }

    public void setSendToNCBIDate(Date sendToNCBIDate) {
        this.sendToNCBIDate = sendToNCBIDate;
    }

    public Date getStudyAddedDate() {
        return studyAddedDate;
    }

    public void setStudyAddedDate(Date studyAddedDate) {
        this.studyAddedDate = studyAddedDate;
    }

    public Date getCatalogUnpublishDate() {
        return catalogUnpublishDate;
    }

    public void setCatalogUnpublishDate(Date catalogUnpublishDate) {
        this.catalogUnpublishDate = catalogUnpublishDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    @Override
    public String toString() {
        return "Housekeeping{" +
                "id=" + id +
                ", studySnpCheckedLevelOne=" + studySnpCheckedLevelOne +
                ", studySnpCheckedLevelTwo=" + studySnpCheckedLevelTwo +
                ", ethnicityCheckedLevelOne=" + ethnicityCheckedLevelOne +
                ", ethnicityCheckedLevelTwo=" + ethnicityCheckedLevelTwo +
                ", ethnicityBackFilled=" + ethnicityBackFilled +
                ", checkedNCBIError=" + checkedNCBIError +
                ", snpsRechecked=" + snpsRechecked +
                ", catalogPublishDate=" + catalogPublishDate +
                ", sendToNCBIDate=" + sendToNCBIDate +
                ", studyAddedDate=" + studyAddedDate +
                ", catalogUnpublishDate=" + catalogUnpublishDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", fileName='" + fileName + '\'' +
                ", notes='" + notes + '\'' +
                ", curator=" + curator +
                ", curationStatus=" + curationStatus +
                '}';
    }

}
