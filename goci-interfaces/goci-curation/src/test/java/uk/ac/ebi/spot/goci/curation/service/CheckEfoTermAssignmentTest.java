package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by emma on 29/02/2016.
 *
 * @author emma
 *         <p>
 *         Test for CheckEfoTermAssignment.java
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckEfoTermAssignmentTest {

    private static final EfoTrait EFO1 =
            new EfoTraitBuilder().setId(987L)
                    .setTrait("asthma")
                    .setUri("http://www.ebi.ac.uk/efo/EFO_0000270")
                    .build();

    private static final EfoTrait EFO2 = new EfoTraitBuilder()
            .setId(988L)
            .setTrait("atrophic rhinitis")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0007159")
            .build();

    private static final EfoTrait EFO3 = new EfoTraitBuilder()
            .setId(989L)
            .setTrait("HeLa")
            .setUri("http://www.ebi.ac.uk/efo/EFO_0001185")
            .build();

    private static final Association ASS_WITH_EFO_TRAIT_1 =
            new AssociationBuilder().setId(801L)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2)).build();

    private static final Association ASS_WITH_EFO_TRAIT_2 =
            new AssociationBuilder().setId(801L)
                    .setEfoTraits(Collections.singletonList(EFO3)).build();

    private static final Association ASS_NO_EFO_TRAIT =
            new AssociationBuilder().setId(803L).build();

    private static final Study STUDY_EFO_TRAIT_ASSIGNED =
            new StudyBuilder().setId(802L)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2))
                    .build();

    private static final Study STUDY_NO_EFO_TRAIT =
            new StudyBuilder().setId(802L)
                    .build();

    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;

    @Before
    public void setUp() throws Exception {
        checkEfoTermAssignmentService = new CheckEfoTermAssignmentService();
    }

    @Test
    public void testCheckAssociationsEfoAssignment() {
        assertTrue(checkEfoTermAssignmentService.checkAssociationsEfoAssignment(Arrays.asList(ASS_WITH_EFO_TRAIT_1,
                                                                                              ASS_WITH_EFO_TRAIT_2)));
        assertFalse(checkEfoTermAssignmentService.checkAssociationsEfoAssignment(Arrays.asList(ASS_NO_EFO_TRAIT,
                                                                                               ASS_WITH_EFO_TRAIT_1)));
    }

    @Test
    public void testCheckAssociationEfoAssignment() {
        assertTrue(checkEfoTermAssignmentService.checkAssociationEfoAssignment(ASS_WITH_EFO_TRAIT_1));
        assertFalse(checkEfoTermAssignmentService.checkAssociationEfoAssignment(ASS_NO_EFO_TRAIT));
    }

    @Test
    public void testCheckStudyEfoAssignment() {
        assertTrue(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_EFO_TRAIT_ASSIGNED));
        assertFalse(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_NO_EFO_TRAIT));
    }
}