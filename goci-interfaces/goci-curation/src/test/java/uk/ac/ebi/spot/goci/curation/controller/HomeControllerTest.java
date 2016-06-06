package uk.ac.ebi.spot.goci.curation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by emma on 14/10/2015.
 *
 * @author emma
 *         <p>
 *         Unit test for HomeController based on http://spring.io/guides/gs/spring-boot/
 */
@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    private HomeController homeController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        homeController = new HomeController();
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    public void getHome() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}