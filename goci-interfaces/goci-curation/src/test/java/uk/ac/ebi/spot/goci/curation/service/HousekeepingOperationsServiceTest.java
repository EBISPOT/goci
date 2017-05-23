package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
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

    private static final CurationStatus LEVEL_01 =
            new CurationStatusBuilder().setId(804L).setStatus("Level 1 curation done").build();

    private static final CurationStatus AWAITING_CURATION =
            new CurationStatusBuilder().setId(816L).setStatus("Awaiting Curation").build();

    private static final Curator UNASSIGNED = new CuratorBuilder().setId(803L)
            .setLastName("Unassigned")
            .build();

    private static final Curator LEVEL_1_CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Level 1 Curator")
            .build();

    private static final Housekeeping CURRENT_HOUSEKEEPING =
            new HousekeepingBuilder().setId(799L)
                    .setCurator(UNASSIGNED)
                    .setCurationStatus(AWAITING_CURATION)
                    .build();

    private static final Housekeeping NEW_HOUSEKEEPING =
            new HousekeepingBuilder().setId(799L)
                    .setNotes("Testing saving")
                    .setAncestryCheckedLevelOne(true)
                    .setStudySnpCheckedLevelOne(true)
                    .setCurationStatus(LEVEL_01)
                    .setCurator(LEVEL_1_CURATOR)
                    .build();

    private static final Study STU1 = new StudyBuilder().setId(802L).setHousekeeping(CURRENT_HOUSEKEEPING).build();

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
        when(curationStatusRepository.findByStatus("Awaiting Curation")).thenReturn(AWAITING_CURATION);
        when(curatorRepository.findByLastName("Level 1 Curator")).thenReturn(LEVEL_1_CURATOR);

        Housekeeping housekeeping = housekeepingOperationsService.createHousekeeping();
        verify(curationStatusRepository, times(1)).findByStatus("Awaiting Curation");
        verify(curatorRepository, times(1)).findByLastName("Level 1 Curator");
        verify(housekeepingRepository, times(1)).save(Matchers.any(Housekeeping.class));
        verifyZeroInteractions(studyRepository);

        // Assertions
        assertThat(housekeeping).extracting(curator -> curator.getCurator().getLastName()).contains("Level 1 Curator");
        assertThat(housekeeping).extracting(curationStatus -> curationStatus.getCurationStatus().getStatus()).contains("Awaiting Curation");
        assertThat(housekeeping.getStudyAddedDate()).isToday();
    }

    @Test
    public void saveHousekeeping() throws Exception {
        housekeepingOperationsService.saveHousekeeping(STU1, NEW_HOUSEKEEPING);
        verify(housekeepingRepository, times(1)).save(NEW_HOUSEKEEPING);
        verify(studyRepository, times(1)).save(STU1);

        verifyZeroInteractions(curationStatusRepository);
        verifyZeroInteractions(curatorRepository);
        assertThat(STU1.getHousekeeping()).isEqualToComparingFieldByField(NEW_HOUSEKEEPING);
    }
}