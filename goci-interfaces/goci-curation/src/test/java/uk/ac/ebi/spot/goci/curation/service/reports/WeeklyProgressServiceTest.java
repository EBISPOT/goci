package uk.ac.ebi.spot.goci.curation.service.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.WeeklyProgressViewBuilder;
import uk.ac.ebi.spot.goci.curation.model.reports.ReportsWeeklyProgressView;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;
import uk.ac.ebi.spot.goci.repository.WeeklyProgressViewRepository;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 20/06/2016.
 *
 * @author emma
 *         <p>
 *         Test for WeeklyProgressService
 */
@RunWith(MockitoJUnitRunner.class)
public class WeeklyProgressServiceTest {

    @Mock
    private WeeklyProgressViewRepository weeklyProgressViewRepository;

    private WeeklyProgressService weeklyProgressService;

    private static final Date testDate = new GregorianCalendar(2016, Calendar.FEBRUARY, 11).getTime();

    private static final WeeklyProgressView STUDY_CREATION =
            new WeeklyProgressViewBuilder().setStudyId((long) 100).setEventType(
                    "STUDY_CREATION").setId((long) 1).setWeekStartDay(testDate).build();

    private static final WeeklyProgressView STUDY_PUBLISH =
            new WeeklyProgressViewBuilder().setStudyId((long) 101).setEventType(
                    "STUDY_STATUS_CHANGE_PUBLISH_STUDY").setId((long) 2).setWeekStartDay(testDate).build();

    private static final WeeklyProgressView STUDY_LEVEL_1_EV_01 =
            new WeeklyProgressViewBuilder().setStudyId((long) 102)
                    .setEventType(
                            "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE")
                    .setId((long) 3)
                    .setWeekStartDay(testDate)
                    .build();

    private static final WeeklyProgressView STUDY_LEVEL_1_EV_02 =
            new WeeklyProgressViewBuilder().setStudyId((long) 102)
                    .setEventType(
                            "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE")
                    .setId((long) 4)
                    .setWeekStartDay(testDate)
                    .build();

    private static final WeeklyProgressView STUDY_LEVEL_2 =
            new WeeklyProgressViewBuilder().setStudyId((long) 103)
                    .setEventType(
                            "STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE")
                    .setId((long) 5)
                    .setWeekStartDay(testDate)
                    .build();

    @Before
    public void setUp() throws Exception {
        weeklyProgressService = new WeeklyProgressService(weeklyProgressViewRepository);
    }

    @Test
    public void processWeeklyView() throws Exception {

        // Stubbing
        when(weeklyProgressViewRepository.findAll()).thenReturn(Arrays.asList(STUDY_CREATION,
                                                                              STUDY_LEVEL_1_EV_01,
                                                                              STUDY_LEVEL_1_EV_02,
                                                                              STUDY_LEVEL_2,
                                                                              STUDY_PUBLISH));

        List<ReportsWeeklyProgressView> views = weeklyProgressService.processWeeklyView();

        // Assertions
        assertThat(views).hasSize(1);
        assertThat(views).extracting("studiesCreated",
                                     "studiesLevel1Completed",
                                     "studiesLevel2Completed",
                                     "studiesPublished")
                .containsOnly(tuple(Collections.singleton(STUDY_CREATION.getStudyId()),
                                    Collections.singleton(102L),
                                    Collections.singleton(STUDY_LEVEL_2.getStudyId()),
                                    Collections.singleton(STUDY_PUBLISH.getStudyId())));
    }
}