package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.service.EventTypeService;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.AssigneeBuilder;
import uk.ac.ebi.spot.goci.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StatusAssignmentBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.UnpublishReason;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
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
    private TrackingOperationService trackingOperationService;

    @Mock
    private AncestryRepository ancestryRepository;

    @Mock
    private EventTypeService eventTypeService;

    @Mock
    private HousekeepingOperationsService housekeepingOperationsService;

    // Class under test
    private StudyOperationsService studyOperationsService;

    // Build entity objects for use during testing
    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final CurationStatus LEVEL_01 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus PUBLISH =
            new CurationStatusBuilder().setId(805L).setStatus("Publish study").build();

    private static final CurationStatus LEVEL_02 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 curation done").build();

    private static final CurationStatus AWAITING_CURATION =
            new CurationStatusBuilder().setId(816L).setStatus("Awaiting Curation").build();

    private static final CurationStatus UNPUBLISH =
            new CurationStatusBuilder().setId(811L).setStatus("Unpublished from catalog").build();

    private static final Curator UNASSIGNED = new CuratorBuilder().setId(803L)
            .setLastName("Unassigned")
            .build();

    private static final Curator LEVEL_1_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Level 1 Curator")
            .build();

    private static final Curator PUBLISH_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("GWAS Catalog")
            .build();

    private static final Housekeeping NEW_HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(AWAITING_CURATION)
                    .setCurator(LEVEL_1_CURATOR)
                    .setStudyAddedDate(new Date())
                    .build();

    private static final Housekeeping LEVEL_01_HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(LEVEL_01)
                    .setCurator(LEVEL_1_CURATOR)
                    .setNotes("Some notes")
                    .setAncestryCheckedLevelOne(true)
                    .setStudySnpCheckedLevelOne(true)
                    .build();

    private static final Housekeeping NEW_HOUSEKEEPING_NO_STATUS_CHANGE =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(AWAITING_CURATION)
                    .setCurator(UNASSIGNED)
                    .setNotes("Some notes")
                    .setAncestryCheckedLevelOne(true)
                    .build();

    private static final Housekeeping NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(PUBLISH)
                    .setCurator(PUBLISH_CURATOR)
                    .setNotes("Some notes")
                    .setAncestryCheckedLevelOne(true)
                    .setStudyAddedDate(new Date())
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

    private static final Study NEW_STUDY = new StudyBuilder().setAuthor("Smith X")
            .setPubmedId("1001002")
            .setPublication("Nature")
            .setPublicationDate(new Date())
            .setTitle("Test")
            .build();

    private static Study STU1;

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
                                                            eventTypeService,
                                                            housekeepingOperationsService);
        // Create these objects before each test
        Housekeeping CURRENT_HOUSEKEEPING = new HousekeepingBuilder().setId(799L)
                .setCurationStatus(AWAITING_CURATION)
                .setCurator(UNASSIGNED)
                .build();
        STU1 = new StudyBuilder().setId(802L).setHousekeeping(CURRENT_HOUSEKEEPING).build();
    }


    @Test
    public void testCreateStudy() {

        // Stubbing
        when(housekeepingOperationsService.createHousekeeping()).thenReturn(NEW_HOUSEKEEPING);

        // Test creating a study
        Study study = studyOperationsService.createStudy(NEW_STUDY, SECURE_USER);
        verify(trackingOperationService, times(1)).create(NEW_STUDY, SECURE_USER);
        verify(studyRepository, times(1)).save(NEW_STUDY);

        assertThat(study).extracting("author", "title", "publication", "pubmedId")
                .contains("Smith X", "Test", "Nature", "1001002");
        assertThat(study.getPublicationDate()).isToday();
        assertThat(study.getHousekeeping().getStudyAddedDate()).isToday();
        assertThat(study.getHousekeeping().getCurationStatus()).extracting("status").contains("Awaiting Curation");
        assertThat(study.getHousekeeping().getCurator()).extracting("lastName").contains("Level 1 Curator");
    }

    @Test
    public void testAssignStudyStatusToLevelOneCurationDone() {

        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_01);
        when(eventTypeService.determineEventTypeFromStatus(LEVEL_01)).thenReturn("STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE");

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(mailService).sendEmailNotification(STU1, LEVEL_01.getStatus());
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(LEVEL_01);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE");
        verify(studyRepository, times(1)).save(STU1);

        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 1 curation done");
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusToLevelTwoCurationDone() {

        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_02);
        when(eventTypeService.determineEventTypeFromStatus(LEVEL_02)).thenReturn("STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE");

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(LEVEL_02);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE");
        verify(studyRepository, times(1)).save(STU1);

        verify(mailService).sendEmailNotification(STU1, LEVEL_02.getStatus());
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Level 2 curation done");
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusNoChange() {

        // No change to status
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(AWAITING_CURATION);
        when(eventTypeService.determineEventTypeFromStatus(AWAITING_CURATION)).thenReturn("STUDY_STATUS_CHANGE_AWAITING_CURATION");

        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);

        verifyZeroInteractions(housekeepingRepository);
        verifyZeroInteractions(studyRepository);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(trackingOperationService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(eventTypeService);

        // Assert there has been no change
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Awaiting Curation");
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
        when(eventTypeService.determineEventTypeFromStatus(PUBLISH)).thenReturn("STUDY_STATUS_CHANGE_PUBLISH_STUDY");

        String message = studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(mailService, times(1)).sendEmailNotification(STU1, PUBLISH.getStatus());
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(PUBLISH);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_PUBLISH_STUDY");
        verify(studyRepository, times(1)).save(STU1);


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
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn("ERROR");

        String message = studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);

        verifyZeroInteractions(mailService);
        verifyZeroInteractions(studyRepository);
        verifyZeroInteractions(trackingOperationService);
        verifyZeroInteractions(eventTypeService);

        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status").contains("Awaiting Curation");
        assertThat(message).isEqualTo("ERROR");
    }

    @Test
    public void testAssignStudyCurator() {

        // Stubbing
        when(curatorRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_1_CURATOR);
        when(eventTypeService.determineEventTypeFromCurator(LEVEL_1_CURATOR)).thenReturn("STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR");

        studyOperationsService.assignStudyCurator(STU1, ASSIGNEE, SECURE_USER);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR");
        verify(studyRepository, times(1)).save(STU1);

        assertThat(STU1.getHousekeeping().getCurator()).extracting("lastName").containsOnly("Level 1 Curator");

    }

    @Test
    public void testUpdateHousekeepingWithStatusAndCuratorChange() {

        when(eventTypeService.determineEventTypeFromCurator(LEVEL_01_HOUSEKEEPING.getCurator())).thenReturn("STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR");
        when(eventTypeService.determineEventTypeFromStatus(LEVEL_01_HOUSEKEEPING.getCurationStatus())).thenReturn(
                "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE");

        // Test updating housekeeping where the status and curator has changed
        String message = studyOperationsService.updateHousekeeping(LEVEL_01_HOUSEKEEPING, STU1, SECURE_USER);

        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_CURATOR_ASSIGNMENT_LEVEL_1_CURATOR");
        verify(mailService).sendEmailNotification(STU1, LEVEL_01_HOUSEKEEPING.getCurationStatus().getStatus());
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE");
        verify(studyRepository, times(2)).save(STU1);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(publishStudyCheckService);
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingNoStatusChangeAndNoCuratorChange() {

        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_NO_STATUS_CHANGE.getCurator())).thenReturn(
                "STUDY_CURATOR_ASSIGNMENT_UNASSIGNED");
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_NO_STATUS_CHANGE.getCurationStatus())).thenReturn(
                "STUDY_STATUS_CHANGE_AWAITING_CURATION");

        // Test updating housekeeping where the status has not changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_NO_STATUS_CHANGE, STU1, SECURE_USER);

        verifyZeroInteractions(mailService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);
        verifyZeroInteractions(trackingOperationService);
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingWithStatusChangeToPublish() {

        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        // Stub behaviour
        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurator())).thenReturn(
                "STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG");
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()))
                .thenReturn("STUDY_STATUS_CHANGE_PUBLISH_STUDY");
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(null);

        // Test updating housekeeping where the status has changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH, STU1, SECURE_USER);

        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG");
        verify(mailService).sendEmailNotification(STU1,
                                                  NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()
                                                          .getStatus());
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_PUBLISH_STUDY");
        verify(studyRepository, times(2)).save(STU1);
        assertNull(message);
    }

    @Test
    public void testUpdateHousekeepingWithStatusChangeToPublishyWithStudyThatDoesNotPassPublishCheck() {

        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS3);

        // Stub behaviour
        when(eventTypeService.determineEventTypeFromCurator(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurator())).thenReturn(
                "STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG");
        when(eventTypeService.determineEventTypeFromStatus(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH.getCurationStatus()))
                .thenReturn("STUDY_STATUS_CHANGE_PUBLISH_STUDY");
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn("ERROR");

        // Test updating housekeeping where the status has changed
        String message =
                studyOperationsService.updateHousekeeping(NEW_HOUSEKEEPING_STATUS_CHANGE_TO_PUBLISH, STU1, SECURE_USER);

        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_CURATOR_ASSIGNMENT_GWAS_CATALOG");
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verifyZeroInteractions(mailService);
        assertThat(message).isEqualTo("ERROR");
    }

    @Test
    public void testUnpublishStudy() {

        // Create clones of our study/housekeeping before the method runs
        Housekeeping housekeepingBeforeUnpublish =
                new HousekeepingBuilder().setId(799L)
                        .setCurationStatus(AWAITING_CURATION)
                        .setCurator(UNASSIGNED)
                        .build();
        Study beforeUnPublish = new StudyBuilder().setId(802L).setHousekeeping(housekeepingBeforeUnpublish).build();

        // Stubbing
        when(studyRepository.findOne(STU1.getId())).thenReturn(STU1);
        when(housekeepingRepository.findOne(STU1.getHousekeeping().getId())).thenReturn(STU1.getHousekeeping());
        when(curationStatusRepository.findByStatus("Unpublished from catalog")).thenReturn(UNPUBLISH);
        when(eventTypeService.determineEventTypeFromStatus(UNPUBLISH)).thenReturn("STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG");

        studyOperationsService.unpublishStudy(STU1.getId(), Matchers.any(UnpublishReason.class), SECURE_USER);

        verify(studyRepository, times(1)).findOne(STU1.getId());
        verify(housekeepingRepository, times(1)).findOne(STU1.getHousekeeping().getId());
        verify(curationStatusRepository, times(1)).findByStatus("Unpublished from catalog");
        verify(eventTypeService, times(1)).determineEventTypeFromStatus(STU1.getHousekeeping().getCurationStatus());
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          "STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG");
        verify(studyRepository, times(1)).save(STU1);
        verifyZeroInteractions(mailService);
        verifyZeroInteractions(publishStudyCheckService);
        verifyZeroInteractions(associationRepository);

        // Check housekeeping was saved
        assertThat(STU1).isEqualToIgnoringGivenFields(beforeUnPublish, "housekeeping");
        assertThat(STU1.getHousekeeping()).isEqualToIgnoringGivenFields(housekeepingBeforeUnpublish,
                                                                        "catalogUnpublishDate",
                                                                        "lastUpdateDate",
                                                                        "curationStatus");
        assertThat(STU1.getHousekeeping().getCurationStatus()).extracting("status")
                .contains("Unpublished from catalog");
        assertThat(STU1.getHousekeeping().getCatalogUnpublishDate()).isToday();
    }
}