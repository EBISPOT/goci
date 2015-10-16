package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import uk.ac.ebi.spot.goci.CurationApplication;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void testAllDiseaseTraits() {

        DiseaseTrait diseaseTrait = Mockito.mock(DiseaseTrait.class);
        List <DiseaseTrait> diseaseTraits = Collections.singletonList(diseaseTrait);
        DiseaseTraitRepository diseaseTraitRepository = Mockito.mock(DiseaseTraitRepository.class);
        Mockito.when(diseaseTraitRepository.findAll(Mockito.any(Sort.class))).thenReturn(diseaseTraits);

        StudyRepository studyRepository = Mockito.mock(StudyRepository.class);
        DiseaseTraitController diseaseTraitControllerMock = new DiseaseTraitController(diseaseTraitRepository, studyRepository);



        Model mockModel = Mockito.mock(Model.class);
        String s = diseaseTraitController.allDiseaseTraits(mockModel);

        // Check the view name
        // Check model attributes


        Mockito.verify(mockModel).addAttribute("diseaseTraits",diseaseTraits);

//        model.addAttribute("diseaseTraits", diseaseTraitRepository.findAll(sortByTraitAsc()));
//        model.addAttribute("totaldiseaseTraits" , diseaseTraitRepository.findAll(sortByTraitAsc()).size());
//
//        // Return an empty DiseaseTrait object so user can add a new one
//        model.addAttribute("diseaseTrait", new DiseaseTrait());
//
//        return "disease_traits";
    }


}
