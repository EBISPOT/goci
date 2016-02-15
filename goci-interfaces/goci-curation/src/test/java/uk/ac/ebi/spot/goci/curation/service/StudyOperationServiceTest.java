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
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    private StudyOperationsService studyOperationsService;

    private static final Association ASS1 =
            new AssociationBuilder().setId(800L)
                    .setSnpApproved(true).build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(801L)
                    .setSnpApproved(true).build();

    private static final Association ASS3 =
            new AssociationBuilder().setId(803L)
                    .setSnpApproved(false).build();

    private static final Curator CURATOR1 = new CuratorBuilder().setId(803L)
            .setEmail("gwas-dev@ebi.ac.uk")
            .setFirstName("test")
            .setLastName("Test")
            .setUserName("testing")
            .build();

    private static final CurationStatus NEW_STATUS1 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus NEW_STATUS2 =
            new CurationStatusBuilder().setId(805L).setStatus("Publish study").build();

    private static final CurationStatus NEW_STATUS3 =
            new CurationStatusBuilder().setId(806L).setStatus("Awaiting EFO assignment").build();

    private static final CurationStatus CURRENT_STATUS1 =
            new CurationStatusBuilder().setId(806L).setStatus("Level 2 ancestry done").build();

    private static final Housekeeping HOUSEKEEPING1 =
            new HousekeepingBuilder().setId(799L).setCurationStatus(CURRENT_STATUS1).setCurator(CURATOR1).build();

    private static final Study STU1 =
            new StudyBuilder().setId(802L).setHousekeeping(HOUSEKEEPING1).build();

    @Before
    public void setUpMock() {
        setStudyOperationsService(new StudyOperationsService(associationRepository,
                                                             mailService,
                                                             housekeepingRepository));

    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(associationRepository);
        assertNotNull(mailService);
        assertNotNull(housekeepingRepository);
    }

    @Test
    public void testUpdateStatus() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);

        // Test changing status
        getStudyOperationsService().updateStatus(NEW_STATUS3, STU1, CURRENT_STATUS1);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        assertEquals("Study status must be " + NEW_STATUS3.getStatus(),STU1.getHousekeeping().getCurationStatus(), NEW_STATUS3);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
    }


    @Test
    public void testUpdateStatusToLevelOneCurationDone() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);

        // Test changing status to "Level 1 curation done"
        getStudyOperationsService().updateStatus(NEW_STATUS1, STU1, CURRENT_STATUS1);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(mailService).sendEmailNotification(STU1, NEW_STATUS1.getStatus());
        assertEquals("Study status must be " + NEW_STATUS1.getStatus(),STU1.getHousekeeping().getCurationStatus(), NEW_STATUS1);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
    }

    @Test
    public void testUpdateStatusToPublishStudy() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS2);

        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);

        // Test changing status to "Publish study"
        getStudyOperationsService().updateStatus(NEW_STATUS2, STU1, CURRENT_STATUS1);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(mailService).sendEmailNotification(STU1, NEW_STATUS2.getStatus());
        assertEquals("Study status must be " + NEW_STATUS2.getStatus() ,STU1.getHousekeeping().getCurationStatus(), NEW_STATUS2);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);

    }

    @Test
    public void testUpdateStatusToPublishStudyWithUnapprovedSnps() {

        // Test interaction with association repository
        Collection<Association> associations = new ArrayList<>();
        associations.add(ASS1);
        associations.add(ASS3);

        when(associationRepository.findByStudyId(STU1.getId())).thenReturn(associations);

        // Test changing status to "Publish study" where SNPs are unapproved
        getStudyOperationsService().updateStatus(NEW_STATUS2, STU1, CURRENT_STATUS1);
        verify(associationRepository, times(1)).findByStudyId(STU1.getId());
        verify(mailService, never()).sendEmailNotification(STU1, NEW_STATUS2.getStatus());
        assertEquals("Study status must be " + CURRENT_STATUS1.getStatus(),
                     STU1.getHousekeeping().getCurationStatus(),
                     CURRENT_STATUS1); // check status was not changed
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING1);
    }

    @Test
    public void testStudyAssociationCheck() {
        Collection<Association> associations = Arrays.asList(ASS1, ASS2);
        assertEquals(0, getStudyOperationsService().studyAssociationCheck(associations));

        Collection<Association> associations1 = Arrays.asList(ASS1, ASS3);
        assertEquals(1, getStudyOperationsService().studyAssociationCheck(associations1));
    }

    // Class to test, getter and setters
    public StudyOperationsService getStudyOperationsService() {
        return studyOperationsService;
    }

    public void setStudyOperationsService(StudyOperationsService studyOperationsService) {
        this.studyOperationsService = studyOperationsService;
    }
}
