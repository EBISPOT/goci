package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
            new DiseaseTraitBuilder().setId(800L).setTrait("Body mass index").build();

    private static final DiseaseTrait NEW_DISEASE_TRAIT =
            new DiseaseTraitBuilder().setTrait("Urate levels").build();

    private static final DiseaseTrait NEW_DISEASE_TRAIT_WITH_ERROR =
            new DiseaseTraitBuilder().build();

    private static final Study STUDY = new StudyBuilder().setId(123L).setDiseaseTrait(DISEASE_TRAIT_2).build();

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

        mockMvc.perform(get("/diseasetraits").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("disease_traits"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attributeExists("diseaseTraits"))
                .andExpect(model().attributeExists("totaldiseaseTraits"))
                .andExpect(model().attribute("diseaseTrait", instanceOf(DiseaseTrait.class)))
                .andExpect(model().attribute("diseaseTraits", instanceOf(Collection.class)))
                .andExpect(model().attribute("diseaseTraits", hasSize(2)))
                .andExpect(model().attribute("totaldiseaseTraits", instanceOf(Integer.class)))
                .andExpect(model().attribute("totaldiseaseTraits", equalTo(2)));

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void testAddDiseaseTrait() throws Exception {

        when(diseaseTraitRepository.findByTraitIgnoreCase(Matchers.anyString())).thenReturn(null);

        mockMvc.perform(post("/diseasetraits")
                                .param("trait", NEW_DISEASE_TRAIT.getTrait()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("diseaseTraitSaved", "Trait Urate levels added to database"))
                .andExpect(view().name("redirect:/diseasetraits"));

        //verify properties of bound object
        ArgumentCaptor<DiseaseTrait> diseaseTraitArgumentCaptor = ArgumentCaptor.forClass(DiseaseTrait.class);
        verify(diseaseTraitRepository).save(diseaseTraitArgumentCaptor.capture());
        assertEquals(NEW_DISEASE_TRAIT.getTrait(), diseaseTraitArgumentCaptor.getValue().getTrait());
        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void testAddDiseaseTraitWithErrors() throws Exception {

        when(diseaseTraitRepository.findByTraitIgnoreCase(Matchers.anyString())).thenReturn(null);

        mockMvc.perform(post("/diseasetraits")
                                .param("trait", NEW_DISEASE_TRAIT_WITH_ERROR.getTrait()))
                .andExpect(model().attributeHasFieldErrors("diseaseTrait", "trait"))
                .andExpect(view().name("disease_traits"));

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void testAddDiseaseTraitWithExistingTrait() throws Exception {

        when(diseaseTraitRepository.findByTraitIgnoreCase(Matchers.anyString())).thenReturn(DISEASE_TRAIT_1);

        mockMvc.perform(post("/diseasetraits")
                                .param("trait", DISEASE_TRAIT_1.getTrait()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("diseaseTraitExists",
                                             "Trait already exists " +
                                                     "in database: database value = Asthma, value entered = Asthma"))
                .andExpect(view().name("redirect:/diseasetraits"));

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void testViewDiseaseTrait() throws Exception {

        when(diseaseTraitRepository.findOne(Matchers.anyLong())).thenReturn(DISEASE_TRAIT_1);

        mockMvc.perform(get("/diseasetraits/799").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_disease_trait"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attribute("diseaseTrait", instanceOf(DiseaseTrait.class)))
                .andExpect(model().attribute("diseaseTrait", hasProperty("trait", is("Asthma"))));

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void editDiseaseTrait() throws Exception {

        mockMvc.perform(post("/diseasetraits/799")
                                .param("trait", "Severe asthma"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/diseasetraits"));

        //verify properties of bound object
        ArgumentCaptor<DiseaseTrait> diseaseTraitArgumentCaptor = ArgumentCaptor.forClass(DiseaseTrait.class);
        verify(diseaseTraitRepository).save(diseaseTraitArgumentCaptor.capture());
        assertEquals("Severe asthma", diseaseTraitArgumentCaptor.getValue().getTrait());

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void editDiseaseTraitWithErrors() throws Exception {

        mockMvc.perform(post("/diseasetraits/799")
                                .param("trait", ""))
                .andExpect(model().attributeHasFieldErrors("diseaseTrait", "trait"))
                .andExpect(view().name("edit_disease_trait"));

        verifyZeroInteractions(studyRepository);
    }

    @Test
    public void testViewDiseaseTraitToDelete() throws Exception {

        when(diseaseTraitRepository.findOne(Matchers.anyLong())).thenReturn(DISEASE_TRAIT_2);
        when(studyRepository.findByDiseaseTraitId(DISEASE_TRAIT_2.getId())).thenReturn(Collections.singletonList(STUDY));

        mockMvc.perform(get("/diseasetraits/800/delete").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_disease_trait"))
                .andExpect(model().attributeExists("diseaseTrait"))
                .andExpect(model().attributeExists("studies"))
                .andExpect(model().attributeExists("totalStudies"))
                .andExpect(model().attribute("diseaseTrait", instanceOf(DiseaseTrait.class)))
                .andExpect(model().attribute("studies", instanceOf(Collection.class)))
                .andExpect(model().attribute("studies", hasSize(1)))
                .andExpect(model().attribute("totalStudies", instanceOf(Integer.class)))
                .andExpect(model().attribute("totalStudies", equalTo(1)));
    }


    @Test
    public void testDeleteDiseaseTrait() throws Exception {

        when(studyRepository.findByDiseaseTraitId(DISEASE_TRAIT_1.getId())).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(post("/diseasetraits/799/delete")
                                .param("id", "799"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/diseasetraits"));

        //verify properties of bound object
        ArgumentCaptor<Long> diseaseTraitIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(diseaseTraitRepository).delete(diseaseTraitIdArgumentCaptor.capture());
        assertEquals(799L, diseaseTraitIdArgumentCaptor.getValue().longValue());
    }

    @Test
    public void testDeleteDiseaseTraitWhereDiseaseTraitHasStudy() throws Exception {

        when(studyRepository.findByDiseaseTraitId(DISEASE_TRAIT_2.getId())).thenReturn(Collections.singletonList(STUDY));

        mockMvc.perform(post("/diseasetraits/800/delete")
                                .param("id", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("diseaseTraitUsed", "Trait is used in 1 study/studies, cannot delete!"))
                .andExpect(view().name("redirect:/diseasetraits/800/delete"));
    }
}