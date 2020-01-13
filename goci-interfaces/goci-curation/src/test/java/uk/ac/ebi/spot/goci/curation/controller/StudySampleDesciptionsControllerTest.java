package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ebi.spot.goci.curation.service.StudySampleDescriptionsDownloadService;

import java.io.OutputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by emma on 05/09/2016.
 *
 * @author emma
 */
@RunWith(MockitoJUnitRunner.class)
public class StudySampleDesciptionsControllerTest {

    private StudySampleDesciptionsController studySampleDesciptionsController;

    @Mock
    private StudySampleDescriptionsDownloadService studySampleDescriptionsDownloadService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        studySampleDesciptionsController = new StudySampleDesciptionsController(studySampleDescriptionsDownloadService);
        mockMvc = MockMvcBuilders.standaloneSetup(studySampleDesciptionsController).build();
    }

    @Test
    public void getStudiesSampleDescriptions() throws Exception {
        mockMvc.perform(get("/sampledescriptions"))
                .andExpect(content().contentType("text/tsv"))
                .andExpect(status().isOk());
        verify(studySampleDescriptionsDownloadService, times(1)).generateStudySampleDescriptions();
        verify(studySampleDescriptionsDownloadService, times(1)).createDownloadFile(Matchers.any(OutputStream.class), Matchers.anyCollection());
    }
}