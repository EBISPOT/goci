package uk.ac.ebi.spot.goci.curation.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.curation.builder.SecureUserBuilder;
import uk.ac.ebi.spot.goci.curation.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.service.tracking.StudyTrackingOperationServiceImpl;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

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

    private static final DiseaseTrait NEW_DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(799L).setTrait("Asthma").build();

    private static final DiseaseTrait DISEASE_TRAIT =
            new DiseaseTraitBuilder().setId(797L).setTrait("Acne").build();

    private static final Study UPDATED_STUDY =
            new StudyBuilder().setId(802L).setDiseaseTrait(NEW_DISEASE_TRAIT).build();

    private static final SecureUser SECURE_USER =
            new SecureUserBuilder().setId(564L).setEmail("test@test.com").setPasswordHash("738274$$").build();

    private static final Study STU1 =
            new StudyBuilder().setId(802L).setDiseaseTrait(null).build();

    private static final Study STU2 =
            new StudyBuilder().setId(802L).setDiseaseTrait(DISEASE_TRAIT).build();

    @Before
    public void setUp() throws Exception {
        studyUpdateService = new StudyUpdateService(trackingOperationService, studyRepository, attributeUpdateService);
    }

    @Test
    public void updateStudy() throws Exception {
        // Stubbing
        when(studyRepository.findOne(STU1.getId())).thenReturn(STU1);
        when(attributeUpdateService.compareAttribute("Disease Trait",
                                                     null,
                                                     UPDATED_STUDY.getDiseaseTrait().getTrait())).thenReturn(
                "Disease Trait set to Asthma");

        // Test updating a study
        studyUpdateService.updateStudy(STU1.getId(), UPDATED_STUDY, SECURE_USER);

        verify(trackingOperationService, times(1)).update(UPDATED_STUDY,
                                                          SECURE_USER,
                                                          EventType.STUDY_UPDATE,
                                                          "Disease Trait set to Asthma");
        verify(studyRepository, times(1)).save(UPDATED_STUDY);
    }

    @Test
    public void updateStudyChangeDiseaseTrait() throws Exception {
        // Stubbing
        when(studyRepository.findOne(STU2.getId())).thenReturn(STU2);
        when(attributeUpdateService.compareAttribute("Disease Trait",
                                                     "Acne",
                                                     UPDATED_STUDY.getDiseaseTrait().getTrait())).thenReturn(
                "Disease Trait updated from Acne to Asthma");

        // Test updating a study
        studyUpdateService.updateStudy(STU2.getId(), UPDATED_STUDY, SECURE_USER);

        verify(trackingOperationService, times(1)).update(UPDATED_STUDY,
                                                          SECURE_USER,
                                                          EventType.STUDY_UPDATE,
                                                          "Disease Trait updated from Acne to Asthma");
        verify(studyRepository, times(1)).save(UPDATED_STUDY);
    }
}