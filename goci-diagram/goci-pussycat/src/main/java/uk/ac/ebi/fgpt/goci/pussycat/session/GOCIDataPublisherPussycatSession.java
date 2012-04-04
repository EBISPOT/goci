package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class GOCIDataPublisherPussycatSession implements PussycatSession {
    private String sessionID;
    private Collection<Renderlet> renderlets;

    private ReasonerSession reasonerSession;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public GOCIDataPublisherPussycatSession() {
        // set up this session
        this.sessionID = generateSessionID();
        this.renderlets = getAvailableRenderlets();

    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public void setReasonerSession(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    public String getSessionID() {
        return sessionID;
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

    public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {

        String svgString = null;
        try {
            svgString = renderletNexus.getSVG(classExpression, getReasoner());
        } catch (OWLConversionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return svgString;
    }

    public boolean clearRendering() {
        return false;
    }

    public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
        if (getReasonerSession().isReasonerInitialized()) {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is fully initialized and ready to serve data");
            return getReasonerSession().getReasoner();
        }
        else {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is not yet initialized - waiting for reasoner");
            throw new PussycatSessionNotReadyException("Reasoner is being initialized");
        }
    }

    public Set<OWLNamedIndividual> query(OWLClassExpression classExpression)
            throws OWLConversionException, PussycatSessionNotReadyException {
        getLog().debug("Searching reasoner for instances of " + classExpression.toString());
        return getReasoner().getInstances(classExpression, false).getFlattened();
    }

    private String generateSessionID() {
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

    private String getHexRepresentation(byte[] raw) {
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
