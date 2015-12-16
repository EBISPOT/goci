package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by emma on 15/10/2015.
 *
 * @author emma
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class DiseaseTraitControllerTestNew {

    private StudyRepository studyRepositoryMock;

    private DiseaseTraitRepository diseaseTraitRepositoryMock;

    private DiseaseTraitController diseaseTraitControllerMock;

    @Before
    public void setupMocks() {
        diseaseTraitRepositoryMock = Mockito.mock(DiseaseTraitRepository.class);
        studyRepositoryMock = Mockito.mock(StudyRepository.class);
        diseaseTraitControllerMock = new DiseaseTraitController(diseaseTraitRepositoryMock, studyRepositoryMock);
    }

    @Test
    public void testEndpoint() throws Exception {

        // Mock disease trait object
        DiseaseTrait diseaseTrait = Mockito.mock(DiseaseTrait.class);
        List<DiseaseTrait> diseaseTraits = Collections.singletonList(diseaseTrait);
        Mockito.when(diseaseTraitRepositoryMock.findAll(Mockito.any(Sort.class))).thenReturn(diseaseTraits);

        MockMvc mvc = MockMvcBuilders.standaloneSetup(diseaseTraitControllerMock).build();

        mvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"));
    }

    @Test
    public void testAllDiseaseTraits() {

        // Mock disease trait object
        DiseaseTrait diseaseTrait = Mockito.mock(DiseaseTrait.class);
        List<DiseaseTrait> diseaseTraits = Collections.singletonList(diseaseTrait);
        Integer totalDiseaseTraits = diseaseTraits.size();

        // Stubbing
        Mockito.when(diseaseTraitRepositoryMock.findAll(Mockito.any(Sort.class))).thenReturn(diseaseTraits);

        Model mockModel = Mockito.mock(Model.class);
        String s = diseaseTraitControllerMock.allDiseaseTraits(mockModel);
        assertEquals("disease_traits", s);

        Mockito.verify(diseaseTraitRepositoryMock, Mockito.times(2)).findAll(Mockito.any(Sort.class));
        Mockito.verify(mockModel).addAttribute("diseaseTraits", diseaseTraits);
        Mockito.verify(mockModel).addAttribute("totaldiseaseTraits", totalDiseaseTraits);

        // TODO DOES NOT WORK
        Mockito.verify(mockModel).addAttribute("diseaseTrait", diseaseTrait);
    }
}
