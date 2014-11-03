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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;
import uk.ac.ebi.fgpt.goci.model.DefaultGwasStudy;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * A GociPubMedDispatcher service that loads PubMed query strings from a properties file, pubmed.properties.  Upon
 * construction, this dispatcher is ready to dispatch requests manually, but will only do so periodically once the
 * startDispatcher() method is called.  The startDispatcher() method does a limited amount of initialisation, only
 * ensuring that queries are dispatched regularly.  If you are using this service to dispatch queries manually, you do
 * not need to call startDispatcher().
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public class PropertyFilePubMedDispatcherService implements GwasPubMedDispatcherService {
    public static final int PUBMED_MAXRECORDS = 1000;

    private HttpContext httpContext;
    private HttpClient httpClient;

    private String searchString;
    private String summaryString;
    private String fetchString;
    private String dateProp;
    private String xmlVersion;



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
            this.dateProp = properties.getProperty("pubmed.gwas.search.time");
            this.xmlVersion = properties.getProperty("pubmed.xml.version");
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



    public Collection<String> dispatchSearchQuery() throws DispatcherException {
        try {
            // int some fields for looping over results
            int start = 0, count = -1;
            Collection<String> results = new HashSet<String>();

            String limitParams = "&retmax=" + PUBMED_MAXRECORDS + "&restart=" + start;
            String dateParams = "&reldate=".concat(dateProp) + "&datetype=pdat";
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

    public Map<String, GwasStudy> dispatchSummaryQuery(Collection<String> pubmedIDs) throws DispatcherException {
        Map<String, GwasStudy> results = new HashMap<String, GwasStudy>();

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

            Document response = doPubmedQuery(URI.create(summaryString.replace("{idlist}", idList) + xmlVersion));

            getLog().info("Pubmed query is " + summaryString.replace("{idlist}", idList) + xmlVersion);

            NodeList docSumNodes = response.getElementsByTagName("DocumentSummary");

            //NodeList docSumNodes = response.getFirstChild().getChildNodes();

            for (int i = 0; i < docSumNodes.getLength(); i++) {
                Node docSumNode = docSumNodes.item(i);

                // init id, title strings for each docSumNode
                String pmid = null;
                String title = "";
                Date pubDate = null;
                String author = "";
                String publication = "";


                // get the ID element (should only be one, take first regardless

                Element study = (Element) docSumNode;

                pmid = study.getAttribute("uid");

                title = study.getElementsByTagName("Title").item(0).getTextContent();
                publication = study.getElementsByTagName("Source").item(0).getTextContent();
                author = study.getElementsByTagName("SortFirstAuthor").item(0).getTextContent();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                String date = study.getElementsByTagName("SortPubDate").item(0).getTextContent();

                if(date.contains("/")){
                    date = date.replace("/","-");
                }

                java.util.Date studyDate = format.parse(date);


                pubDate = new java.sql.Date(studyDate.getTime());


//                study.getElementsByTagName()

//                Collection<Node> items = getChildNodes(docSumNode, "Item");
//                for (Node item : items) {
//                    NamedNodeMap attributes = item.getAttributes();
//                    if (attributes.getNamedItem("Name").getNodeValue().equals("Title")) {
//                        // should only be one <Item Name="Title">, this will overwrite if more
//                        title = item.getTextContent();
//                        getLog().trace("PubMed ID: " + id + "; Title: " + title);
//                    }
//                    else if(attributes.getNamedItem("Name").getNodeValue().equals("Source")) {
//                        publication = item.getTextContent();
//                    }
//                    else if(attributes.getNamedItem("Name").getNodeValue().equals("Authors")) {
//                        author = item.getFirstChild().getFirstChild().getTextContent();
//                    }
//                    else if(attributes.getNamedItem("Name").getNodeValue().equals("PubDate")) {
//                        //         pubDate = Date.valueOf(item.getTextContent());
//
//
//                        pubDate = DateFormat.getDateInstance().parse(item.getTextContent());
//                    }
//
//                }

//                        // add results
                if (pmid != null) {
                    GwasStudy newStudy = new DefaultGwasStudy(pmid, author, pubDate, publication, title);

                    results.put(pmid, newStudy);
                }
            }
        }
        catch (IOException e) {
            throw new DispatcherException("Communication problem with PubMed", e);

        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return results;

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



    public void addPubMedID(String pubmedID) throws DispatcherException {
        getLog().debug("Dispatching query for PubMed ID '" + pubmedID + "'...");

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
