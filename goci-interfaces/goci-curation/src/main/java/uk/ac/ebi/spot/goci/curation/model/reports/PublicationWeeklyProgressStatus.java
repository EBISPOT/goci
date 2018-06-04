package uk.ac.ebi.spot.goci.curation.model.reports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trish on 11-Apr-2018
 *
 * @author twhetzel
 *
 * Helper class to determine when all studies for a given publication
 * have reached a status of interest, e.g. have had an EventType of
 * STUDY_CREATION, STUDY_STATUS_CHANGE_PUBLISH_STUDY, STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE
 * and STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE.
 *
 */

public class PublicationWeeklyProgressStatus {

    private String pubmedId;

    private List<Long> studyIds = new ArrayList<>();

    private Long totalStudyCount;

    private Long count_Created;

    private Long count_Level1_CurationDone;

    private Long count_Level2_CurationDone;

    private Long count_Published;

    // constructor
    public PublicationWeeklyProgressStatus(String pubmedId, List studyIds) {
        this.pubmedId = pubmedId;
        this.studyIds = studyIds;
        this.totalStudyCount = 0L;
        this.count_Created = 0L;
        this.count_Level1_CurationDone = 0L;
        this.count_Level2_CurationDone = 0L;
        this.count_Published = 0L;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public List<Long> getStudyIds() {
        return studyIds;
    }

    public void setStudyIds(List studyIds) {
//        this.studyIds=studyIds;
        studyIds.addAll(studyIds);
    }

    public Long getTotalStudyCount() {
        totalStudyCount = Long.valueOf(studyIds.size());
        // NEW
//        if (totalStudyCount == null) {
//            totalStudyCount = 0L;
//        }
        return totalStudyCount;
    }

    public void setTotalStudyCount(Long totalStudyCount) {

        this.totalStudyCount = Long.valueOf(studyIds.size());
//        this.totalStudyCount = totalStudyCount;
    }

    public Long getCount_Created() {
        return count_Created;
    }

    public void setCount_Created(Long count_Created) {
        this.count_Created = count_Created;
    }

    public Long getCount_Level1_CurationDone() {
        return count_Level1_CurationDone;
    }

    public void setCount_Level1_CurationDone(Long count_Level1_CurationDone) {
        this.count_Level1_CurationDone = count_Level1_CurationDone;
    }

    public Long getCount_Level2_CurationDone() {
        return count_Level2_CurationDone;
    }

    public void setCount_Level2_CurationDone(Long count_Level2_CurationDone) {
        this.count_Level2_CurationDone = count_Level2_CurationDone;
    }

    public Long getCount_Published() {
        return count_Published;
    }

    public void setCount_Published(Long count_Published) {
        this.count_Published = count_Published;
    }
}
