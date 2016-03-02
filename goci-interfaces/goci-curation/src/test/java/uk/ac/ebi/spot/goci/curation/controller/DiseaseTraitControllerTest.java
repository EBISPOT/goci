/*
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
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import java.util.Collection;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

*/
/**
 * Created by emma on 15/10/2015.
 *
 * @author emma
 *         <p>
 *         Disease trait unot controller test
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CurationApplication.class)
@WebAppConfiguration
public class DiseaseTraitControllerTest {

    @Autowired
    private DiseaseTraitController diseaseTraitController;

    private MockMvc mockMvc;

    @Before
    public void setUpMock() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.diseaseTraitController).build();
    }

    @Test
    public void testAllDiseaseTraits() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attributeExists("diseaseTraits"))
                .andExpect(model().attributeExists("totaldiseaseTraits"))
                .andExpect(model().attribute("diseaseTrait", isA(DiseaseTrait.class)))
                .andExpect(model().attribute("diseaseTraits", isA(Collection.class)))
                .andExpect(model().attribute("diseaseTraits",iterableWithSize(greaterThan(0))))
                .andExpect(model().attribute("totaldiseaseTraits", greaterThan(0)))
                .andExpect(model().attribute("totaldiseaseTraits", isA(Integer.class)))
        ;
    }
}
*/
