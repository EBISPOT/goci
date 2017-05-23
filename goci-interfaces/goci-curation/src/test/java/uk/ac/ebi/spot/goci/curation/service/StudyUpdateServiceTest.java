package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.builder.EfoTraitBuilder;
import uk.ac.ebi.spot.goci.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.StudyTrackingOperationServiceImpl;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 04/08/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class StudyUpdateServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private AttributeUpdateService attributeUpdateService;

    @Mock
    private StudyTrackingOperationServiceImpl trackingOperationService;

    private StudyUpdateService studyUpdateService;

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final DiseaseTrait NEW_DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(799L).setTrait("Asthma").build();

    private static final DiseaseTrait DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(797L).setTrait("Acne").build();

    private static final EfoTrait EFO1 =
            new EfoTraitBuilder().setId(987L)
                    .setTrait("asthma")
                    .setUri("http://www.ebi.ac.uk/efo/EFO_0000270")
                    .build();

    private static final Study STU1 =
            new StudyBuilder().setId(802L).setEfoTraits(Collections.EMPTY_LIST).build();

    private static final Study STU1_UPDATED =
            new StudyBuilder().setId(802L).setEfoTraits(Collections.EMPTY_LIST).build();

    private static final Study STU2 =
            new StudyBuilder().setId(803L)
                    .setDiseaseTrait(DISEASE_TRAIT).
                    setEfoTraits(Collections.EMPTY_LIST)
                    .build();

    private static final Study STU2_UPDATED =
            new StudyBuilder().setId(803L)
                    .setDiseaseTrait(NEW_DISEASE_TRAIT)
                    .setEfoTraits(Arrays.asList(EFO1))
                    .build();

    @Before
    public void setUp() throws Exception {
        studyUpdateService = new StudyUpdateService(trackingOperationService, studyRepository, attributeUpdateService);
    }

    @Test
    public void updateStudyNoTraitChange() throws Exception {
        // Stubbing
        when(studyRepository.findOne(STU1.getId())).thenReturn(STU1);
        when(attributeUpdateService.compareAttribute("Disease Trait",
                                                     null,
                                                     null)).thenReturn(null);
        when(attributeUpdateService.compareAttribute("EFO Trait",
                                                     null,
                                                     null)).thenReturn(null);

        // Test updating a study
        studyUpdateService.updateStudy(STU1.getId(), STU1_UPDATED, SECURE_USER);

        verify(trackingOperationService, times(1)).update(STU1_UPDATED,
                                                          SECURE_USER,
                                                          "STUDY_UPDATE",
                                                          null);
        verify(studyRepository, times(1)).save(STU1_UPDATED);
        verify(attributeUpdateService, times(2)).compareAttribute(Matchers.anyString(),
                                                                  Matchers.anyString(),
                                                                  Matchers.anyString());
    }

    @Test
    public void updateStudyChangeDiseaseTraitAndEfoTrait() throws Exception {
        // Stubbing
        when(studyRepository.findOne(STU2.getId())).thenReturn(STU2);
        when(attributeUpdateService.compareAttribute("Disease Trait",
                                                     "Acne",
                                                     STU2_UPDATED.getDiseaseTrait().getTrait())).thenReturn(
                "Disease Trait updated from 'Acne' to 'Asthma'");
        when(attributeUpdateService.compareAttribute("EFO Trait",
                                                     null,
                                                     "asthma")).thenReturn(
                "EFO Trait set to 'asthma'");

        // Test updating a study
        studyUpdateService.updateStudy(STU2.getId(), STU2_UPDATED, SECURE_USER);

        verify(trackingOperationService, times(1)).update(STU2_UPDATED,
                                                          SECURE_USER,
                                                          "STUDY_UPDATE",
                                                          "Disease Trait updated from 'Acne' to 'Asthma', EFO Trait set to 'asthma'");
        verify(studyRepository, times(1)).save(STU2_UPDATED);
        verify(attributeUpdateService, times(2)).compareAttribute(Matchers.anyString(),
                                                                  Matchers.anyString(),
                                                                  Matchers.anyString());
    }
}