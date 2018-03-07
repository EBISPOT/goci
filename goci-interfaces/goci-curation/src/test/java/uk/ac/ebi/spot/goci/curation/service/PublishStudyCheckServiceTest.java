package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.*;
import uk.ac.ebi.spot.goci.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 26/02/2016.
 *
 * @author emma
 *         <p>
 *         Test of PublishStudyCheckService.java
 */
@RunWith(MockitoJUnitRunner.class)
public class PublishStudyCheckServiceTest {

    private PublishStudyCheckService publishStudyCheckService;

    @Mock
    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;


    // THOR
    private static final Author AUTHOR = new AuthorBuilder().setFullname("MacTest T")
            .setOrcid("0000-0002-0002-003").build();


    // THOR
    private static final Publication PUBLICATION = new PublicationBuilder().setPublication("Nature")
            .setPubmedId("1234569")
            .setPublication("Testing is Awesome")
            .setTitle("Pubmed TEST")
            .setPublicationDate(new Date())
            .setFirstAuthor(AUTHOR)
            .build();

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

    private static final Country COR =
            new CountryBuilder().setId(601L)
                    .setCountryName("Germany")
                    .build();

    private static final Ancestry AN_WITH_COR =
            new AncestryBuilder().setId(605L)
                    .setCountryOfRecruitment(Collections.singletonList(COR))
                    .build();

    private static final Ancestry AN_NO_COR =
            new AncestryBuilder().setId(608L)
                    .build();

    private static final Association ASS_APPROVED =
            new AssociationBuilder().setId(801L)
                    .setSnpApproved(true).build();

    private static final Association ASS_NOT_APPROVED =
            new AssociationBuilder().setId(803L)
                    .setSnpApproved(false).build();

    private static final GenotypingTechnology GT_GENOMWIDE_ARRAY =
            new GenotypingTechnologyBuilder().setId(900L)
                    .setGenotypingTechnology("Genome-wide genotyping array").build();

    private static final GenotypingTechnology GT_TARGETED_ARRAY =
            new GenotypingTechnologyBuilder().setId(905L)
                    .setGenotypingTechnology("Targeted genotyping array").build();


    private static final Study STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR =
            new StudyBuilder().setId(802L)
                    .setPublication(PUBLICATION)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2))
                    .setAssociations(Collections.singletonList(ASS_APPROVED))
                    .setAncestries(Collections.singleton(AN_WITH_COR))
                    .setGenotypingTechnologies(Collections.singleton(GT_GENOMWIDE_ARRAY))
                    .build();

    private static final Study STUDY_NO_EFO_TRAIT =
            new StudyBuilder().setId(802L)
                    .setPublication(PUBLICATION)
                    .setAssociations(Collections.singletonList(ASS_NOT_APPROVED))
                    .setAncestries(Collections.singleton(AN_NO_COR))
                    .setGenotypingTechnologies(Collections.singleton(GT_GENOMWIDE_ARRAY))
                    .build();

    private static final Study STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR_TARGETED_ARRAY =
            new StudyBuilder().setId(802L)
                    .setPublication(PUBLICATION)
                    .setEfoTraits(Arrays.asList(EFO1, EFO2))
                    .setAssociations(Collections.singletonList(ASS_APPROVED))
                    .setAncestries(Collections.singleton(AN_WITH_COR))
                    .setGenotypingTechnologies(Collections.singleton(GT_TARGETED_ARRAY))
                    .build();

    @Before
    public void setUp() throws Exception {
        publishStudyCheckService = new PublishStudyCheckService(checkEfoTermAssignmentService);
    }

    @Test
    public void testMocks() {
        // Test mock creation
        assertNotNull(checkEfoTermAssignmentService);
    }

    @Test
    public void testStudyWithEfoTraitsAndApprovedAssociation() throws Exception {

        when(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR)).thenReturn(true);

        publishStudyCheckService.runChecks(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR,
                                           Collections.singletonList(ASS_APPROVED));
        verify(checkEfoTermAssignmentService, times(1)).checkStudyEfoAssignment(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR);
        assertTrue(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR));
        assertNull(publishStudyCheckService.runChecks(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR,
                                                      Collections.singletonList(ASS_APPROVED)));
    }

    @Test
    public void testStudyWithoutEfoTraitsAndAssociationNotApproved() {
        when(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_NO_EFO_TRAIT)).thenReturn(false);

        publishStudyCheckService.runChecks(STUDY_NO_EFO_TRAIT,
                                           Collections.singletonList(ASS_NOT_APPROVED));
        verify(checkEfoTermAssignmentService, times(1)).checkStudyEfoAssignment(STUDY_NO_EFO_TRAIT);
        assertFalse(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_NO_EFO_TRAIT));
        assertNotNull(publishStudyCheckService.runChecks(STUDY_NO_EFO_TRAIT,
                                                         Collections.singletonList(ASS_APPROVED)));
        assertThat(publishStudyCheckService.runChecks(STUDY_NO_EFO_TRAIT,
                                                      Collections.singletonList(ASS_APPROVED))
                           .contains("some SNP associations have not been approved; no EFO trait assigned; "));

    }

    @Test
    public void testStudyWithTargetedArray() throws Exception {
        when(checkEfoTermAssignmentService.checkStudyEfoAssignment(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR)).thenReturn(true);

        publishStudyCheckService.runChecks(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR_TARGETED_ARRAY,
                                           Collections.singletonList(ASS_APPROVED));

        assertNotNull(publishStudyCheckService.runChecks(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR_TARGETED_ARRAY,
                                                         Collections.singletonList(ASS_APPROVED)));

        assertThat(publishStudyCheckService.runChecks(STUDY_EFO_TRAIT_ASSIGNED_ASS_APPROVED_AN_WITH_COR_TARGETED_ARRAY,
                                                      Collections.singletonList(ASS_APPROVED))
                           .contains("is a targeted array study, other non-genome-wide or sequencing study and should not be published."));

    }
}
