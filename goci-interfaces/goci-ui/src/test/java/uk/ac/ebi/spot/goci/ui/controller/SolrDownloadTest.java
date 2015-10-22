package uk.ac.ebi.spot.goci.ui.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.spot.goci.SearchApplication;

/**
 * Created by dwelter on 21/10/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/application.properties" })
@SpringApplicationConfiguration(classes = SearchApplication.class)
public class SolrDownloadTest {


//    @Autowired
//    private SearchConfiguration config;
    @Autowired
    private SolrSearchController controller;


    @Test
    public void TestDownloadController(){

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRequestURI("api/search/downloads");
        request.setQueryString("q=text:*&pvalfilter=&orfilter=&betafilter=&datefilter=&traitfilter[]=&dateaddedfilter=");


        String output = request.

////        SearchConfiguration config = new SearchConfiguration();
////        SolrSearchController controller = new SolrSearchController(config);
//
//        HttpServletResponse response = new MockHttpServletResponse();
//
//        try {
//            controller.getSearchResults("*", "", "", "", "", null, "", response);
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail();
//        }
    }

//    @Configuration
//    @ComponentScan("uk.ac.ebi.spot.goci.ui.controller")
//    static class someConfig {
//
//        // because @PropertySource doesnt work in annotation only land
//        @Bean
//        PropertyPlaceholderConfigurer propConfig() {
//            PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
//            ppc.setLocation(new ClassPathResource("application.properties"));
//            return ppc;
//        }
//    }
}
