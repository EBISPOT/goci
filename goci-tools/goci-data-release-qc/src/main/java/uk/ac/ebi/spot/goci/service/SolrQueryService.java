package uk.ac.ebi.spot.goci.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.PublishedStudy;
import uk.ac.ebi.spot.goci.service.mail.SolrDataProcessingService;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by dwelter on 29/06/16.
 */
@Service
public class SolrQueryService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Value("${catalog.stats.file}")
    private Resource catalogStatsFile;

    @NotNull @Value("${search.server}")
    private URL server;


    public SolrQueryService(){

    }

    public List<PublishedStudy> getPublishedStudies() throws IOException{
        String lastReleaseDate = getLastReleaseDate();

        String query = buildSolrQuery(lastReleaseDate);

        HttpEntity entity = querySolr(query);

        List<PublishedStudy> studies = processSolrResult(entity);

        return studies;
    }


    public String getLastReleaseDate(){
        String releasedate;

        Properties properties = new Properties();
        try {
            properties.load(catalogStatsFile.getInputStream());
            releasedate = properties.getProperty("releasedate");
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Unable to return catolog stats: failed to read catalog.stats.file resource", e);
        }
        return releasedate;
    }

    public String buildSolrQuery(String releaseDate) throws IOException{

        StringBuilder solrSearchBuilder = new StringBuilder();
        solrSearchBuilder.append(server)
                .append("/select?");

        solrSearchBuilder.append("q=")
                .append("catalogPublishDate%3A%7B")
                .append(releaseDate)
                .append("T00%3A00%3A00Z+TO+*%5D");

        solrSearchBuilder.append("&fq=")
                .append("resourcename")
                .append("%3A")
                .append("study");

        solrSearchBuilder.append("&wt=json");

        return solrSearchBuilder.toString();
    }

    public HttpEntity querySolr(String searchString) throws IOException{

        System.out.println(searchString);
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

        HttpEntity entity = null;
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            getLog().debug("Received HTTP response: " + response.getStatusLine().toString());
            entity = response.getEntity();


        }
        return entity;
    }

    public List<PublishedStudy> processSolrResult(HttpEntity entity) throws IOException{
        List<PublishedStudy> studies = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

        String output;
        while ((output = br.readLine()) != null) {

            SolrDataProcessingService jsonProcessor = new SolrDataProcessingService(output);
            studies = jsonProcessor.processJson();
        }

        EntityUtils.consume(entity);

//        if (file == null) {
//
//            //TO DO throw exception here and add error handler
//            file =
//                    "Some error occurred during your request. Please try again or contact the GWAS Catalog team for assistance";
//        }

        return studies;
    }


    public Resource getCatalogStatsFile(){return catalogStatsFile;};

    public void setCatalogStatsFile(Resource csf){
        this.catalogStatsFile = csf;
    }

    public URL getServer() {
        return server;
    }

    public void setServer(URL server) {
        this.server = server;
    }


}
