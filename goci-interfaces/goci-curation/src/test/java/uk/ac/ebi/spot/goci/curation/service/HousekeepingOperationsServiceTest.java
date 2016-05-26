package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.curation.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 26/05/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class HousekeepingOperationsServiceTest {
    @Mock
    private HousekeepingRepository housekeepingRepository;

    @Mock
    private CuratorRepository curatorRepository;

    @Mock
    private CurationStatusRepository curationStatusRepository;

    @Mock
    private StudyRepository studyRepository;

    private HousekeepingOperationsService housekeepingOperationsService;

    private static final Housekeeping HOUSEKEEPING =
            new HousekeepingBuilder().setId(799L).build();

    private static final Study STU1 = new StudyBuilder().setId(802L).setHousekeeping(HOUSEKEEPING).build();

    private static final Curator CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Level 1 Curator")
            .build();

    private static final CurationStatus CURATION_STATUS =
            new CurationStatusBuilder().setId(804L).setStatus("Awaiting Curation").build();

    @Before
    public void setUp() throws Exception {
        housekeepingOperationsService = new HousekeepingOperationsService(housekeepingRepository,
                                                                          curatorRepository,
                                                                          curationStatusRepository,
                                                                          studyRepository);
    }

    @Test
    public void createHousekeeping() throws Exception {

        // Stubbing
        when(curationStatusRepository.findByStatus("Awaiting Curation")).thenReturn(CURATION_STATUS);
        when(curatorRepository.findByLastName("Level 1 Curator")).thenReturn(CURATOR);

        Housekeeping housekeeping = housekeepingOperationsService.createHousekeeping();
        verify(curationStatusRepository, times(1)).findByStatus("Awaiting Curation");
        verify(curatorRepository, times(1)).findByLastName("Level 1 Curator");
        verify(housekeepingRepository, times(1)).save(Matchers.any(Housekeeping.class));
        verifyZeroInteractions(studyRepository);

        // Assertions
        assertThat(housekeeping.getCurator()).extracting("lastName").contains("Level 1 Curator");
        assertThat(housekeeping.getCurationStatus()).extracting("status").contains("Awaiting Curation");
        assertThat(housekeeping.getStudyAddedDate()).isToday();
    }

    @Test
    public void saveHousekeeping() throws Exception {
        housekeepingOperationsService.saveHousekeeping(STU1, HOUSEKEEPING);
        verify(housekeepingRepository, times(1)).save(HOUSEKEEPING);
        verify(studyRepository, times(1)).save(STU1);

        verifyZeroInteractions(curationStatusRepository);
        verifyZeroInteractions(curatorRepository);
    }
}