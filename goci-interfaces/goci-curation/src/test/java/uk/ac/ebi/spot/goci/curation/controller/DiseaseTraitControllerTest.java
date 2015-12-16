package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 15/10/2015.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class DiseaseTraitControllerTest {

    @InjectMocks
    private DiseaseTraitController diseaseTraitController;

    @Mock
    private DiseaseTraitRepository diseaseTraitRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private DiseaseTrait diseaseTrait;

    private ArgumentCaptor<DiseaseTrait> anyDiseaseTrait = ArgumentCaptor.forClass(DiseaseTrait.class);

    private static final Collection<Study> STUDIES = new ArrayList<>();
    private static final String FIRST_DISEASE_TRAIT_DESCRIPTION = "Addiction";
    private static final String SECOND_DISEASE_TRAIT_DESCRIPTION = "Aging";

    private static final DiseaseTrait DT1 =
            new DiseaseTraitBuilder().id(799L)
                    .trait(FIRST_DISEASE_TRAIT_DESCRIPTION)
                    .studies(STUDIES)
                    .build();
    private static final DiseaseTrait DT2 =
            new DiseaseTraitBuilder().id(798L)
                    .trait(SECOND_DISEASE_TRAIT_DESCRIPTION)
                    .studies(STUDIES)
                    .build();

    private static final DiseaseTrait DT3 =
            new DiseaseTraitBuilder().build();


/*
    @Test
    public void testEndpoint() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(diseaseTraitController).build();
        mvc.perform(MockMvcRequestBuilders.get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"));
    }
*/


    @Test
    public void testAllDiseaseTraits() {

        // Mock disease trait object
        List<DiseaseTrait> diseaseTraits = Collections.singletonList(DT1);
        Integer totalDiseaseTraits = diseaseTraits.size();
        DiseaseTrait newTrait = new DiseaseTrait();

        // Stubbing
        when(diseaseTraitRepository.findAll(Mockito.any(Sort.class))).thenReturn(diseaseTraits);
        assertEquals("disease_traits", diseaseTraitController.allDiseaseTraits(model));

        // Verification
        Mockito.verify(model).addAttribute("diseaseTraits", diseaseTraits);
        Mockito.verify(model).addAttribute("totaldiseaseTraits", totalDiseaseTraits);
        Mockito.verify(model).addAttribute("diseaseTrait", newTrait);

    }

    @Test
    public void whenAddingAnItemItShouldUseTheRepository() {
        diseaseTraitController.addDiseaseTrait(DT1, bindingResult, model, redirectAttributes);
        verify(diseaseTraitRepository).save(anyDiseaseTrait.capture());
        assertEquals("Addiction", anyDiseaseTrait.getValue().getTrait());
    }

}
