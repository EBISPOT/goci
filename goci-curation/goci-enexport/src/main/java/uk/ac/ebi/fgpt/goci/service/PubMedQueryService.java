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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.fgpt.goci.exception.DispatcherException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 05/06/13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class PubMedQueryService {

    private HttpContext httpContext;
    private HttpClient httpClient;

    private String fetchString;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public PubMedQueryService(){
        this.httpContext = new BasicHttpContext();
        this.httpContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
        this.httpClient = new DefaultHttpClient();

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResource("pubmed.properties").openStream());
            this.fetchString = properties.getProperty("pubmed.root")
                    .concat(properties.getProperty("pubmed.gwas.fetch"));
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

    public String dispatchFetchQuery(Collection<String> pubmedIDs) throws DispatcherException {
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

            Document response = doPubmedQuery(URI.create(fetchString.replace("{idlist}", idList)));

            return response.toString();

        }
        catch (IOException e) {
            throw new DispatcherException("Communication problem with PubMed", e);
        }
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
