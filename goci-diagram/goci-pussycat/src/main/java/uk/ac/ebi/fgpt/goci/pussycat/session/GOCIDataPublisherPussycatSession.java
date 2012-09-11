package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.w3c.dom.Document;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * A pussycat session that uses a {@link ReasonerSession} created from a GOCI DataPublisher to obtain the data to
 * render.  This session requires that a reasoner is created, pre-classifie and held in memory for the life of the
 * application.  For large knowledge bases this represents a significant amount of memory and computational resources
 * and a significant start up time, so it may not be wise to use this in live, production instances.
 *
 * @author Tony Burdett
 * @date 01/03/12
 * @see GOCIDataPublisherPussycatSession
 * @see uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher
 */
public class GOCIDataPublisherPussycatSession extends AbstractPussycatSession {
    private Collection<Renderlet> renderlets;

    private ReasonerSession reasonerSession;
    private OntologyConfiguration ontologyConfiguration;

    private Map<OWLClassExpression, String> svgCache;

    public GOCIDataPublisherPussycatSession() {
        super();

        // set up this session
        this.renderlets = getAvailableRenderlets();

        // setup a cache to retain SVG documents by OWLClassExpression
        // this means that if different sessions request SVG for the same class expression, we can reuse
        this.svgCache = new HashMap<OWLClassExpression, String>();
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public void setReasonerSession(ReasonerSession reasonerSession) {
        this.reasonerSession = reasonerSession;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    public Collection<Renderlet> getAvailableRenderlets() {
        if (renderlets == null) {
            ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
            Collection<Renderlet> loadedRenderlets = new HashSet<Renderlet>();
            for (Renderlet renderlet : renderletLoader) {
                loadedRenderlets.add(renderlet);
            }
            getLog().debug("Loaded " + loadedRenderlets.size() + " renderlets");
            return loadedRenderlets;
        }
        else {
            return renderlets;
        }
    }

    public String performRendering(OWLClassExpression classExpression, RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {
        if (classExpression.isOWLThing()) {
            // is this a request for OWL:Thing? If so, return default SVG
            getLog().debug("Received render request for OWL:Thing, dispatching to default rendering function.");
            return lazilyRenderDefaultSVG(renderletNexus);
        }
        else {
            // otherwise render the SVG for this request
            if (svgCache.containsKey(classExpression)) {
                getLog().debug("PussycatSession '" + getSessionID() + "' can serve up pre-rendered SVG " +
                                       "for '" + classExpression + "'");
                return svgCache.get(classExpression);
            }
            else {
                getLog().info("Novel request: rendering SVG representing '" + classExpression + "'...");
                long start, end;
                start = System.currentTimeMillis();
                String svg = renderletNexus.getSVG(classExpression);
                svgCache.put(classExpression, svg);
                end = System.currentTimeMillis();
                double time = ((double) (end - start)) / 1000;
                getLog().info("Rendering complete in  " + time + " s.  " +
                                      "New SVG for '" + classExpression + "' added to cache");

                //save SVG out as PNG
                StringReader reader = new StringReader(svg);
                String uri = "urn:gwas.svg";

                String parser = XMLResourceDescriptor.getXMLParserClassName();
                SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
                try {
                    Document doc = f.createSVGDocument(uri, reader);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return svg;
            }
        }
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

    private String initialRender = null;
    private boolean initialRenderStarted, initialRenderComplete;
    private Exception defaultSVGException;

    private synchronized String getInitialRender() throws Exception {
        if (defaultSVGException == null) {
            return initialRender;
        }
        else {
            throw defaultSVGException;
        }
    }

    private synchronized void startInitialRender() {
        this.initialRenderStarted = true;
    }

    private synchronized boolean isInitialRenderStarted() {
        return initialRenderStarted;
    }

    private synchronized void completeInitialRender(String svg) {
        this.initialRender = svg;
        this.initialRenderComplete = true;
    }

    private synchronized void completeInitialRender(Exception e) {
        this.defaultSVGException = e;
        this.initialRenderComplete = true;
    }

    private synchronized boolean isInitialRenderComplete() {
        return initialRenderComplete;
    }

    private synchronized String lazilyRenderDefaultSVG(final RenderletNexus renderletNexus)
            throws PussycatSessionNotReadyException {
        // have we completed the initial rendering yet?
        if (isInitialRenderComplete()) {
            try {
                getLog().debug("PussycatSession '" + getSessionID() + "' can serve up pre-rendered default SVG");
                return getInitialRender();
            }
            catch (Exception e) {
                getLog().error("PussycatSession '" + getSessionID() + "' encountered prior rendering errors");
                throw new PussycatSessionNotReadyException("Failed to create GWAS diagram (" + e.getMessage() + ")", e);
            }
        }
        else {
            // not initialized yet, have we already started rendering?
            if (isInitialRenderStarted()) {
                // already initializing, throw an exception and expect retry later
                getLog().debug("Pussycat Session '" + getSessionID() + "' is still performing initial SVG rendering");
                throw new PussycatSessionNotReadyException("GWAS diagram is being calculated");
            }
            else {
                // start initial render
                startInitialRender();

                // grab a reference to OWLThing
                final OWLClassExpression thing = getOntologyConfiguration().getOWLDataFactory().getOWLThing();

                // create new thread to do initialization
                new Thread((new Runnable() {
                    public void run() {
                        // do initial SVG rendering
                        try {
                            getLog().info("Rendering default SVG representing '" + thing + "'...");
                            long start, end;
                            start = System.currentTimeMillis();
                            String svg = renderletNexus.getSVG(thing);
                            end = System.currentTimeMillis();
                            double time = ((double) (end - start)) / 1000;
                            getLog().info("Default SVG rendering complete in  " + time + " s.");
                            completeInitialRender(svg);
                        }
                        catch (Exception e) {
                            // log error and store exception
                            getLog().error("Failed to render default SVG", e);
                            completeInitialRender(e);
                        }
                    }
                })).start();

                // started initializing, throw an exception and expect retry later
                getLog().debug("Pussycat Session '" + getSessionID() + "' is still performing initial SVG rendering");
                throw new PussycatSessionNotReadyException("Started GWAS diagram calculation");
            }
        }
    }
}
