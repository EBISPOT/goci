package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by emma on 15/10/2015.
 *
 * @author emma
 *         <p>
 *         Disease trait controller test
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
public class DiseaseTraitControllerTest {

    @Mock
    private DiseaseTraitRepository diseaseTraitRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    private MockMvc mockMvc;


    private static final Collection<Study> STUDIES = new ArrayList<>();
    private static final String FIRST_DISEASE_TRAIT_DESCRIPTION = "Addiction";
    private static final String SECOND_DISEASE_TRAIT_DESCRIPTION = "Aging";

    private static final DiseaseTrait DT1 =
            new DiseaseTraitBuilder().setId(799L)
                    .setTrait(FIRST_DISEASE_TRAIT_DESCRIPTION)
                    .setStudies(STUDIES)
                    .build();
    private static final DiseaseTrait DT2 =
            new DiseaseTraitBuilder().setId(798L)
                    .setTrait(SECOND_DISEASE_TRAIT_DESCRIPTION)
                    .setStudies(STUDIES)
                    .build();


    @Before
    public void setUpMock() {
        DiseaseTraitController diseaseTraitController =
                new DiseaseTraitController(diseaseTraitRepository, studyRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(diseaseTraitController).build();
    }

    @Test
    public void testAllDiseaseTraits() throws Exception {


        // Mock disease trait object
        List<DiseaseTrait> diseaseTraits = Arrays.asList(DT1, DT2);
        Integer totalDiseaseTraits = diseaseTraits.size();

        when(diseaseTraitRepository.findAll(Mockito.any(Sort.class))).thenReturn(diseaseTraits);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attributeExists("diseaseTraits"))
                .andExpect(model().attributeExists("totaldiseaseTraits"))
                .andExpect(model().attribute("diseaseTrait", isA(DiseaseTrait.class)))
                .andExpect(model().attribute("diseaseTraits", hasItem(
                        allOf(
                                hasProperty("id", is(799L)),
                                hasProperty("trait", is("Addiction")),
                                hasProperty("studies", is(STUDIES))
                        )
                )))
                .andExpect(model().attribute("diseaseTraits", hasItem(
                        allOf(
                                hasProperty("id", is(798L)),
                                hasProperty("trait", is("Aging")),
                                hasProperty("studies", is(STUDIES))
                        )
                )))
                .andExpect(model().attribute("totaldiseaseTraits", isA(Integer.class)))
                .andExpect(model().attribute("totaldiseaseTraits", 2));
    }
}
