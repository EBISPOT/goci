package uk.ac.ebi.spot.goci.curation.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssigneeBuilder;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StatusAssignmentBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.curation.service.tracking.EventTypeService;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 12/02/2016.
 *
 * @author emma
 *         <p>
 *         Test class for uk.ac.ebi.spot.goci.curation.service.StudyOperationsService
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyOperationServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private MailService mailService;

    @Mock
    private HousekeepingRepository housekeepingRepository;

    @Mock
    private PublishStudyCheckService publishStudyCheckService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private CuratorRepository curatorRepository;

    @Mock
    private CurationStatusRepository curationStatusRepository;

    @Mock
    private EventOperationsService eventOperationsService;

    @Mock
    private TrackingOperationService trackingOperationService;

    @Mock
    private EthnicityRepository ethnicityRepository;

    @Mock
    private EventTypeService eventTypeService;

    private StudyOperationsService studyOperationsService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final CurationStatus LEVEL_01 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus PUBLISH =
            new CurationStatusBuilder().setId(805L).setStatus("Publish study").build();

    private static final CurationStatus LEVEL_02 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 curation done").build();

    private static final CurationStatus CURRENT_STATUS1 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 ancestry done").build();

    private static final Curator CURATOR1 = new CuratorBuilder().setId(803L)
            .setLastName("Unassigned")
            .build();

    private static final Curator LEVEL_1_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Level 1 Curator")
            .build();

    private static final Curator PUBLISH_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("GWAS Catalog")
            .build();

    private static final Housekeeping CURRENT_HOUSEKEEPING =
            new HousekeepingBuilder().setId(799L).setCurationStatus(CURRENT_STATUS1).setCurator(CURATOR1).build();

    private static final Housekeeping NEW_HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(LEVEL_01)
                    .setCurator(LEVEL_1_CURATOR)
                    .setNotes("Some notes")
                    .setEthnicityCheckedLevelOne(true)
                    .setStudyAddedDate(new Date())
                    .build();

    private static final Housekeeping NEW_HOUSEKEEPING_NO_STATUS_CHANGE =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(CURRENT_STATUS1)
                    .setCurator(CURATOR1)
                    .setNotes("Some notes")
                    .setEthnicityCheckedLevelOne(true)
                    .setStudyAddedDate(new Date())
                    .build();

    private static final Housekeeping NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(PUBLISH)
                    .setCurator(PUBLISH_CURATOR)
                    .setNotes("Some notes")
                    .setEthnicityCheckedLevelOne(true)
                    .setStudyAddedDate(new Date())
                    .build();

    private static final Study STU1 =
            new StudyBuilder().setId(802L).setHousekeeping(CURRENT_HOUSEKEEPING).build();

    private static final Study NEW_STUDY = new StudyBuilder().setAuthor("Smith X")
            .setPubmedId("1001002")
            .setPublication("Nature")
            .setPublicationDate(new Date())
            .setTitle("Test")
            .build();

    private static final Association ASS1 =
            new AssociationBuilder().setId(800L)
                    .setSnpApproved(true).build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(801L)
                    .setSnpApproved(true).build();

    private static final Association ASS3 =
            new AssociationBuilder().setId(803L)
                    .setSnpApproved(false).build();

    private static final StatusAssignment STATUS_ASSIGNMENT =
            new StatusAssignmentBuilder().build();

    private static final Assignee ASSIGNEE = new AssigneeBuilder().build();

    @Before
    public void setUpMock() {
        studyOperationsService = new StudyOperationsService(associationRepository,
                                                            mailService,
                                                            housekeepingRepository,
                                                            publishStudyCheckService,
                                                            studyRepository,
                                                            curatorRepository,
                                                            curationStatusRepository,
                                                            trackingOperationService,
                                                            ethnicityRepository,
                                                            eventTypeService);
    }

    @After
    public void restoreTestStudy() {
        CURRENT_HOUSEKEEPING.setCurationStatus(CURRENT_STATUS1);
        CURRENT_HOUSEKEEPING.setCurator(CURATOR1);
        STU1.setHousekeeping(CURRENT_HOUSEKEEPING);
    }

    @Test
    public void testSaveStudy() {
        // Test saving a study
        Study study = studyOperationsService.createStudy(NEW_STUDY, SECURE_USER);
        verify(housekeepingRepository, times(1)).save(Matchers.any(Housekeeping.class));
        verify(studyRepository, times(1)).save(NEW_STUDY);
        verify(trackingOperationService, times(1)).create(NEW_STUDY, SECURE_USER);

        assertThat(study).extracting("author", "title", "publication", "pubmedId")
                .contains("Smith X", "Test", "Nature", "1001002");
        assertThat(study).extracting("publicationDate").isNotNull();
    }

    @Test
    public void testAssignStudyStatusToLevelOneCurationDone() {

        // Change assignment from "Level 2 ancestry done" to "Level 1 curation done"
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_01);
        when(eventTypeService.determineEventTypeFromStatus(LEVEL_01)).thenReturn(EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(mailService).sendEmailNotification(STU1, LEVEL_01.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(2)).save(STU1);
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(LEVEL_01);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);

        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 1 curation done");
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusToLevelTwoCurationDone() {

        // Change assignment from "Level 2 ancestry done" to "Level 2 curation done"
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_02);
        when(eventTypeService.determineEventTypeFromStatus(LEVEL_02)).thenReturn(EventType.STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE);

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(2)).save(STU1);
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(LEVEL_02);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE);

        verifyZeroInteractions(mailService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 2 curation done");
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusNoChange() {

        // No change to status
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(CURRENT_STATUS1);
        when(eventTypeService.determineEventTypeFromStatus(CURRENT_STATUS1)).thenReturn(EventType.STUDY_STATUS_CHANGE_LEVEL_2_ANCESTRY_DONE);

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);

        verifyZeroInteractions(housekeepingRepository);
        verifyZeroInteractions(studyRepository);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(trackingOperationService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(eventTypeService);

        assertEquals("Current status and new status are the same, no change required", message);
    }

    @Test
    public void testAssignStudyStatusPublishStudy() {

        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        // Stub behaviour
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(PUBLISH);
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(null);
        when(eventTypeService.determineEventTypeFromStatus(PUBLISH)).thenReturn(EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);

        String message = studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(PUBLISH);
        verify(mailService, times(1)).sendEmailNotification(STU1, PUBLISH.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(2)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Publish study");
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusPublishStudyWithStudyThatDoesNotPassPublishCheck() {
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS3);

        // Stub behaviour
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(PUBLISH);
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(Matchers.anyString());
        when(eventTypeService.determineEventTypeFromStatus(PUBLISH)).thenReturn(EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);

        String message = studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);

        verifyZeroInteractions(housekeepingRepository);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(studyRepository);
        verifyZeroInteractions(trackingOperationService);
        verifyZeroInteractions(eventTypeService);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 2 ancestry done");
        assertNotNull(message);
    }


    @Test
    public void testAssignStudyCurator() {

        // Stubbing
        when(curatorRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_1_CURATOR);
        when(eventTypeService.determineEventTypeFromCurator(LEVEL_1_CURATOR)).thenReturn(EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR);

        studyOperationsService.assignStudyCurator(STU1, ASSIGNEE, SECURE_USER);
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(2)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR);

    }

    @Test
    public void testUpdateHousekeepingWithStatusAndCuratorChange() {

        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING.getCurator())).thenReturn(EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR);
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING.getCurationStatus())).thenReturn(EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);

        // Test updating housekeeping where the status and curator has changed
        String message = studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING, STU1, SECURE_USER);


        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR);
        verify(mailService).sendEmailNotification(STU1, NEW_HOUSEKEEPING.getCurationStatus().getStatus());
        verify(housekeepingRepository, times(1)).save(NEW_HOUSEKEEPING);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);
        verify(studyRepository, times(3)).save(STU1);

        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(publishStudyCheckService);

        // Check housekeeping was saved
        assertThat(STU1.getHousekeeping()).extracting("notes", "ethnicityCheckedLevelOne")
                .contains("Some notes", true);
        assertThat(STU1.getHousekeeping()).extracting("studyAddedDate").isNotNull();
        assertThat(STU1.getHousekeeping().getCurator()).extracting("lastName").contains("Level 1 Curator");
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 1 curation done");
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingNoStatusChangeAndNoCuratorChange() {

        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_NO_STATUS_CHANGE.getCurator())).thenReturn(
                EventType.STUDY_CURATOR_ASSIGNMENT_UNASSIGNED);
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_NO_STATUS_CHANGE.getCurationStatus())).thenReturn(
                EventType.STUDY_STATUS_CHANGE_LEVEL_2_ANCESTRY_DONE);

        // Test updating housekeeping where the status has not changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_NO_STATUS_CHANGE, STU1, SECURE_USER);

        verify(housekeepingRepository, times(1)).save(NEW_HOUSEKEEPING_NO_STATUS_CHANGE);
        verify(studyRepository, times(1)).save(STU1);

        verifyZeroInteractions(mailService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        // Check housekeeping was saved
        assertThat(STU1.getHousekeeping()).extracting("notes", "ethnicityCheckedLevelOne")
                .contains("Some notes", true);
        assertThat(STU1.getHousekeeping()).extracting("studyAddedDate").isNotNull();
        assertThat(STU1.getHousekeeping().getCurator()).extracting("lastName").contains("Unassigned");
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 2 ancestry done");
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingWithStatusChangeToPublish() {

        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        // Stub behaviour
        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurator())).thenReturn(
                EventType.STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG);
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()))
                .thenReturn(EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(null);

        // Test updating housekeeping where the status has changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH, STU1, SECURE_USER);

        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(mailService).sendEmailNotification(STU1,
                                                  NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()
                                                          .getStatus());
        verify(housekeepingRepository, times(1)).save(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH);
        verify(studyRepository, times(3)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);

        // Check housekeeping was saved
        assertThat(STU1.getHousekeeping()).extracting("notes", "ethnicityCheckedLevelOne")
                .contains("Some notes", true);
        assertThat(STU1.getHousekeeping()).extracting("studyAddedDate").isNotNull();
        assertThat(STU1.getHousekeeping().getCurator()).extracting("lastName").contains("GWAS Catalog");
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Publish study");
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingWithStatusChangeToPublishyWithStudyThatDoesNotPassPublishCheck() {

        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS3);

        // Stub behaviour
        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurator())).thenReturn(
                EventType.STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG);
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()))
                .thenReturn(EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(Matchers.anyString());

        // Test updating housekeeping where the status has changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH, STU1, SECURE_USER);

        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(housekeepingRepository, times(1)).save(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH);
        verify(studyRepository, times(2)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1, SECURE_USER, EventType.STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG);

        verifyZeroInteractions(mailService);

        // Check housekeeping was saved
        assertThat(STU1.getHousekeeping()).extracting("notes", "ethnicityCheckedLevelOne")
                .contains("Some notes", true);
        assertThat(STU1.getHousekeeping()).extracting("studyAddedDate").isNotNull();
        assertThat(STU1.getHousekeeping().getCurator()).extracting("lastName")
                .contains("GWAS Catalog");
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 2 ancestry done");
        assertNotNull(message);
    }
}