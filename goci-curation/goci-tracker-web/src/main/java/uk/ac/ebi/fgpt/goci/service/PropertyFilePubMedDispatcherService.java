package uk.ac.ebi.fgpt.goci.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.factory.GociStudyFactory;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.service.job.PubMedSearchJob;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;


/**
 * A GociPubMedDispatcher service that loads PubMed query strings from a properties file, pubmed.properties.  Upon
 * construction, this dispatcher is ready to dispatch requests manually, but will only do so periodically once the
 * startDispatcher() method is called.  The startDispatcher() method does a limited amount of initialisation, only
 * ensuring that queries are dispatched regularly.  If you are using this service to dispatch queries manually, you do
 * not need to call startDispatcher().
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public class PropertyFilePubMedDispatcherService implements GociPubMedDispatcherService {
    public static final int PUBMED_MAXRECORDS = 100;

    private HttpContext httpContext;
    private HttpClient httpClient;

    private String searchString;
    private String summaryString;
    private String fetchString;

    private int queryEvery;

    private Scheduler scheduler;

    private GociStudyFactory studyFactory;
    private GociTrackerService trackerService;

    private Logger log = LoggerFactory.getLogger(getClass());

    public PropertyFilePubMedDispatcherService() {
        this.httpContext = new BasicHttpContext();
        this.httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        this.httpClient = new DefaultHttpClient();

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResource("pubmed.properties").openStream());
            this.searchString = properties.getProperty("pubmed.root")
                    .concat(properties.getProperty("pubmed.gwas.search"))
                    .concat(properties.getProperty("pubmed.gwas.search.terms"));
            this.summaryString = properties.getProperty("pubmed.root")
                    .concat(properties.getProperty("pubmed.gwas.summary"));
            this.fetchString = properties.getProperty("pubmed.root")
                    .concat(properties.getProperty("pubmed.gwas.fetch"));
            this.queryEvery = Integer.parseInt(properties.getProperty("pubmed.query.interval.hours"));
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: failed to read pubmed.properties resource", e);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(
                    "Unable to create dispatcher service: you must provide a integer query interval " +
                            "in minutes (pubmed.query.interval.mins)", e);
        }
    }

    protected Logger getLog() {
        return log;
    }

    public GociStudyFactory getStudyFactory() {
        return studyFactory;
    }

    public void setStudyFactory(GociStudyFactory studyFactory) {
        this.studyFactory = studyFactory;
    }

    public GociTrackerService getTrackerService() {
        return trackerService;
    }

    public void setTrackerService(GociTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    public Collection<String> dispatchSearchQuery() throws DispatcherException {
        try {
            // int some fields for looping over results
            int start = 0, count = -1;
            Collection<String> results = new HashSet<String>();

            String limitParams = "&retmax=" + PUBMED_MAXRECORDS + "&retstart=" + start;
            String dateParams = "&reldate=30";
            getLog().debug("PubMed query is '" + searchString + dateParams + limitParams + "'");
            Document response = doPubmedQuery(URI.create(searchString + dateParams + limitParams));

            // parse resulting xml to discover count - we might need to loop in increments
            NodeList countNodes = response.getElementsByTagName("Count");
            // get total number of records
            count = Integer.parseInt(countNodes.item(0).getTextContent());
            getLog().debug("Total number of records in this query: " + count);

            if (count == -1) {
                throw new DispatcherException("Could not get number of records from PubMed");
            }

            int it = 1;
            while (start < count) {
                // redo query with limits until we reach the end
                limitParams = "&retmax=" + PUBMED_MAXRECORDS + "&retstart=" + start;
                getLog().debug("Iteration " + it + ": " + searchString + dateParams + limitParams);
                response = doPubmedQuery(URI.create(searchString + dateParams + limitParams));

                NodeList idNodes = response.getElementsByTagName("Id");
                for (int i = 0; i < idNodes.getLength(); i++) {
                    Node idNode = idNodes.item(i);
                    results.add(idNode.getTextContent());
                }

                // increment start point
                start += PUBMED_MAXRECORDS;
                it++;
            }

            getLog().debug("Acquired " + results.size() + " PubMed IDs OK!");
            return results;
        }
        catch (IOException e) {
            throw new DispatcherException("Communication problem with PubMed", e);
        }
    }

    public Map<String, String> dispatchSummaryQuery(Collection<String> pubmedIDs) throws DispatcherException {
        try {
            // convert set of supplied pubmed IDs to comma separated list
            String idList = "";
            Iterator<String> pubmedIdIterator = pubmedIDs.iterator();
            while (pubmedIdIterator.hasNext()) {
                idList += pubmedIdIterator.next();
                if (pubmedIdIterator.hasNext()) {
                    idList += ",";
                }
            }

            Map<String, String> results = new HashMap<String, String>();
            Document response = doPubmedQuery(URI.create(summaryString.replace("{idlist}", idList)));
            NodeList docSumNodes = response.getElementsByTagName("DocSum");
            for (int i = 0; i < docSumNodes.getLength(); i++) {
                Node docSumNode = docSumNodes.item(i);

                // init id, title strings for each docSumNode
                String id = null;
                String title = "";

                // get the ID element (should only be one, take first regardless
                id = getChildNodes(docSumNode, "Id").iterator().next().getTextContent();

                Collection<Node> items = getChildNodes(docSumNode, "Item");
                for (Node item : items) {
                    NamedNodeMap attributes = item.getAttributes();
                    if (attributes.getNamedItem("Name").getNodeValue().equals("Title")) {
                        // should only be one <Item Name="Title">, this will overwrite if more
                        title = item.getTextContent();
                        getLog().trace("PubMed ID: " + id + "; Title: " + title);
                    }
                }

                // add results
                if (id != null && title != null) {
                    results.put(id, title);
                }
            }
            return results;
        }
        catch (IOException e) {
            throw new DispatcherException("Communication problem with PubMed", e);
        }
    }

    public Map<String, String> dispatchFetchQuery(Collection<String> pubmedIDs) throws DispatcherException {
        try {
            // convert set of supplied pubmed IDs to comma separated list
            String idList = "";
            Iterator<String> pubmedIdIterator = pubmedIDs.iterator();
            while (pubmedIdIterator.hasNext()) {
                idList += pubmedIdIterator.next();
                if (pubmedIdIterator.hasNext()) {
                    idList += ",";
                }
            }

            Map<String, String> results = new HashMap<String, String>();
            Document response = doPubmedQuery(URI.create(fetchString.replace("{idlist}", idList)));
            NodeList pubmedArticleNodes = response.getElementsByTagName("PubmedArticle");
            for (int i = 0; i < pubmedArticleNodes.getLength(); i++) {
                Node pubmedArticleNode = pubmedArticleNodes.item(i);

                // init id, abstract strings for each articleNode
                String id = null;
                String abstractText = "";

                for (Node medLineCitation : getChildNodes(pubmedArticleNode, "MedlineCitation")) {
                    // should be exactly one <PMID> node, take first regardless
                    id = getChildNodes(medLineCitation, "PMID").iterator().next().getTextContent();

                    for (Node article : getChildNodes(medLineCitation, "Article")) {
                        for (Node abstractNode : getChildNodes(article, "Abstract")) {
                            for (Node abstractTextNode : getChildNodes(abstractNode, "AbstractText")) {
                                NamedNodeMap attributes = abstractTextNode.getAttributes();
                                if (attributes.getNamedItem("Label") != null) {
                                    abstractText +=
                                            attributes.getNamedItem("Label").getNodeValue() + ": ";
                                }
                                abstractText += abstractTextNode.getTextContent();
                            }
                            getLog().trace("PubMed ID: " + id + "; Abstract: " + abstractText);
                        }
                    }
                }

                // add results
                if (id != null && abstractText != null) {
                    results.put(id, abstractText);
                }
            }
            return results;
        }
        catch (IOException e) {
            throw new DispatcherException("Communication problem with PubMed", e);
        }
    }

    public void startDispatcher() throws DispatcherException {
        try {
            SchedulerFactory schedFact = new StdSchedulerFactory();
            scheduler = schedFact.getScheduler();
            scheduler.start();

            // define the job and tie it to PubMedSearchJob
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("dispatcherService", this);
            dataMap.put("trackerService", getTrackerService());
            dataMap.put("studyFactory", getStudyFactory());

            JobDetail job = JobBuilder.newJob(PubMedSearchJob.class)
                    .withIdentity("pubmedSearch", "defaultGroup")
                    .usingJobData(dataMap)
                    .build();

            // Trigger the job to run now, and then every 24 hours
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("pubmedSearchTrigger", "defaultGroup")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                          .withIntervalInHours(queryEvery)
                                          .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e) {
            throw new DispatcherException("Could not start dispatcher", e);
        }
    }

    public void stopDispatcher() throws DispatcherException {
        try {
            scheduler.shutdown();
        }
        catch (SchedulerException e) {
            throw new DispatcherException("Scheduler shutdown failed: could not stop dispatcher", e);
        }
    }

    public void addPubMedID(String pubmedID) throws DispatcherException {
        getLog().debug("Dispatching query for PubMed ID '" + pubmedID + "'...");
        Map<String, String> titlesMap = dispatchSummaryQuery(Collections.singleton(pubmedID));
        Map<String, String> abstractsMap = dispatchFetchQuery(Collections.singleton(pubmedID));
        GociStudy study = getStudyFactory().createStudy(pubmedID, titlesMap.get(pubmedID), abstractsMap.get(pubmedID));
        getTrackerService().enterStudy(study);
        getLog().debug("Study ID '" + pubmedID + "' was manually requested and has been entered into tracking system");
    }

    private Document doPubmedQuery(URI queryUri) throws IOException {
        HttpGet httpGet = new HttpGet(queryUri);
        HttpResponse response = httpClient.execute(httpGet, httpContext);
        HttpEntity entity = response.getEntity();
        InputStream entityIn = entity.getContent();
        try {
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                try {
                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    return db.parse(entityIn);
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
            else {
                throw new IOException(
                        "Could not obtain results from '" + queryUri + "' due to an unknown communication problem");
            }
        }
        finally {
            entityIn.close();
        }
    }

    private Collection<Node> getChildNodes(Node parent, String tagName) {
        Collection<Node> nodes = new HashSet<Node>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(tagName)) {
                nodes.add(child);
            }
        }
        return nodes;
    }
}
