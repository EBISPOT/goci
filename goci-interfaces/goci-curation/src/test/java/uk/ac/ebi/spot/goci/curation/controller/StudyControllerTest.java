package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import uk.ac.ebi.spot.goci.curation.model.PubmedIdForImport;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.repository.UnpublishReasonRepository;
import uk.ac.ebi.spot.goci.service.DefaultPubMedSearchService;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by emma on 12/02/2016.
 *
 * @author emma
 *         <p>
 *         Study controller test
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
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
    private AssociationRepository associationRepository;

    @Mock
    private EthnicityRepository ethnicityRepository;

    @Mock
    private UnpublishReasonRepository unpublishReasonRepository;

    @Mock
    private DefaultPubMedSearchService defaultPubMedSearchService;

    @Mock
    private StudyOperationsService studyService;
    
    @Mock
    private Study study;

    private MockMvc mockMvc;


    @Before
    public void setUpMock() {
        StudyController studyController = new StudyController(studyRepository,
                                                              studyService,
                                                              defaultPubMedSearchService,
                                                              unpublishReasonRepository,
                                                              ethnicityRepository,
                                                              associationRepository,
                                                              curationStatusRepository,
                                                              curatorRepository,
                                                              efoTraitRepository,
                                                              diseaseTraitRepository,
                                                              housekeepingRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(studyController).build();
    }

    @Test
    public void testNewStudyForm() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/studies/new").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("add_study"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("pubmedIdForImport"))
                .andExpect(model().attribute("study", isA(Study.class)))
                .andExpect(model().attribute("pubmedIdForImport", isA(PubmedIdForImport.class)));
    }
}
