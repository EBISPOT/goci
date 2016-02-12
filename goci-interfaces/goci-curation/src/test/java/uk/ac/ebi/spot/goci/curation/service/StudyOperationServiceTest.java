package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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

    private static final Study STU1 =
            new StudyBuilder().setId(801L).build();

    @Before
    public void setUpMock() {
        setStudyOperationsService(new StudyOperationsService(associationRepository,
                                                             mailService,
                                                             housekeepingRepository));

    }


    @Test
    public void testUpdateStatus() {

        verify(associationRepository, atLeastOnce()).findByStudyId(STU1.getId());
    }

    @Test
    public void testStudyAssociationCheck() {
        Collection<Association> associations = Arrays.asList(ASS1, ASS2);
        assertEquals(0, getStudyOperationsService().studyAssociationCheck(associations));
    }


    // Class to test, getter and setters
    public StudyOperationsService getStudyOperationsService() {
        return studyOperationsService;
    }

    public void setStudyOperationsService(StudyOperationsService studyOperationsService) {
        this.studyOperationsService = studyOperationsService;
    }
}
