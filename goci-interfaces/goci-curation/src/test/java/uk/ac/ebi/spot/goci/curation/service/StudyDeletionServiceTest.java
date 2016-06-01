package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.EthnicityBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.service.tracking.StudyTrackingOperationServiceImpl;
import uk.ac.ebi.spot.goci.model.DeletedStudy;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DeletedStudyRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
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
    private EthnicityRepository ethnicityRepository;

    @Mock
    private StudyTrackingOperationServiceImpl trackingOperationService;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private DeletedStudyRepository deletedStudyRepository;

    private StudyDeletionService studyDeletionService;

    private static final Ethnicity ETH1 = new EthnicityBuilder().setNotes("ETH1 notes")
            .setId(40L)
            .setCountryOfOrigin("Ireland")
            .setCountryOfRecruitment("Ireland")
            .setDescription("ETH1 description")
            .setEthnicGroup("European")
            .setNumberOfIndividuals(100)
            .setType("initial")
            .build();

    private static final Ethnicity ETH2 = new EthnicityBuilder().setNotes("ETH2 notes")
            .setId(60L)
            .setCountryOfOrigin("U.K.")
            .setCountryOfRecruitment("U.K.")
            .setDescription("ETH2 description")
            .setEthnicGroup("European")
            .setNumberOfIndividuals(200)
            .setType("replication")
            .build();

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Study STUDY = new StudyBuilder().setId(112L)
            .setAuthor("MacTest T")
            .setPubmedId("1234569")
            .setPublication("Testiing is Awesome")
            .setTitle("I like to test")
            .setPublicationDate(new Date())
            .setStudyDesignComment("comment")
            .setInitialSampleSize("initial")
            .setReplicateSampleSize("rep")
            .setEthnicities(Arrays.asList(ETH1, ETH2)).build();

    @Before
    public void setUp() throws Exception {
        studyDeletionService = new StudyDeletionService(ethnicityRepository,
                                                        trackingOperationService,
                                                        studyRepository,
                                                        deletedStudyRepository);
    }

    @Test
    public void deleteStudy() throws Exception {
        when(ethnicityRepository.findByStudyId(STUDY.getId())).thenReturn(Arrays.asList(ETH1, ETH2));
        studyDeletionService.deleteStudy(STUDY, SECURE_USER);
        verify(ethnicityRepository, times(1)).delete(ETH1);
        verify(ethnicityRepository, times(1)).delete(ETH2);
        verify(trackingOperationService, times(1)).delete(STUDY, SECURE_USER);
        verify(studyRepository, times(1)).delete(STUDY);
        verify(deletedStudyRepository, times(1)).save(Matchers.any(DeletedStudy.class));
    }
}