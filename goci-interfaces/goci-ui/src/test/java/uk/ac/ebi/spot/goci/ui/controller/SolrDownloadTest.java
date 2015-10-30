package uk.ac.ebi.spot.goci.ui.controller;

import org.apache.http.HttpEntity;
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
import java.io.PrintWriter;

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

        String searchString = "http://www.ebi.ac.uk/gwas/api/search/downloads?q=text:*&pvalfilter=&orfilter=&betafilter=&datefilter=&traitfilter[]=&dateaddedfilter=";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(searchString);

        try {
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println("Received HTTP response: " + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();

                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

                PrintWriter outputWriter = new PrintWriter("DownloadTestOutput.csv");

                String output;
                int counter = 0;
                while ((output = br.readLine()) != null) {
                    counter++;
                    outputWriter.write(output);
                }
                System.out.println(counter + " lines were retrieved via this query");
                outputWriter.flush();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
