package uk.ac.ebi.spot.goci.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by dwelter on 16/02/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnsemblControllerTest {


//    @Mock
//    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Before
    public void setUp() throws Exception {
//        EnsemblController ensemblController = new EnsemblController(singleNucleotidePolymorphismRepository);
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


    @Test
    public void testGetColourMapping() throws Exception{

        this.mockMvc
                .perform(get("/gwas/rest/api/parentMapping/EFO_0001359").contextPath("/gwas/rest").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void testGetColourMappings() throws Exception{

        List<String> terms = new ArrayList<>();

        terms.add("EFO_0001359");
        terms.add("HP_0002140");
        terms.add("GO_1902518");


        this.mockMvc
                .perform(post("/gwas/rest/api/parentMappings")
                                 .contextPath("/gwas/rest")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(asJsonString(terms))
                                 .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());



    }


    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
