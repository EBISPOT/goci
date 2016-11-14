package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;

import java.util.Date;

/**
 * Created by emma on 20/06/2016.
 *
 * @author emma
 *         <p>
 *         Builder for WeeklyProgressView used in testing
 */
public class WeeklyProgressViewBuilder {

    private WeeklyProgressView weeklyProgressView = new WeeklyProgressView();

    public WeeklyProgressViewBuilder setId(Long id) {
        weeklyProgressView.setId(id);
        return this;
    }

    public WeeklyProgressViewBuilder setWeekStartDay(Date weekStartDay) {
        weeklyProgressView.setWeekStartDay(weekStartDay);
        return this;
    }

    public WeeklyProgressViewBuilder setEventType(String eventType) {
        weeklyProgressView.setEventType(eventType);
        return this;
    }

    public WeeklyProgressViewBuilder setStudyId(Long studyId) {
        weeklyProgressView.setStudyId(studyId);
        return this;
    }

    public WeeklyProgressView build() {
        return weeklyProgressView;
    }

}
