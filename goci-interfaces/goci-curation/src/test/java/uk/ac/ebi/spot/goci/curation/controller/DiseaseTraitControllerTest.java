package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * Created by emma on 15/10/2015.
 *
 * @author emma
 *         <p>
 *         Disease trait unot controller test
 */
@RunWith(MockitoJUnitRunner.class)
public class DiseaseTraitControllerTest {

    @Mock
    private DiseaseTraitRepository diseaseTraitRepository;

    @Mock
    private StudyRepository studyRepository;

    private MockMvc mockMvc;

    private static final DiseaseTrait DISEASE_TRAIT_1 =
            new DiseaseTraitBuilder().setId(799L).setTrait("Asthma").build();

    private static final DiseaseTrait DISEASE_TRAIT_2 =
            new DiseaseTraitBuilder().setId(799L).setTrait("Body mass index").build();

    @Before
    public void setUpMock() {
        DiseaseTraitController diseaseTraitController =
                new DiseaseTraitController(diseaseTraitRepository, studyRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(diseaseTraitController).build();
    }

    @Test
    public void testAllDiseaseTraits() throws Exception {

        List<DiseaseTrait> allTraits = Arrays.asList(DISEASE_TRAIT_1, DISEASE_TRAIT_2);
        when(diseaseTraitRepository.findAll(Matchers.any(Sort.class))).thenReturn(allTraits);

        mockMvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attributeExists("diseaseTraits"))
                .andExpect(model().attributeExists("totaldiseaseTraits"))
                .andExpect(model().attribute("diseaseTrait", instanceOf(DiseaseTrait.class)))
                .andExpect(model().attribute("diseaseTraits", instanceOf(Collection.class)))
                .andExpect(model().attribute("diseaseTraits", hasSize(2)))
                .andExpect(model().attribute("totaldiseaseTraits", instanceOf(Integer.class)))
                .andExpect(model().attribute("totaldiseaseTraits", equalTo(2)))
        ;
    }
}

