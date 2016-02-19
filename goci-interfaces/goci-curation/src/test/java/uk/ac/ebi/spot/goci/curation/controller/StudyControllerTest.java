package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.CurationApplication;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.curation.model.PubmedIdForImport;
import uk.ac.ebi.spot.goci.curation.service.StudyOperationsService;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.isA;
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
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CurationApplication.class)
@WebAppConfiguration
public class StudyControllerTest {

    @Autowired
    private StudyController studyController;

    private MockMvc mockMvc;

    @Before
    public void setUpMock() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.studyController).build();
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
