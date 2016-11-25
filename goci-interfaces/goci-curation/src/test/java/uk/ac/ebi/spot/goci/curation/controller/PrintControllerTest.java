package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.builder.AncestryBuilder;
import uk.ac.ebi.spot.goci.builder.HousekeepingBuilder;
import uk.ac.ebi.spot.goci.builder.StudyBuilder;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.curation.service.StudyPrintService;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by emma on 06/06/2016.
 * <p>
 * PrintController test
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintControllerTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private AncestryRepository ancestryRepository;

    @Mock
    private StudyPrintService studyPrintService;

    private MockMvc mockMvc;

    private static final Ancestry ETH1 = new AncestryBuilder().setNotes("ETH1 notes")
            .setId(40L)
            .setType("initial")
            .build();

    private static final Ancestry ETH2 = new AncestryBuilder().setNotes("ETH2 notes")
            .setId(60L)
            .setType("replication")
            .build();

    private static final Ancestry ETH3 = new AncestryBuilder().setNotes("ETH2 notes")
            .setId(60L)
            .setType("replication")
            .build();

    private static final Housekeeping HOUSEKEEPING =
            new HousekeepingBuilder()
                    .setId(799L)
                    .setStudySnpCheckedLevelOne(true)
                    .build();

    private static final Study STUDY =
            new StudyBuilder().setId(101L)
                    .setInitialSampleSize("Initial Sample Size")
                    .setReplicateSampleSize("Replicate Sample Size")
                    .setHousekeeping(HOUSEKEEPING)
                    .build();


    @Before
    public void setUp() throws Exception {
        PrintController printController = new PrintController(studyRepository, ancestryRepository, studyPrintService);
        mockMvc = MockMvcBuilders.standaloneSetup(printController).build();
    }

    @Test
    public void viewPrintableDetailsOfStudy() throws Exception {

        // Set up some basic views
        SnpAssociationTableView snpAssociationTableView1 = new SnpAssociationTableView();
        snpAssociationTableView1.setBetaNum((float) 0.012);
        SnpAssociationTableView snpAssociationTableView2 = new SnpAssociationTableView();
        snpAssociationTableView1.setOrPerCopyNum((float) 5.97);

        // Stub
        when(studyRepository.findOne(STUDY.getId())).thenReturn(STUDY);

        Collection<Ancestry> initialStudyAncestryDescriptions = new ArrayList<>();
        Collection<Ancestry> replicationStudyAncestryDescriptions = new ArrayList<>();
        initialStudyAncestryDescriptions.add(ETH1);
        replicationStudyAncestryDescriptions.add(ETH2);
        replicationStudyAncestryDescriptions.add(ETH3);

        when(ancestryRepository.findByStudyIdAndType(STUDY.getId(), "initial")).thenReturn(
                initialStudyAncestryDescriptions);
        when(ancestryRepository.findByStudyIdAndType(STUDY.getId(), "replication")).thenReturn(
                replicationStudyAncestryDescriptions);
        when(studyPrintService.generatePrintView(STUDY.getId())).thenReturn(Arrays.asList(snpAssociationTableView1,
                                                                                          snpAssociationTableView2));

        MvcResult mvcResult = this.mockMvc.perform(get("/studies/101/printview").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(request().asyncStarted())
                .andExpect(request().asyncResult("study_printview"))
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("study_printview"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attribute("study", instanceOf(Study.class)))
                .andExpect(model().attributeExists("housekeeping"))
                .andExpect(model().attribute("housekeeping", instanceOf(Housekeeping.class)))
                .andExpect(model().attribute("housekeeping", hasProperty("studySnpCheckedLevelOne", is(true))))
                .andExpect(model().attributeExists("initialSampleDescription"))
                .andExpect(model().attribute("initialSampleDescription", instanceOf(String.class)))
                .andExpect(model().attribute("initialSampleDescription", is("Initial Sample Size")))
                .andExpect(model().attributeExists("replicateSampleDescription"))
                .andExpect(model().attribute("replicateSampleDescription", instanceOf(String.class)))
                .andExpect(model().attribute("replicateSampleDescription", is("Replicate Sample Size")))
                .andExpect(model().attributeExists("initialStudyAncestryDescriptions"))
                .andExpect(model().attribute("initialStudyAncestryDescriptions", hasSize(1)))
                .andExpect(model().attributeExists("replicationStudyAncestryDescriptions"))
                .andExpect(model().attribute("replicationStudyAncestryDescriptions", hasSize(2)))
                .andExpect(model().attributeExists("snpAssociationTableViews"))
                .andExpect(model().attribute("snpAssociationTableViews", hasSize(2)));
    }
}