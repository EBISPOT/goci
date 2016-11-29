package uk.ac.ebi.spot.goci.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AncestryBuilder;
import uk.ac.ebi.spot.goci.builder.EventBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Event;
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
public class AncestryTrackingOperationServiceImplTest {

    private AncestryTrackingOperationServiceImpl trackingOperationService;

    @Mock
    private EventOperationsService eventOperationsService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Ancestry ANCESTRY = new AncestryBuilder().setId(100L).build();

    private static final Event CREATE_EVENT = new EventBuilder().setId(99L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ANCESTRY_CREATED")
            .build();

    private static final Event UPDATE_EVENT = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ANCESTRY_UPDATED")
            .build();

    private static final Event DELETE_EVENT = new EventBuilder().setId(96L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ANCESTRY_DELETED")
            .build();


    @Before
    public void setUp() throws Exception {
        trackingOperationService = new AncestryTrackingOperationServiceImpl(eventOperationsService);
        ANCESTRY.setEvents(new ArrayList<>());
    }

    @Test
    public void create() throws Exception {
        when(eventOperationsService.createEvent("ANCESTRY_CREATED", SECURE_USER)).thenReturn(CREATE_EVENT);
        trackingOperationService.create(ANCESTRY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent("ANCESTRY_CREATED", SECURE_USER);
        Assertions.assertThat(ANCESTRY.getEvents()).hasSize(1);
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getEventType())
                .contains("ANCESTRY_CREATED");
        Assertions.assertThat(ANCESTRY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void delete() throws Exception {
        when(eventOperationsService.createEvent("ANCESTRY_DELETED", SECURE_USER)).thenReturn(DELETE_EVENT);
        trackingOperationService.delete(ANCESTRY, SECURE_USER);
        verify(eventOperationsService, times(1)).createEvent("ANCESTRY_DELETED", SECURE_USER);
        Assertions.assertThat(ANCESTRY.getEvents()).hasSize(1);
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getEventType())
                .contains("ANCESTRY_DELETED");
        Assertions.assertThat(ANCESTRY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }

    @Test
    public void update() throws Exception {
        when(eventOperationsService.createEvent("ANCESTRY_UPDATED", SECURE_USER)).thenReturn(UPDATE_EVENT);
        trackingOperationService.update(ANCESTRY, SECURE_USER, "ANCESTRY_UPDATED");
        verify(eventOperationsService, times(1)).createEvent("ANCESTRY_UPDATED", SECURE_USER);
        Assertions.assertThat(ANCESTRY.getEvents()).hasSize(1);
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getUser().getEmail())
                .contains("test@test.com");
        Assertions.assertThat(ANCESTRY.getEvents())
                .extracting(event -> event.getEventType())
                .contains("ANCESTRY_UPDATED");
        Assertions.assertThat(ANCESTRY.getEvents()).extracting(event -> event.getEventDate()).isNotNull();
    }
}