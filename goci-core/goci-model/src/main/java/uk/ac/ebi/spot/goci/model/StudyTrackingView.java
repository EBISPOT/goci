package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;


/**
 * Created by Cinzia on 8/11/16.
 *
 * @author Cinzia
 *         <p>
 *         Model object representing study tracking view and its attributes
 */
@Entity
public class StudyTrackingView {

    @Id
    private Long studyId;

    private Integer hasEvent;

    private String status;

    private String pubmedId;

    private Long housekeepingId;

    private Date publicationDate;

    private Date lastUpdateDate;

    private Date catalogPublishDate;

    private Date studyAddedDate;

    private Integer curationStatusId;

    private Long curatorId;

    private Date catalogUnpublishDate;

    private Integer unpublishReasonId;

    private Integer isPublished;


    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Integer getHasEvent() {
        return hasEvent;
    }

    public void setHasEvent(Integer hasEvent) {
        this.hasEvent = hasEvent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Long getHousekeepingId() {
        return housekeepingId;
    }

    public void setHousekeepingId(Long housekeepingId) {
        this.housekeepingId = housekeepingId;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getCatalogPublishDate() {
        return catalogPublishDate;
    }

    public void setCatalogPublishDate(Date catalogPublishDate) {
        this.catalogPublishDate = catalogPublishDate;
    }

    public Date getStudyAddedDate() {
        return studyAddedDate;
    }

    public void setStudyAddedDate(Date studyAddedDate) {
        this.studyAddedDate = studyAddedDate;
    }

    public Integer getCurationStatusId() {
        return curationStatusId;
    }

    public void setCurationStatusId(Integer curationStatusId) {
        this.curationStatusId = curationStatusId;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }

    public Date getCatalogUnpublishDate() {
        return catalogUnpublishDate;
    }

    public void setCatalogUnpublishDate(Date catalogUnpublishDate) {
        this.catalogUnpublishDate = catalogUnpublishDate;
    }

    public Integer getUnpublishReasonId() {
        return unpublishReasonId;
    }

    public void setUnpublishReasonId(Integer unpublishReasonId) {
        this.unpublishReasonId = unpublishReasonId;
    }

    public Integer getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Integer isPublished) {
        this.isPublished = isPublished;
    }
}

