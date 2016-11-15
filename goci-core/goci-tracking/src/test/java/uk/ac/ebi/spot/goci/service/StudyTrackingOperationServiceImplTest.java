package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.EventBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by emma on 24/05/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyTrackingOperationServiceImplTest {

    private StudyTrackingOperationServiceImpl studyTrackingOperationService;

    @Mock
    private EventOperationsService eventOperationsService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Study STUDY = new StudyBuilder().setId(100L).build();

    private static final Event CREATE_EVENT = new EventBuilder().setId(99L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("STUDY_CREATION")
            .build();

    private static final Event STATUS_EVENT = new EventBuilder().setId(98L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE")
            .build();

    private static final Event UPDATE_EVENT = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("STUDY_UPDATE")
            .build();

    private static final Event DELETE_EVENT = new EventBuilder().setId(96L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("STUDY_DELETION")
            .build();

    private static final Event UPDATE_EVENT_WITH_DESCRIPTION = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("STUDY_UPDATE")
            .setEventDescription("DESCRIPTION")
            .build();

    @Before
    public void setUp() throws Exception {
        studyTrackingOperationService = new StudyTrackingOperationServiceImpl(eventOperationsService);
        STUDY.setEvents(new ArrayList<>());
    }

    @Test
    public void create() throws Exception {
        when(eventOperationsService.createEvent("STUDY_CREATION", SECURE_USER)).thenReturn(CREATE_EVENT);
        studyTrackingOperationService.create(STUDY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent("STUDY_CREATION", SECURE_USER);
        assertThat(STUDY.getEvents()).hasSize(1);
        assertThat(STUDY.getEvents()).extracting(event -> event.getUser().getEmail()).contains("test@test.com");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventType()).contains("STUDY_CREATION");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void update() throws Exception {
        when(eventOperationsService.createEvent("STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE",
                                                SECURE_USER)).thenReturn(STATUS_EVENT);
        when(eventOperationsService.createEvent("STUDY_UPDATE", SECURE_USER)).thenReturn(UPDATE_EVENT);

        studyTrackingOperationService.update(STUDY, SECURE_USER, "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE");
        studyTrackingOperationService.update(STUDY, SECURE_USER, "STUDY_UPDATE");

        verify(eventOperationsService, times(1)).createEvent("STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE",
                                                             SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent("STUDY_UPDATE", SECURE_USER);

        assertThat(STUDY.getEvents()).hasSize(2);
        assertThat(STUDY.getEvents()).extracting(event -> event.getUser().getEmail()).containsOnly("test@test.com");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventType())
                .containsOnly("STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE",
                              "STUDY_UPDATE");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void updateWithDescription() throws Exception {

        when(eventOperationsService.createEvent("STUDY_UPDATE", SECURE_USER, "DESCRIPTION")).thenReturn(
                UPDATE_EVENT_WITH_DESCRIPTION);

        studyTrackingOperationService.update(STUDY, SECURE_USER, "STUDY_UPDATE", "DESCRIPTION");

        verify(eventOperationsService, times(1)).createEvent("STUDY_UPDATE", SECURE_USER, "DESCRIPTION");

        assertThat(STUDY.getEvents()).hasSize(1);
        assertThat(STUDY.getEvents()).extracting(event -> event.getUser().getEmail()).containsOnly("test@test.com");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventType())
                .containsOnly("STUDY_UPDATE");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventDescription())
                .containsOnly("DESCRIPTION");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void delete() throws Exception {
        when(eventOperationsService.createEvent("STUDY_DELETION", SECURE_USER)).thenReturn(DELETE_EVENT);
        studyTrackingOperationService.delete(STUDY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent("STUDY_DELETION", SECURE_USER);
        assertThat(STUDY.getEvents()).hasSize(1);
        assertThat(STUDY.getEvents()).extracting(event -> event.getUser().getEmail()).containsOnly("test@test.com");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventType())
                .containsOnly("STUDY_DELETION");
        assertThat(STUDY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

}