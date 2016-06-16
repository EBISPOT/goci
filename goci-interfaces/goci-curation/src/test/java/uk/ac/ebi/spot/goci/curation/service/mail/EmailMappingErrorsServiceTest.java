package uk.ac.ebi.spot.goci.curation.service.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.builder.AssociationBuilder;
import uk.ac.ebi.spot.goci.builder.AssociationReportBuilder;
import uk.ac.ebi.spot.goci.curation.builder.CurationSystemEmailToCuratorBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.mail.CurationSystemEmailToCurator;
import uk.ac.ebi.spot.goci.curation.service.AssociationMappingErrorService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 09/03/2016.
 *
 * @author emma
 *         <p>
 *         Test for uk.ac.ebi.spot.goci.curation.service.mail.EmailMappingErrorsService
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailMappingErrorsServiceTest {

    @Mock
    private AssociationMappingErrorService associationMappingErrorService;

    private EmailMappingErrorsService emailMappingErrorsService;

    private static final Study STU_NO_ASSOCIATIONS =
            new StudyBuilder().setId(400L).build();

    private static final Study STU_WITH_ASSOCIATIONS =
            new StudyBuilder().setId(401L).build();

    private static final CurationSystemEmailToCurator EMAIL1 =
            new CurationSystemEmailToCuratorBuilder().setLink("link/").setBody("This is a test body" + "\n\n").buiid();

    private static final Association ASS1 =
            new AssociationBuilder().setId(103L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("Level 1 Curator").build();

    private static final Association ASS2 =
            new AssociationBuilder().setId(104L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("Level 2 Curator").build();

    private static final Association ASS3 =
            new AssociationBuilder().setId(103L)
                    .setLastMappingDate(new Date()).setLastMappingPerformedBy("Level 1 Curator").build();

    private static final AssociationReport ASS_REP =
            new AssociationReportBuilder().setSnpGeneOnDiffChr("ERROR").build();

    @Before
    public void setUp() throws Exception {
        emailMappingErrorsService = new EmailMappingErrorsService(associationMappingErrorService);
    }

    @After
    public void after() throws Exception {
        EMAIL1.setBody("This is a test body" + "\n\n");
        STU_WITH_ASSOCIATIONS.setAssociations(new ArrayList<>());
    }

    @Test
    public void testGetMappingDetailsOnStudyWithNoAssociations() throws Exception {
        STU_NO_ASSOCIATIONS.setAssociations(Collections.EMPTY_LIST);
        CurationSystemEmailToCurator email = emailMappingErrorsService.getMappingDetails(STU_NO_ASSOCIATIONS,
                                                                                         EMAIL1);
        assertThat(email).isInstanceOf(CurationSystemEmailToCurator.class);
        assertThat(email).hasFieldOrPropertyWithValue("body",
                                                      "This is a test body" + "\n\n" +
                                                              "No associations for this study");

    }


    @Test
    public void testGetMappingDetailsOnStudyWithAssociationsThatHaveNoErrors() throws Exception {
        STU_WITH_ASSOCIATIONS.setAssociations(Arrays.asList(ASS1, ASS2));
        CurationSystemEmailToCurator email = emailMappingErrorsService.getMappingDetails(STU_WITH_ASSOCIATIONS,
                                                                                         EMAIL1);
        assertThat(email).isInstanceOf(CurationSystemEmailToCurator.class);
        assertThat(email).hasFieldOrPropertyWithValue("body",
                                                      "This is a test body" + "\n\n" +
                                                              "Note: No mapping errors detected for any association in this study.");

    }

    @Test
    public void testGetMappingDetailsOnStudyWithAssociationsThatHaveErrors() throws Exception {

        ASS3.setAssociationReport(ASS_REP);
        STU_WITH_ASSOCIATIONS.setAssociations(Collections.singletonList(ASS3));
        Map<String, String> map = new HashMap<>();
        map.put("No Gene For Symbol", "ERROR");
        when(associationMappingErrorService.createAssociationErrorMap(ASS_REP)).thenReturn(map);

        CurationSystemEmailToCurator email = emailMappingErrorsService.getMappingDetails(STU_WITH_ASSOCIATIONS,
                                                                                         EMAIL1);
        assertThat(email).isInstanceOf(CurationSystemEmailToCurator.class);
        assertThat(email).hasFieldOrProperty("body");
        assertThat(email.getBody()).contains("Mapping errors: ERROR");
    }
}