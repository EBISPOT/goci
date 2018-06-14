package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 08/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for /goci/curation/service/MappingDetailsService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class MappingDetailsServiceTest {

    @Mock
    private AssociationRepository associationRepository;

    private MappingDetailsService mappingDetailsService;

    private static final Association ASS1 =
            new AssociationBuilder().setId(100L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("automatic_mapping_process").build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(101L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("automatic_mapping_process").build();

    private static final Timestamp LASTDATEMAPPING = new Timestamp(new Date().getTime());


    private static final Association ASS3 =
            new AssociationBuilder().setId(103L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("Level 1 Curator").build();

    private static final Association ASS4 =
            new AssociationBuilder().setId(104L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("Level 2 Curator").build();

    private static final Association ASS_NO_MAPPING_01 =
            new AssociationBuilder().setId(105L).build();

    private static final Association ASS_NO_MAPPING_02 =
            new AssociationBuilder().setId(106L).build();

    private static final Study STU_NO_ASS =
            new StudyBuilder().setId(200L).build();

    private static final Study STU_ASS_NO_MAPPING =
            new StudyBuilder().setId(201L).build();

    private static final Study STU_AUTO_MAPPING =
            new StudyBuilder().setId(202L).build();

    private static final Study STU_CURATOR_MAPPING =
            new StudyBuilder().setId(202L).build();

    @Before
    public void setUpMock() {
        mappingDetailsService = new MappingDetailsService(associationRepository);
    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(associationRepository);
    }

    @Test
    public void testCreateMappingSummaryForStudyWithNoAssociations() {

        // Stubbing association repository
        // GOCI-2267
        Optional<Timestamp> returnLastDateMapping = Optional.ofNullable(null);
        when(associationRepository.findLastMappingDateByStudyId(STU_NO_ASS.getId())).thenReturn(returnLastDateMapping);

        MappingDetails mappingDetails = mappingDetailsService.createMappingSummary(STU_NO_ASS);
        verify(associationRepository, times(1)).findLastMappingDateByStudyId(STU_NO_ASS.getId());
        assertThat(mappingDetails).isInstanceOf(MappingDetails.class);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("mappingDate",
                                                               null);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("performer",
                                                               null);
    }

    @Test
    public void testCreateMappingSummaryForStudyWithAssociationsWithNoMapping() {

        // Stubbing association repository
        // GOCI-2267
        Optional<Timestamp> returnLastDateMapping = Optional.ofNullable(null);
        when(associationRepository.findLastMappingDateByStudyId(STU_ASS_NO_MAPPING.getId())).thenReturn(returnLastDateMapping);

        MappingDetails mappingDetails = mappingDetailsService.createMappingSummary(STU_ASS_NO_MAPPING);
        verify(associationRepository, times(1)).findLastMappingDateByStudyId(STU_ASS_NO_MAPPING.getId());
        assertThat(mappingDetails).isInstanceOf(MappingDetails.class);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("mappingDate",
                                                               null);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("performer",
                                                               null);
    }

    @Test
    public void testCreateMappingSummaryForStudyWithAssociationsWithCuratorMapping() {

        // Stubbing association repository
        // GOCI-2267
        Optional<Timestamp> returnLastDateMapping = Optional.ofNullable(null);
        when(associationRepository.findLastMappingDateByStudyId(STU_CURATOR_MAPPING.getId())).thenReturn(returnLastDateMapping);


        MappingDetails mappingDetails = mappingDetailsService.createMappingSummary(STU_CURATOR_MAPPING);
        verify(associationRepository, times(1)).findLastMappingDateByStudyId(STU_CURATOR_MAPPING.getId());

        assertThat(mappingDetails).isInstanceOf(MappingDetails.class);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("mappingDate",
                                                               null);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("performer",
                                                               null);
    }

    @Test
    public void testCreateMappingSummaryForStudyWithAssociationsWithAutomaticMapping() {

        // Stubbing association repository
        // GOCI-2267
        Optional<Timestamp> returnLastDateMapping = Optional.of((Timestamp) LASTDATEMAPPING);
        when(associationRepository.findLastMappingDateByStudyId(STU_AUTO_MAPPING.getId())).thenReturn(returnLastDateMapping);

        MappingDetails mappingDetails = mappingDetailsService.createMappingSummary(STU_AUTO_MAPPING);
        verify(associationRepository, times(1)).findLastMappingDateByStudyId(STU_AUTO_MAPPING.getId());

        assertThat(mappingDetails).isInstanceOf(MappingDetails.class);
        assertThat(mappingDetails.getMappingDate()).isInstanceOf(Date.class);
        assertThat(mappingDetails).hasFieldOrPropertyWithValue("performer",
                                                               "automatic_mapping_process");

    }
}