package uk.ac.ebi.spot.goci.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by dwelter on 16/02/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnsemblControllerTest {


    @Mock
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Before
    public void setUp() throws Exception {
        EnsemblController ensemblController = new EnsemblController(singleNucleotidePolymorphismRepository);
//        mockMvc = MockMvcBuilders.standaloneSetup(ensemblController).build();
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


    @Test
    public void testGetColourMapping() throws Exception{

        this.mockMvc
                .perform(get("/gwas/rest/api/parentMapping/EFO_0001359").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());



    }

}
