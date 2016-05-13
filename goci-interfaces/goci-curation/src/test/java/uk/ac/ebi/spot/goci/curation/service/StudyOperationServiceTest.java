package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StatusAssignmentBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
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
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private StudyOperationsService studyOperationsService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final CurationStatus LEVEL_01 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus NEW_STATUS2 =
            new CurationStatusBuilder().setId(805L).setStatus("Publish study").build();

    private static final CurationStatus LEVEL_02 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 curation done").build();

    private static final CurationStatus CURRENT_STATUS1 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 ancestry done").build();

    private static final Curator CURATOR1 = new CuratorBuilder().setId(803L)
            .setEmail("gwas-dev@ebi.ac.uk")
            .setFirstName("test")
            .setLastName("Test")
            .setUserName("testing")
            .build();

    private static final Housekeeping HOUSEKEEPING1 =
            new HousekeepingBuilder().setId(799L).setCurationStatus(CURRENT_STATUS1).setCurator(CURATOR1).build();

    private static final Study STU1 =
            new StudyBuilder().setId(802L).setHousekeeping(HOUSEKEEPING1).build();

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
            new StatusAssignmentBuilder().setStatusId(4L).build();


    @Before
    public void setUpMock() {
        studyOperationsService = new StudyOperationsService(associationRepository,
                                                            mailService,
                                                            housekeepingRepository,
                                                            publishStudyCheckService,
                                                            studyRepository,
                                                            curatorRepository,
                                                            curationStatusRepository,
                                                            trackingOperationService);
    }

    @Test
    public void testSaveStudy() {
        // Test saving a study
        Study study = studyOperationsService.saveStudy(NEW_STUDY, SECURE_USER);
        verify(housekeepingRepository, times(1)).save(Matchers.any(Housekeeping.class));
        verify(studyRepository, times(1)).save(NEW_STUDY);
        verify(trackingOperationService, times(1)).create(NEW_STUDY, SECURE_USER);

        assertThat(study).extracting("author", "title", "publication", "pubmedId")
                .contains("Smith X", "Test", "Nature", "1001002");
        assertThat(study).extracting("publicationDate").isNotNull();
    }

    @Test
    public void testAssignStudyStatus() {
        // Change assignment from "Level 2 ancestry done" to "Level 2 curation done"
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_02);
        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(1)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE);
        assertEquals("Study status must be " + LEVEL_02.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     LEVEL_02);
        assertNull(message);
    }

    @Test
    public void testAssignStudyStatusToLevelOneCurationDone() {
        // Change assignment from "Level 2 ancestry done" to "Level 1 curation done"
        when(curationStatusRepository.findOne(Matchers.anyLong())).thenReturn(LEVEL_01);
        String message =
                studyOperationsService.assignStudyStatus(STU1, STATUS_ASSIGNMENT, SECURE_USER);
        verify(curationStatusRepository, times(1)).findOne(Matchers.anyLong());
        verify(mailService).sendEmailNotification(STU1, LEVEL_01.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(1)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);
        assertEquals("Study status must be " + LEVEL_01.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     LEVEL_01);
        assertNull(message);
    }

    // TODO ADD TEST FOR PUBLISHED STUDY
  /*

    @Test
    public void testUpdateStatusToLevelOneCurationDone() {

        // Test changing status to "Level 1 curation done"
        studyOperationsService.updateStatus(LEVEL_01, STU1, CURRENT_STATUS1,
                                            SECURE_USER);
        verify(mailService).sendEmailNotification(STU1, LEVEL_01.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(1)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE);

        assertEquals("Study status must be " + LEVEL_01.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     LEVEL_01);
    }

    @Test
    public void testUpdateStatusToPublishStudy() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        // Stub behaviour
        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(null);

        // Test changing status to "Publish study"
        studyOperationsService.updateStatus(NEW_STATUS2, STU1, CURRENT_STATUS1,
                                            SECURE_USER);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(mailService).sendEmailNotification(STU1, NEW_STATUS2.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());
        verify(studyRepository, times(1)).save(STU1);
        verify(trackingOperationService, times(1)).update(STU1,
                                                          SECURE_USER,
                                                          EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY);
        assertEquals("Study status must be " + NEW_STATUS2.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     NEW_STATUS2);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
    }

    @Test
    public void testUpdateStatusToPublishStudyWithStudyThatDoesNotPassPublishCheck() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS3);

        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);
        when(publishStudyCheckService.runChecks(STU1, associations)).thenReturn(
                "No EFO trait assigned and some SNP associations have not been approved for study");

        // Test changing status to "Publish study" where SNPs are unapproved
        studyOperationsService.updateStatus(NEW_STATUS2, STU1, CURRENT_STATUS1,
                                            SECURE_USER);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(publishStudyCheckService, times(1)).runChecks(STU1, associations);
        verify(mailService, never()).sendEmailNotification(STU1, NEW_STATUS2.getStatus());
        verify(housekeepingRepository, times(1)).save(STU1.getHousekeeping());

        verifyZeroInteractions(studyRepository);
        verifyZeroInteractions(trackingOperationService);

        assertEquals("Study status must be " + CURRENT_STATUS1.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     CURRENT_STATUS1); // check status was not changed
    }*/
}