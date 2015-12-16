package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by emma on 16/12/2015.
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class EndpointsTest {

    @InjectMocks
    private HomeController homeController;

    @InjectMocks
    private DiseaseTraitController diseaseTraitController;

    @Mock
    private DiseaseTraitRepository diseaseTraitRepository;

    @Mock
    private Model model;

    @Test
    public void testHome() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(homeController).build();
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testDiseaseTrait() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(diseaseTraitController).build();
        mvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"));
    }


}
