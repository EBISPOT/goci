package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.UnpublishReason;

import java.util.Date;

public class HousekeepingBuilder {

    private Housekeeping housekeeping = new Housekeeping();

    public HousekeepingBuilder setId(Long id) {
        housekeeping.setId(id);
        return this;
    }

    public HousekeepingBuilder setStudySnpCheckedLevelOne(Boolean studySnpCheckedLevelOne) {
        housekeeping.setStudySnpCheckedLevelOne(studySnpCheckedLevelOne);
        return this;
    }

    public HousekeepingBuilder setStudySnpCheckedLevelTwo(Boolean studySnpCheckedLevelTwo) {
        housekeeping.setStudySnpCheckedLevelTwo(studySnpCheckedLevelTwo);
        return this;
    }

    public HousekeepingBuilder setAncestryCheckedLevelOne(Boolean ancestryCheckedLevelOne) {
        housekeeping.setAncestryCheckedLevelOne(ancestryCheckedLevelOne);
        return this;
    }

    public HousekeepingBuilder setAncestryCheckedLevelTwo(Boolean ancestryCheckedLevelTwo) {
        housekeeping.setAncestryCheckedLevelTwo(ancestryCheckedLevelTwo);
        return this;
    }

    public HousekeepingBuilder setAncestryBackFilled(Boolean ancestryBackFilled) {
        housekeeping.setAncestryBackFilled(ancestryBackFilled);
        return this;
    }

    public HousekeepingBuilder setCheckedMappingError(Boolean checkedMappingError) {
        housekeeping.setCheckedMappingError(checkedMappingError);
        return this;
    }

    public HousekeepingBuilder setSnpsRechecked(Boolean snpsRechecked) {
        housekeeping.setSnpsRechecked(snpsRechecked);
        return this;
    }

    public HousekeepingBuilder setIsPublished(Boolean isPublished) {
        housekeeping.setIsPublished(isPublished);
        return this;
    }

    public HousekeepingBuilder setCatalogPublishDate(Date catalogPublishDate) {
        housekeeping.setCatalogPublishDate(catalogPublishDate);
        return this;
    }

    public HousekeepingBuilder setSendToNCBIDate(Date sendToNCBIDate) {
        housekeeping.setSendToNCBIDate(sendToNCBIDate);
        return this;
    }

    public HousekeepingBuilder setStudyAddedDate(Date studyAddedDate) {
        housekeeping.setStudyAddedDate(studyAddedDate);
        return this;
    }

    public HousekeepingBuilder setCatalogUnpublishDate(Date catalogUnpublishDate) {
        housekeeping.setCatalogUnpublishDate(catalogUnpublishDate);
        return this;
    }

    public HousekeepingBuilder setLastUpdateDate(Date lastUpdateDate) {
        housekeeping.setLastUpdateDate(lastUpdateDate);
        return this;
    }

    public HousekeepingBuilder setFileName(String fileName) {
        housekeeping.setFileName(fileName);
        return this;
    }

    public HousekeepingBuilder setNotes(String notes) {
        housekeeping.setNotes(notes);
        return this;
    }

    public HousekeepingBuilder setCurator(Curator curator) {
        housekeeping.setCurator(curator);
        return this;
    }

    public HousekeepingBuilder setCurationStatus(CurationStatus curationStatus) {
        housekeeping.setCurationStatus(curationStatus);
        return this;
    }

    public HousekeepingBuilder setUnpublishReason(UnpublishReason unpublishReason) {
        housekeeping.setUnpublishReason(unpublishReason);
        return this;
    }

    public Housekeeping build() {
        return housekeeping;
    }
}