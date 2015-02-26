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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * A GociPubMedDispatcher service that loads PubMed query strings from a properties file, application.properties. Upon
 * construction, this dispatcher is ready to dispatch requests manually,
 *
 * @author Tony Burdett Date 26/10/11
 *         <p>
 *         Adapted by Emma (2015-01-16) based on code written by Tony.
 */

@Service
@Component
public class PropertyFilePubMedLookupService implements GwasPubMedLookupService {
    @Value("${pubmed.xml.version}")
    private String xmlVersion; // xml version is very important here , it must be "&version=2.0"

    @Value("${pubmed.root}")
    private String pubmedRoot;

    @Value("${pubmed.gwas.summary}")
    private String pubmedGwasSummary;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Study dispatchSummaryQuery(String pubmedId) throws PubmedLookupException {

        String summaryString;
        if (pubmedRoot != null && pubmedGwasSummary != null) {
            summaryString = pubmedRoot.concat(pubmedGwasSummary);
        }
        else{
            throw new PubmedLookupException(
                    "Unable to search pubmed - no URL configured. " +
                            "Set pubmed.root, pubmed.gwas.summary and pubmed.xml.version in your config!");
        }

        Document response = null;

        // Run query and create study object
        try {
            response = dispatchSearch(summaryString.replace("{idlist}", pubmedId) + xmlVersion);

            NodeList docSumNodes = response.getElementsByTagName("DocumentSummary");

            // The document summary is present then we should have a publication
            if (docSumNodes.getLength() > 0) {
                Study newStudy = new Study();

                // Assuming here we only have one document summary as pubmed id should correspond to one publication
                Node docSumNode = docSumNodes.item(0);

                // Initialize study attributes
                String pmid = null;
                String title = "";
                Date pubDate = null;
                String author = "";
                String publication = "";


                // Get the ID element (should only be one, take first regardless)
                Element study = (Element) docSumNode;

                pmid = study.getAttribute("uid");

                if (study.getElementsByTagName("error").item(0) != null) {

                    author = null;
                    title = null;
                    publication = null;
                    pubDate = null;

                }
                else {

                    title = study.getElementsByTagName("Title").item(0).getTextContent();
                    publication = study.getElementsByTagName("Source").item(0).getTextContent();
                    author = study.getElementsByTagName("SortFirstAuthor").item(0).getTextContent();

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    String date = study.getElementsByTagName("SortPubDate").item(0).getTextContent();

                    if (date.contains("/")) {
                        date = date.replace("/", "-");
                    }

                    java.util.Date studyDate = null;
                    try {
                        studyDate = format.parse(date);
                    }
                    catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    pubDate = new Date(studyDate.getTime());
                }


                if (pmid != null) {

                    if (author != null && pubDate != null && publication != null && title != null) {

                        newStudy.setAuthor(author);
                        newStudy.setPubmedId(pmid);
                        newStudy.setPublication(publication);
                        newStudy.setTitle(title);
                        newStudy.setStudyDate(pubDate);
                    }
                }

                return newStudy;
            }
            else {
                throw new PubmedLookupException("Couldn't find pubmed id " + pubmedId + " in PubMed");
            }
        }
        catch (IOException e) {
            throw new PubmedLookupException("Couldn't find pubmed id " + pubmedId + " in PubMed", e);
        }

    }

    // Uses Entrez Programming Utilities (E-utilities) service to query Pubmed with supplied id

    private Document dispatchSearch(String searchString) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(searchString);

        // This will set our proxy
        if (System.getProperty("http.proxyHost") != null) {
            HttpHost proxy;
            if (System.getProperty("http.proxyPort") != null) {
                proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty
                        ("http.proxyPort")));
                getLog().info("Setting proxy  " + proxy);
            }
            else {
                proxy = new HttpHost(System.getProperty("http.proxyHost"));
                getLog().info("Setting proxy  " + proxy);
            }

            httpGet.setConfig(RequestConfig.custom().setProxy(proxy).build());
        }
        getLog().info("Fetching from " + searchString + " doing http get " + httpGet.toString());
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                HttpEntity entity = response.getEntity();
                try {
                    InputStream in = entity.getContent();
                    return parsePubmedResponse(in);
                }
                finally {
                    EntityUtils.consume(entity);
                }


            }
            else {
                throw new IOException(
                        "Could not obtain results from '" + searchString + "' due to an unknown communication problem");
            }

        }
    }

    private Document parsePubmedResponse(InputStream inputStream) throws IOException {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.parse(inputStream);
        }
        catch (SAXException e) {
            throw new IOException("Could not parse response from PubMed due to an exception reading content",
                                  e);
        }
        catch (ParserConfigurationException e) {
            throw new IOException("Could not parse response from PubMed due to an exception reading content",
                                  e);
        }
    }
}
