
package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.service.CurrentUserDetailsService;
import uk.ac.ebi.spot.goci.curation.service.MappingDetailsService;
import uk.ac.ebi.spot.goci.curation.service.StudyDeletionService;
import uk.ac.ebi.spot.goci.curation.service.StudyDuplicationService;
import uk.ac.ebi.spot.goci.curation.service.StudyEventsViewService;
import uk.ac.ebi.spot.goci.curation.service.StudyFileService;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.curation.service.StudyUpdateService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.PlatformRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.UnpublishReasonRepository;
import uk.ac.ebi.spot.goci.service.DefaultPubMedSearchService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * Created by emma on 12/02/2016.
 *
 * @author emma
 *         <p>
 *         Study controller unit test
 */

@RunWith(MockitoJUnitRunner.class)
public class StudyControllerTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private HousekeepingRepository housekeepingRepository;

    @Mock
    private DiseaseTraitRepository diseaseTraitRepository;

    @Mock
    private EfoTraitRepository efoTraitRepository;

    @Mock
    private CuratorRepository curatorRepository;

    @Mock
    private CurationStatusRepository curationStatusRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private AssociationRepository associationRepository;

    @Mock
    private AncestryRepository ancestryRepository;

    @Mock
    private UnpublishReasonRepository unpublishReasonRepository;

    @Mock
    private DefaultPubMedSearchService defaultPubMedSearchService;

    @Mock
    private StudyOperationsService studyOperationsService;

    @Mock
    private MappingDetailsService mappingDetailsService;

    @Mock
    private CurrentUserDetailsService currentUserDetailsService;

    @Mock
    private StudyFileService studyFileService;

    @Mock
    private StudyDuplicationService studyDuplicationService;

    @Mock
    private StudyDeletionService studyDeletionService;

    @Mock
    private StudyEventsViewService eventsViewService;

    @Mock
    private StudyUpdateService studyUpdateService;

    private MockMvc mockMvc;

    private static final Housekeeping HOUSEKEEPING_PUBLISHED =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setCatalogPublishDate(new Date())
                    .build();

    private static final Study PUBLISHED_STUDY =
            new StudyBuilder().setId(1234L).setPublicationDate(new Date()).setHousekeeping(
                    HOUSEKEEPING_PUBLISHED).build();

    private static final Association ASSOCIATION_01 =
            new AssociationBuilder().setId(100L)
                    .setStudy(PUBLISHED_STUDY)
                    .build();

    private static final Housekeeping HOUSEKEEPING_UNPUBLISHED =
            new HousekeepingBuilder()
                    .setId(699L)
                    .build();

    private static final Study UNPUBLISHED_STUDY =
            new StudyBuilder().setId(1235L).setHousekeeping(HOUSEKEEPING_UNPUBLISHED).build();

    private static final Association ASSOCIATION_02 =
            new AssociationBuilder().setId(100L)
                    .setStudy(UNPUBLISHED_STUDY)
                    .build();


    private static final Housekeeping HOUSEKEEPING_NO_ASS =
            new HousekeepingBuilder()
                    .setId(897L)
                    .build();

    private static final Study UNPUBLISHED_STUDY_NO_ASS =
            new StudyBuilder().setId(2236L).setHousekeeping(HOUSEKEEPING_NO_ASS).build();

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    @Before
    public void setUpMock() {
        StudyController studyController = new StudyController(studyRepository,
                                                              housekeepingRepository,
                                                              diseaseTraitRepository,
                                                              efoTraitRepository,
                                                              curatorRepository,
                                                              curationStatusRepository,
                                                              platformRepository,
                                                              associationRepository,
                                                              ancestryRepository,
                                                              unpublishReasonRepository,
                                                              defaultPubMedSearchService,
                                                              studyOperationsService,
                                                              mappingDetailsService,
                                                              currentUserDetailsService,
                                                              studyFileService,
                                                              studyDuplicationService,
                                                              studyDeletionService,
                                                              eventsViewService, studyUpdateService);
        mockMvc = MockMvcBuilders.standaloneSetup(studyController).build();
    }

    @Test
    public void testViewStudyToDeletePublished() throws Exception {

        when(studyRepository.findOne(PUBLISHED_STUDY.getId())).thenReturn(PUBLISHED_STUDY);
        when(housekeepingRepository.findOne(HOUSEKEEPING_PUBLISHED.getId())).thenReturn(HOUSEKEEPING_PUBLISHED);
        when(associationRepository.findByStudyId(PUBLISHED_STUDY.getId())).thenReturn(Collections.singleton(
                ASSOCIATION_01));

        mockMvc.perform(MockMvcRequestBuilders.get("/studies/1234/delete").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_published_study_warning"))
                .andExpect(model().attributeExists("studyToDelete"))
                .andExpect(model().attribute("studyToDelete", isA(Study.class)));

        verify(studyRepository, times(1)).findOne(PUBLISHED_STUDY.getId());
        verify(housekeepingRepository, times(1)).findOne(HOUSEKEEPING_PUBLISHED.getId());
        verify(associationRepository, times(1)).findByStudyId(PUBLISHED_STUDY.getId());
    }

    @Test
    public void testViewStudyToDeleteNotPublishedNoAssociations() throws Exception {

        when(studyRepository.findOne(UNPUBLISHED_STUDY_NO_ASS.getId())).thenReturn(UNPUBLISHED_STUDY_NO_ASS);
        when(housekeepingRepository.findOne(HOUSEKEEPING_NO_ASS.getId())).thenReturn(HOUSEKEEPING_NO_ASS);
        when(associationRepository.findByStudyId(UNPUBLISHED_STUDY_NO_ASS.getId())).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(MockMvcRequestBuilders.get("/studies/2236/delete").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_study"))
                .andExpect(model().attributeExists("studyToDelete"))
                .andExpect(model().attribute("studyToDelete", isA(Study.class)));

        verify(studyRepository, times(1)).findOne(UNPUBLISHED_STUDY_NO_ASS.getId());
        verify(housekeepingRepository, times(1)).findOne(HOUSEKEEPING_NO_ASS.getId());
        verify(associationRepository, times(1)).findByStudyId(UNPUBLISHED_STUDY_NO_ASS.getId());
    }


    @Test
    public void testViewStudyToDeleteNotPublished() throws Exception {

        when(studyRepository.findOne(UNPUBLISHED_STUDY.getId())).thenReturn(UNPUBLISHED_STUDY);
        when(housekeepingRepository.findOne(HOUSEKEEPING_UNPUBLISHED.getId())).thenReturn(HOUSEKEEPING_UNPUBLISHED);
        when(associationRepository.findByStudyId(UNPUBLISHED_STUDY.getId())).thenReturn(Collections.singleton(
                ASSOCIATION_02));

        mockMvc.perform(MockMvcRequestBuilders.get("/studies/1235/delete").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_study_with_associations_warning"))
                .andExpect(model().attributeExists("studyToDelete"))
                .andExpect(model().attribute("studyToDelete", isA(Study.class)));

        verify(studyRepository, times(1)).findOne(UNPUBLISHED_STUDY.getId());
        verify(housekeepingRepository, times(1)).findOne(HOUSEKEEPING_UNPUBLISHED.getId());
        verify(associationRepository, times(1)).findByStudyId(UNPUBLISHED_STUDY.getId());
    }

    @Test
    public void testDeleteStudy() throws Exception {

        when(studyRepository.findOne(UNPUBLISHED_STUDY.getId())).thenReturn(UNPUBLISHED_STUDY);
        when(currentUserDetailsService.getUserFromRequest(Matchers.any(HttpServletRequest.class))).thenReturn(
                SECURE_USER);

        mockMvc.perform(MockMvcRequestBuilders.post("/studies/1235/delete").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().is3xxRedirection());

        verify(studyRepository, times(1)).findOne(UNPUBLISHED_STUDY.getId());
        verify(studyDeletionService, times(1)).deleteStudy(UNPUBLISHED_STUDY, SECURE_USER);
    }
}
