package uk.ac.ebi.spot.goci.curation.controller;

import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ebi.spot.goci.curation.builder.DiseaseTraitBuilder;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItems;

/**
 * Created by emma on 15/12/2015.
 *
 * @author emma
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class DiseaseTraitControllerIT {

    private static final String DISEASE_TRAITS_RESOURCE = "/diseasetraits";
    private static final String DISEASE_TRAIT_RESOURCE = "/diseasetraits/{diseaseTraitId}";
    private static final String TRAIT_FIELD = "trait";
    private static final String STUDIES_FIELD = "studies";

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

    private StudyRepository studyRepositoryMock;
    private DiseaseTraitRepository diseaseTraitRepositoryMock;
    private DiseaseTraitController diseaseTraitControllerMock;

    private DiseaseTrait firstDiseaseTrait;
    private DiseaseTrait secondDiseaseTrait;

    @Before
    public void setUp() {
        diseaseTraitRepositoryMock = Mockito.mock(DiseaseTraitRepository.class);
        studyRepositoryMock = Mockito.mock(StudyRepository.class);
        diseaseTraitControllerMock = new DiseaseTraitController(diseaseTraitRepositoryMock, studyRepositoryMock);

        firstDiseaseTrait = diseaseTraitRepositoryMock.save(DT1);
        secondDiseaseTrait = diseaseTraitRepositoryMock.save(DT2);
        List<DiseaseTrait> all = diseaseTraitRepositoryMock.findAll();
        RestAssured.port =0;
    }

    @Test
    public void getItemsShouldReturnBothItems() {
        when().get(DISEASE_TRAITS_RESOURCE)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(TRAIT_FIELD, hasItems(FIRST_DISEASE_TRAIT_DESCRIPTION, SECOND_DISEASE_TRAIT_DESCRIPTION))
                .body(STUDIES_FIELD, hasItems(STUDIES, STUDIES));
    }

}
