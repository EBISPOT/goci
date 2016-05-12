package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
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

    private StudyOperationsService studyOperationsService;

    private static final SecureUser SECURE_USER = new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final CurationStatus NEW_STATUS1 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus NEW_STATUS2 =
            new CurationStatusBuilder().setId(805L).setStatus("Publish study").build();

    private static final CurationStatus NEW_STATUS3 =
            new CurationStatusBuilder().setId(806L).setStatus("Awaiting EFO assignment").build();

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

    private static final Association ASS1 =
            new AssociationBuilder().setId(800L)
                    .setSnpApproved(true).build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(801L)
                    .setSnpApproved(true).build();

    private static final Association ASS3 =
            new AssociationBuilder().setId(803L)
                    .setSnpApproved(false).build();

    @Before
    public void setUpMock() {
        studyOperationsService = new StudyOperationsService(associationRepository,
                                                            mailService,
                                                            housekeepingRepository,
                                                            publishStudyCheckService,
                                                            studyRepository,
                                                            curatorRepository,
                                                            curationStatusRepository,
                                                            eventOperationsService);
    }

    @Test
    public void testUpdateStatus() {
        // Test changing status
        studyOperationsService.updateStatus(NEW_STATUS3, STU1, CURRENT_STATUS1,
                                            SECURE_USER);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
        assertEquals("Study status must be " + NEW_STATUS3.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     NEW_STATUS3);
    }

    @Test
    public void testUpdateStatusToLevelOneCurationDone() {

        // Test changing status to "Level 1 curation done"
        studyOperationsService.updateStatus(NEW_STATUS1, STU1, CURRENT_STATUS1,
                                            SECURE_USER);
        verify(mailService).sendEmailNotification(STU1, NEW_STATUS1.getStatus());
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
        assertEquals("Study status must be " + NEW_STATUS1.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     NEW_STATUS1);
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
        assertEquals("Study status must be " + CURRENT_STATUS1.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     CURRENT_STATUS1); // check status was not changed
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
    }
}
