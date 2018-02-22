package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.*;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.DeletedStudyRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.StudyService;
import uk.ac.ebi.spot.goci.service.StudyTrackingOperationServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 31/05/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyDeletionServiceTest {

    @Mock
    private AncestryRepository ancestryRepository;

    @Mock
    private StudyTrackingOperationServiceImpl trackingOperationService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private DeletedStudyRepository deletedStudyRepository;

    @Mock
    private StudyService studyService;

    @Mock
    private PublicationOperationsService publicationOperationsService;

    private StudyDeletionService studyDeletionService;

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
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();


    // THOR
    private static final Author AUTHOR = new AuthorBuilder().setFullname("MacTest T")
            .setOrcid("0000-0002-0002-003").build();

    private static final Author AUTHOR_2 = new AuthorBuilder().setFullname("John Doe").build();

    private static final Author AUTHOR_3 = new AuthorBuilder().setFullname("Joanne Doe").build();

    // THOR
    private static final Publication PUBLICATION = new PublicationBuilder().setPublication("Nature")
            .setPubmedId("1234569")
            .setPublication("Testiing is Awesome")
            .setTitle("I like to test")
            .setPublicationDate(new Date())
            .setFirstAuthor(AUTHOR)
            .build();

    // THOR
    private static final PublicationAuthors PUBLICATION_AUTHORS = new PublicationAuthorsBuilder().setAuthor(AUTHOR)
            .setPublication(PUBLICATION)
            .setSort(1)
            .build();


    private static final PublicationAuthors PUBLICATION_AUTHORS_2 = new PublicationAuthorsBuilder().setAuthor(AUTHOR_2)
            .setPublication(PUBLICATION)
            .setSort(2)
            .build();

    private static final PublicationAuthors PUBLICATION_AUTHORS_3 = new PublicationAuthorsBuilder().setAuthor(AUTHOR_3)
            .setPublication(PUBLICATION)
            .setSort(3)
            .build();


    private static final Study STUDY = new StudyBuilder().setId(112L)
            .setPublication(PUBLICATION)
            .setStudyDesignComment("comment")
            .setInitialSampleSize("initial")
            .setReplicateSampleSize("rep")
            .setAncestries(Arrays.asList(ETH1, ETH2)).build();

    @Before
    public void setUp() throws Exception {
        studyDeletionService = new StudyDeletionService(ancestryRepository,
                                                        trackingOperationService,
                                                        studyRepository,
                                                        deletedStudyRepository,
                                                        studyService,
                                                        publicationOperationsService
                                                        );
    }

    @Test
    public void deleteStudy() throws Exception {
        when(ancestryRepository.findByStudyId(STUDY.getId())).thenReturn(Arrays.asList(ETH1, ETH2));
        studyService.deleteRelatedInfoByStudy(STUDY);
        studyDeletionService.deleteStudy(STUDY, SECURE_USER);
        verify(ancestryRepository, times(1)).delete(ETH1);
        verify(ancestryRepository, times(1)).delete(ETH2);
        verify(trackingOperationService, times(1)).delete(STUDY, SECURE_USER);
        verify(studyRepository, times(1)).delete(STUDY);
        verify(deletedStudyRepository, times(1)).save(Matchers.any(DeletedStudy.class));
    }
}