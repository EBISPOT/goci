package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.CurationApplication;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by emma on 15/10/2015.
 * @author emma
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CurationApplication.class)
@WebAppConfiguration
public class DiseaseTraitControllerTest {

    @Autowired
    private DiseaseTraitController diseaseTraitController;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(diseaseTraitController).build();
    }

    @Test
    public void getDiseaseTraits() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk());
    }


}
