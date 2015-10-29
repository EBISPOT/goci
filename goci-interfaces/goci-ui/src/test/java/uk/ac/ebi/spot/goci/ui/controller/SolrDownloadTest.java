package uk.ac.ebi.spot.goci.ui.controller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.spot.goci.SearchApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dwelter on 21/10/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/application.properties" })
@SpringApplicationConfiguration(classes = SearchApplication.class)
public class SolrDownloadTest {


//    @Autowired
//    private SearchConfiguration config;
//    @Autowired
//    private SolrSearchController controller;


    @Test
    public void TestDownloadController(){

//        MockHttpServletRequest request = new MockHttpServletRequest();

//        request.setRequestURI("api/search/downloads");
//        request.setQueryString("q=text:*&pvalfilter=&orfilter=&betafilter=&datefilter=&traitfilter[]=&dateaddedfilter=");

        String searchString = "http://www.ebi.ac.uk/gwas/api/search/downloads?q=text:*&pvalfilter=&orfilter=&betafilter=&datefilter=&traitfilter[]=&dateaddedfilter=";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(searchString);
        if (System.getProperty("http.proxyHost") != null) {
            HttpHost proxy;
            if (System.getProperty("http.proxyPort") != null) {
                proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty
                        ("http.proxyPort")));
            }
            else {
                proxy = new HttpHost(System.getProperty("http.proxyHost"));
            }
            httpGet.setConfig(RequestConfig.custom().setProxy(proxy).build());
        }

        try {
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println("Received HTTP response: " + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();

                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

                String output;
                while ((output = br.readLine()) != null) {

                    System.out.println(output);

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//        String output = request.

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
//    }

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
