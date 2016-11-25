package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.service.StudyTrackingOperationServiceImpl;
import uk.ac.ebi.spot.goci.builder.CurationStatusBuilder;
import uk.ac.ebi.spot.goci.builder.CuratorBuilder;
import uk.ac.ebi.spot.goci.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.builder.AncestryBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
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

    private StudyDuplicationService studyDuplicationService;

    private static final DiseaseTrait DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(799L).setTrait("Asthma").build();

    private static final EfoTrait EFO1 =
            new EfoTraitBuilder().setId(987L)
                    .setTrait("asthma")
                    .setUri("http://www.ebi.ac.uk/efo/EFO_0000270")
                    .build();

    private static final Ancestry ETH1 = new AncestryBuilder().setNotes("ETH1 notes")
            .setId(40L)
            .setCountryOfOrigin("Ireland")
            .setCountryOfRecruitment("Ireland")
            .setDescription("ETH1 description")
            .setAncestralGroup("European")
            .setNumberOfIndividuals(100)
            .setType("initial")
            .build();

    private static final Ancestry ETH2 = new AncestryBuilder().setNotes("ETH2 notes")
            .setId(60L)
            .setCountryOfOrigin("U.K.")
            .setCountryOfRecruitment("U.K.")
            .setDescription("ETH2 description")
            .setAncestralGroup("European")
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

    private static final Study STUDY_TO_DUPLICATE = new StudyBuilder().setId(112L)
            .setAuthor("MacTest T")
            .setPubmedId("1234569")
            .setPublication("Testiing is Awesome")
            .setTitle("I like to test")
            .setPublicationDate(new Date())
            .setDiseaseTrait(DISEASE_TRAIT)
            .setEfoTraits(Collections.singletonList(EFO1))
            .setStudyDesignComment("comment")
            .setInitialSampleSize("initial")
            .setReplicateSampleSize("rep")
            .setAncestries(Arrays.asList(ETH1, ETH2))
            .build();

    private static final Housekeeping NEW_HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCurationStatus(STATUS)
                    .setCurator(CURATOR)
                    .setStudyAddedDate(new Date())
                    .build();

    @Before
    public void setUp() throws Exception {
        studyDuplicationService = new StudyDuplicationService(ancestryRepository,
                                                              housekeepingOperationsService,
                                                              studyTrackingOperationService,
                                                              studyRepository);
    }

    @Test
    public void duplicateStudy() throws Exception {

        // Stubbing
        when(housekeepingOperationsService.createHousekeeping()).thenReturn(NEW_HOUSEKEEPING);
        when(ancestryRepository.findByStudyId(STUDY_TO_DUPLICATE.getId())).thenReturn(Arrays.asList(ETH1, ETH2));

        Study duplicateStudy = studyDuplicationService.duplicateStudy(STUDY_TO_DUPLICATE, SECURE_USER);

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
                                                                "author");
        assertThat(duplicateStudy.getHousekeeping().getNotes()).isEqualToIgnoringCase(
                "Duplicate of study: MacTest T, PMID: 1234569");
        assertThat(duplicateStudy.getAuthor()).isEqualTo(STUDY_TO_DUPLICATE.getAuthor().concat(" DUP"));
        assertThat(duplicateStudy.getId()).isNotEqualTo(STUDY_TO_DUPLICATE.getId());
        assertThat(duplicateStudy.getHousekeeping().getStudyAddedDate()).isToday();

        /// Check ancestry
        assertThat(duplicateStudy.getAncestries()).extracting("id", "numberOfIndividuals", "ancestralGroup",
                                                               "description",
                                                               "countryOfOrigin",
                                                               "countryOfRecruitment", "type")
                .contains(tuple(null, 100, "European", "ETH1 description", "Ireland", "Ireland", "initial"),
                          tuple(null, 200, "European", "ETH2 description", "U.K.", "U.K.", "replication"));
    }
}