package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexusFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class DummyPussycatSession implements PussycatSession {
    private String sessionID;
    private Collection<Renderlet> renderlets;
    private RenderletNexus renderletNexus;

    private Resource ontologyResource;
    private OWLOntology loadedData;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Resource getOntologyResource() {
        return ontologyResource;
    }

    public void setOntologyResource(Resource ontologyResource) {
        this.ontologyResource = ontologyResource;
    }

    public void init() {
        try {
            // load the ontology data from the supplied resource
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            IRI iri = IRI.create(getOntologyResource().getURI());
            this.loadedData = manager.loadOntologyFromOntologyDocument(iri);

            // set up this session
            this.sessionID = generateSessionID();
            this.renderletNexus = RenderletNexusFactory.createDefaultRenderletNexus();
            this.renderlets = getAvailableRenderlets();

            // register all renderlets
            for (Renderlet r : renderlets) {
                renderletNexus.register(r);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to initialize pussycat session", e);
        }
    }

    public String getSessionID() {
        return sessionID;
    }

    public OWLOntology getLoadedData() {
        return loadedData;
    }

    public RenderletNexus getRenderletNexus() {
        return renderletNexus;
    }

    public Collection<Renderlet> getAvailableRenderlets() {
        if (renderlets == null) {
            ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
            Collection<Renderlet> loadedRenderlets = new HashSet<Renderlet>();
            for (Renderlet renderlet : renderletLoader) {
                loadedRenderlets.add(renderlet);
            }
            return loadedRenderlets;
        }
        else {
            return renderlets;
        }
    }

    protected String generateSessionID() {
        String timestamp = Long.toString(System.currentTimeMillis());
        getLog().debug("Generating new session ID for session created at " + timestamp);
        try {
            // encode the email using SHA-1
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] digest = messageDigest.digest(timestamp.getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String restKey = getHexRepresentation(digest);
            getLog().debug("Session ID was generated: " + restKey);
            return restKey;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available, required to generate session ID");
        }
    }

    private static final String HEXES = "0123456789ABCDEF";

    protected String getHexRepresentation(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
