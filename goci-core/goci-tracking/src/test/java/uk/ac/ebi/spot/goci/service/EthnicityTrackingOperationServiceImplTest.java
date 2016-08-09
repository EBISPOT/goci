package uk.ac.ebi.spot.goci.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.EthnicityBuilder;
import uk.ac.ebi.spot.goci.builder.EventBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 08/08/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class EthnicityTrackingOperationServiceImplTest {

    private EthnicityTrackingOperationServiceImpl trackingOperationService;

    @Mock
    private EventOperationsService eventOperationsService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Ethnicity ETHNICITY = new EthnicityBuilder().setId(100L).build();

    private static final Event CREATE_EVENT = new EventBuilder().setId(99L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType(EventType.ETHNICITY_CREATED)
            .build();

    private static final Event UPDATE_EVENT = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType(EventType.ETHNICITY_UPDATED)
            .build();

    private static final Event DELETE_EVENT = new EventBuilder().setId(96L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType(EventType.ETHNICITY_DELETED)
            .build();


    @Before
    public void setUp() throws Exception {
        trackingOperationService = new EthnicityTrackingOperationServiceImpl(eventOperationsService);
        ETHNICITY.setEvents(new ArrayList<>());
    }

    @Test
    public void create() throws Exception {
        when(eventOperationsService.createEvent(EventType.ETHNICITY_CREATED, SECURE_USER)).thenReturn(CREATE_EVENT);
        trackingOperationService.create(ETHNICITY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent(EventType.ETHNICITY_CREATED, SECURE_USER);
        Assertions.assertThat(ETHNICITY.getEvents()).hasSize(1);
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getEventType())
                .contains(EventType.ETHNICITY_CREATED);
        Assertions.assertThat(ETHNICITY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void delete() throws Exception {
        when(eventOperationsService.createEvent(EventType.ETHNICITY_DELETED, SECURE_USER)).thenReturn(DELETE_EVENT);
        trackingOperationService.delete(ETHNICITY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent(EventType.ETHNICITY_DELETED, SECURE_USER);
        Assertions.assertThat(ETHNICITY.getEvents()).hasSize(1);
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getEventType())
                .contains(EventType.ETHNICITY_DELETED);
        Assertions.assertThat(ETHNICITY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void update() throws Exception {
        when(eventOperationsService.createEvent(EventType.ETHNICITY_UPDATED, SECURE_USER)).thenReturn(UPDATE_EVENT);
        trackingOperationService.update(ETHNICITY, SECURE_USER, EventType.ETHNICITY_UPDATED);
        verify(eventOperationsService, times(1)).createEvent(EventType.ETHNICITY_UPDATED, SECURE_USER);
        Assertions.assertThat(ETHNICITY.getEvents()).hasSize(1);
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ETHNICITY.getEvents())
                .extracting(event -> event.getEventType())
                .contains(EventType.ETHNICITY_UPDATED);
        Assertions.assertThat(ETHNICITY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }
}