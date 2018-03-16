package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.*;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.service.StudyNoteService;
import uk.ac.ebi.spot.goci.service.StudyTrackingOperationServiceImpl;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 26/05/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyDuplicationServiceTest {

    @Mock
    private AncestryRepository ancestryRepository;

    @Mock
    private HousekeepingOperationsService housekeepingOperationsService;

    @Mock
    private StudyTrackingOperationServiceImpl studyTrackingOperationService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyNoteService studyNoteService;

    @Mock
    private StudyNoteOperationsService studyNoteOperationsService;

    private StudyDuplicationService studyDuplicationService;

    private static final DiseaseTrait DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(799L).setTrait("Asthma").build();

    private static final EfoTrait EFO1 =
            new EfoTraitBuilder().setId(987L)
                    .setTrait("asthma")
                    .setUri("http://www.ebi.ac.uk/efo/EFO_0000270")
                    .build();

    private static final Country CO1 =
            new CountryBuilder().setId(20L).setCountryName("Ireland").build();
    private static final Country CO2 =
            new CountryBuilder().setId(25L).setCountryName("U.K").build();

    private static final AncestralGroup AG1 =
            new AncestralGroupBuilder().setId(30L).setAncestralGroup("European").build();

    private static final Ancestry ETH1 = new AncestryBuilder().setNotes("ETH1 notes")
            .setId(40L)
            .setCountryOfOrigin(Collections.singleton(CO1))
            .setCountryOfRecruitment(Collections.singleton(CO1))
            .setDescription("ETH1 description")
            .setAncestralGroups(Collections.singleton(AG1))
            .setNumberOfIndividuals(100)
            .setType("initial")
            .build();

    private static final Ancestry ETH2 = new AncestryBuilder().setNotes("ETH2 notes")
            .setId(60L)
            .setCountryOfOrigin(Collections.singleton(CO2))
            .setCountryOfRecruitment(Collections.singleton(CO2))
            .setDescription("ETH2 description")
            .setAncestralGroups(Collections.singleton(AG1))
            .setNumberOfIndividuals(200)
            .setType("replication")
            .build();


    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(111L).setEmail("curator@test.com").setPasswordHash("738274$$").build();

    private static final Curator CURATOR = new CuratorBuilder().setId(803L)
            .setLastName("Unassigned")
            .build();

    private static final CurationStatus STATUS =
            new CurationStatusBuilder().setId(804L).setStatus("Awaiting Curation").build();

    // THOR
    private static final Author AUTHOR = new AuthorBuilder().setFullname("MacTest T")
            .setOrcid("0000-0002-0002-003").build();

    // THOR
    private static final Publication PUBLICATION = new PublicationBuilder().setPublication("Nature")
            .setPubmedId("1234569")
            .setPublication("Testiing is Awesome")
            .setTitle("I like to test")
            .setPublicationDate(new Date())
            .setFirstAuthor(AUTHOR)
            .build();


    private static final Housekeeping NEW_HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(STATUS)
                    .setCurator(CURATOR)
                    .setStudyAddedDate(new Date())
                    .setNotes("")
                    .build();

    private static final Study STUDY_TO_DUPLICATE = new StudyBuilder().setId(112L)
            .setPublication(PUBLICATION)
            .setDiseaseTrait(DISEASE_TRAIT)
            .setHousekeeping(NEW_HOUSEKEEPING)
            .setEfoTraits(Collections.singletonList(EFO1))
            .setStudyDesignComment("comment")
            .setInitialSampleSize("initial")
            .setReplicateSampleSize("rep")
            .setAncestries(Arrays.asList(ETH1, ETH2))
            .build();

    @Before
    public void setUp() throws Exception {
        studyDuplicationService = new StudyDuplicationService(ancestryRepository,
                housekeepingOperationsService,
                studyTrackingOperationService,
                studyRepository,
                studyNoteOperationsService,
                studyNoteService);
    }

    @Test
    public void duplicateStudy() throws Exception {

        // Stubbing
        when(housekeepingOperationsService.createHousekeeping()).thenReturn(NEW_HOUSEKEEPING);
        when(ancestryRepository.findByStudyId(STUDY_TO_DUPLICATE.getId())).thenReturn(Arrays.asList(ETH1, ETH2));

        Study duplicateStudy = studyDuplicationService.duplicateStudy(STUDY_TO_DUPLICATE, "new tag",SECURE_USER);

        // Verification
        verify(studyTrackingOperationService, times(1)).update(STUDY_TO_DUPLICATE,
                SECURE_USER,
                "STUDY_DUPLICATION");
        verify(studyRepository, times(1)).save(STUDY_TO_DUPLICATE);
        verify(studyTrackingOperationService, times(1)).create(duplicateStudy, SECURE_USER);
        verify(housekeepingOperationsService, times(1)).createHousekeeping();
        verify(ancestryRepository, times(1)).findByStudyId(STUDY_TO_DUPLICATE.getId());
        verify(ancestryRepository, times(2)).save(Matchers.any(Ancestry.class));
        verify(housekeepingOperationsService, times(1)).saveHousekeeping(duplicateStudy,
                duplicateStudy.getHousekeeping());


        // Assertions;
        assertThat(duplicateStudy).isEqualToIgnoringGivenFields(STUDY_TO_DUPLICATE,
                "housekeeping",
                "ancestries",
                "id",
                "publication_id",
                "notes");
        assertEquals(duplicateStudy.getNotes().size(), 2);

        // THOR
        // assertThat(duplicateStudy.getAuthor()).isEqualTo(STUDY_TO_DUPLICATE.getAuthor().concat(" DUP"));

        assertThat(duplicateStudy.getId()).isNotEqualTo(STUDY_TO_DUPLICATE.getId());
        assertThat(duplicateStudy.getHousekeeping().getStudyAddedDate()).isToday();

        /// Check ancestry
        assertThat(duplicateStudy.getAncestries()).extracting("id", "numberOfIndividuals",
//                                                              "ancestralGroup",
                "description",
//                                                               "countryOfOrigin",
//                                                               "countryOfRecruitment",
                "type")
                .contains(tuple(null, 100,
//                                "European",
                        "ETH1 description",
//                                "Ireland", "Ireland",
                        "initial"),
                        tuple(null, 200,
//                                "European",
                                "ETH2 description",
//                                "U.K.", "U.K.",
                                "replication"));
    }
}