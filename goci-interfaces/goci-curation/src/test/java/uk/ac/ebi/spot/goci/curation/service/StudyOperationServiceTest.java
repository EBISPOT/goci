package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;

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


    @Before
    public void setUpMock() {
        setStudyOperationsService(new StudyOperationsService(associationRepository,
                                                             mailService,
                                                             housekeepingRepository));

    }


    // Class to test, getter and setters
    public StudyOperationsService getStudyOperationsService() {
        return studyOperationsService;
    }

    public void setStudyOperationsService(StudyOperationsService studyOperationsService) {
        this.studyOperationsService = studyOperationsService;
    }
}
