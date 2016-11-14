package uk.ac.ebi.spot.goci.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.EventBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by emma on 18/07/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class AssociationTrackingOperationServiceImplTest {

    private AssociationTrackingOperationServiceImpl associationTrackingOperationService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Association ASSOCIATION = new AssociationBuilder().setId((long) 200).build();

    private static final Event CREATE_EVENT = new EventBuilder().setId(99L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_CREATION")
            .build();

    private static final Event UPDATE_EVENT = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_UPDATE")
            .build();

    private static final Event MAPPING_EVENT = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_MAPPING")
            .build();

    private static final Event APPROVE = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_APPROVED")
            .build();

    private static final Event UNAPPROVE = new EventBuilder().setId(97L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_UNAPPROVED")
            .build();

    private static final Event DELETE_EVENT = new EventBuilder().setId(96L)
            .setEventDate(new Date())
            .setUser(SECURE_USER)
            .setEventType("ASSOCIATION_DELETION")
            .build();

    @Mock
    private EventOperationsService eventOperationsService;

    @Before
    public void setUp() throws Exception {
        associationTrackingOperationService = new AssociationTrackingOperationServiceImpl(eventOperationsService);
        ASSOCIATION.setEvents(new ArrayList<>());
    }

    @Test
    public void create() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_CREATION", SECURE_USER)).thenReturn(CREATE_EVENT);

        associationTrackingOperationService.create(ASSOCIATION, SECURE_USER);
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_CREATION");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_CREATION", SECURE_USER);
    }

    @Test
    public void update() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_UPDATE", SECURE_USER)).thenReturn(UPDATE_EVENT);

        associationTrackingOperationService.update(ASSOCIATION, SECURE_USER,"ASSOCIATION_UPDATE");
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_UPDATE");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_UPDATE", SECURE_USER);
    }

    @Test
    public void mapping() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_MAPPING", SECURE_USER)).thenReturn(MAPPING_EVENT);

        associationTrackingOperationService.update(ASSOCIATION, SECURE_USER,"ASSOCIATION_MAPPING");
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_MAPPING");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_MAPPING", SECURE_USER);
    }

    @Test
    public void approve() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_APPROVED", SECURE_USER)).thenReturn(APPROVE);

        associationTrackingOperationService.update(ASSOCIATION, SECURE_USER,"ASSOCIATION_APPROVED");
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_APPROVED");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_APPROVED", SECURE_USER);
    }

    @Test
    public void unapprove() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_UNAPPROVED", SECURE_USER)).thenReturn(UNAPPROVE);

        associationTrackingOperationService.update(ASSOCIATION, SECURE_USER,"ASSOCIATION_UNAPPROVED");
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_UNAPPROVED");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_UNAPPROVED", SECURE_USER);
    }

    @Test
    public void delete() throws Exception {
        // Stubbing
        when(eventOperationsService.createEvent("ASSOCIATION_DELETION", SECURE_USER)).thenReturn(DELETE_EVENT);

        associationTrackingOperationService.delete(ASSOCIATION, SECURE_USER);
        assertThat(ASSOCIATION.getEvents()).hasSize(1);
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventType)
                .containsOnly("ASSOCIATION_DELETION");
        assertThat(ASSOCIATION.getEvents()).extracting(event -> event.getUser().getEmail())
                .containsOnly(SECURE_USER.getEmail());
        assertThat(ASSOCIATION.getEvents()).extracting(Event::getEventDate).isNotNull();
        verify(eventOperationsService, times(1)).createEvent("ASSOCIATION_DELETION", SECURE_USER);
    }
}