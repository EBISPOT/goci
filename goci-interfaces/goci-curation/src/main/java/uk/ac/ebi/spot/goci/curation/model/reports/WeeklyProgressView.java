package uk.ac.ebi.spot.goci.curation.model.reports;

import java.util.Date;
import java.util.Set;

/**
 * Created by emma on 09/06/2016.
 *
 * @author emma
 *         <p>
 *         Model class to represent weekly curation progress
 */
public class WeeklyProgressView {

    private Date weekDate;

    private Set<Long> studiesCreated;

    private Set<Long> studiesLevel1Completed;

    private Set<Long> studiesLevel2Completed;

    private Set<Long> studiesPublished;

    public WeeklyProgressView(Date weekDate) {
        this.weekDate = weekDate;
    }

    public Date getWeekDate() {
        return weekDate;
    }

    public void setWeekDate(Date weekDate) {
        this.weekDate = weekDate;
    }

    public Set<Long> getStudiesCreated() {
        return studiesCreated;
    }

    public void setStudiesCreated(Set<Long> studiesCreated) {
        this.studiesCreated = studiesCreated;
    }

    public Set<Long> getStudiesLevel1Completed() {
        return studiesLevel1Completed;
    }

    public void setStudiesLevel1Completed(Set<Long> studiesLevel1Completed) {
        this.studiesLevel1Completed = studiesLevel1Completed;
    }

    public Set<Long> getStudiesLevel2Completed() {
        return studiesLevel2Completed;
    }

    public void setStudiesLevel2Completed(Set<Long> studiesLevel2Completed) {
        this.studiesLevel2Completed = studiesLevel2Completed;
    }

    public Set<Long> getStudiesPublished() {
        return studiesPublished;
    }

    public void setStudiesPublished(Set<Long> studiesPublished) {
        this.studiesPublished = studiesPublished;
    }
}