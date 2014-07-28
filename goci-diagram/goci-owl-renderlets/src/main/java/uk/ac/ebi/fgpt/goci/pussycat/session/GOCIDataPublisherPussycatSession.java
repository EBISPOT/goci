package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.lang.OWLAPIFilterInterpreter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.Association;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGBuilder;
import uk.ac.ebi.fgpt.goci.pussycat.reasoning.ReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome.ChromosomeRenderlet;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

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

    private Logger diagramLogger = LoggerFactory.getLogger("diagram.log");

    protected Logger getDiagramLogger() {
        return diagramLogger;
    }

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

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        OWLAPIFilterInterpreter interpreter = new OWLAPIFilterInterpreter(getOntologyConfiguration());
        OWLClassExpression classExpression = interpreter.interpretFilters(filters);
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
                String svg = renderletNexus.getSVG();
                svgCache.put(classExpression, svg);
                end = System.currentTimeMillis();
                double time = ((double) (end - start)) / 1000;
                getLog().info("Rendering complete in  " + time + " s.  " +
                                      "New SVG for '" + classExpression + "' added to cache");

//                //save SVG out as PNG
//                StringReader reader = new StringReader(svg);
//                String uri = "urn:gwas.svg";
//
//                String parser = XMLResourceDescriptor.getXMLParserClassName();
//                SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//                try {
//                    Document doc = f.createSVGDocument(uri, reader);
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }

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

    private synchronized String lazilyRenderDefaultSVG(final RenderletNexus renderletNexus, final OWLClassExpression classExpression)
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

                            // clear current rendering
                            renderletNexus.reset();

                            // run the rendering process
                            createSVG(renderletNexus, classExpression);

                            // retrieve cached, sorted SVG from the renderlet nexus
                            String svg = renderletNexus.getSVG();
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

    public void createSVG(RenderletNexus nexus, OWLClassExpression classExpression) {
        getDiagramLogger().info("Rendering diagram for OWLClassExpression " + classExpression);
//check if the chromosomes have already been rendered, otherwise render them
        SVGBuilder svgBuilder = new SVGBuilder();



        // get the ontology loaded into the reasoner
        OWLOntology ontology = reasoner.getRootOntology();

/****        SPARQL CONVERSION ****/
        OWLClass ta = manager.getOWLDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        getDiagramLogger().info("There are " + ta.getIndividuals(ontology).size() + " potential trait associations that could be rendered");

        getLog().trace("There are " + renderlets.size() + " registered renderlets");

        long start, end;
        start = System.currentTimeMillis();
        getLog().debug("Obtaining OWL individuals from reasoner");

/****        SPARQL CONVERSION ****/
        Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();
        end = System.currentTimeMillis();
        double time = ((double) (end - start)) / 1000;
        getLog().info("OWL individuals for OWLClassExpression " + classExpression + " acquired in " + time + " s.");

        getLog().debug("There are " + individuals.size() + " owl individuals that satisfy the expression " +
                               classExpression);

        if(renderedEntities.size() == 0){
//if the map is empty but OWLClassExpression isn't OWLThing, do a dummy rendering to rebuild the map
            if(!classExpression.isOWLThing()){
                getLog().debug("Empty maps and a subset to rendered - rebuild maps via dummy OWLThing rendering");
/****        SPARQL CONVERSION ****/
                OWLClassExpression everything = manager.getOWLDataFactory().getOWLThing();
                Set<OWLNamedIndividual> allIndividuals = reasoner.getInstances(everything,false).getFlattened();
                renderSVGFromScratch(svgBuilder, ontology, allIndividuals);
                getLog().trace("Rendering OWLClassExpression");
                svgBuilder = new SVGBuilder();
                renderFromExistingSVG(svgBuilder, ontology, individuals);
            }
            else {
                renderSVGFromScratch(svgBuilder, ontology, individuals);
                getLog().debug("Rendering complete");
            }
        }
        else{
            renderFromExistingSVG(svgBuilder, ontology, individuals);
            getLog().debug("Rendering complete");
        }

        return svgBuilder.getSVG();

    }

    public void renderChromosomes(SVGBuilder builder) {
/****        SPARQL CONVERSION ****/
        OWLOntology ontology = reasoner.getRootOntology();

        for (Renderlet r : renderlets) {
            if (r instanceof ChromosomeRenderlet) {
/****        SPARQL CONVERSION ****/
                OWLClass chromosome = ontology.getOWLOntologyManager()
                        .getOWLDataFactory()
                        .getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
                NodeSet<OWLClass> all = reasoner.getSubClasses(chromosome, true);
                Set<OWLClass> allChroms = all.getFlattened();

                for (OWLClass chrom : allChroms) {
                    if (r.canRender(this, ontology, chrom)) {
                        getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                        builder.addElement(r.render(this, ontology, chrom));
                    }
                }
            }
        }
    }

    /*TO DO: double-check this entire method because there may be lots wrong with it, especially with adding multipe gwas-trait classes*/
    public void renderFromExistingSVG(SVGBuilder builder, OWLOntology ontology, Set<OWLNamedIndividual> individuals){
        getLog().trace("Rendering from existing SVG");
        OWLClass chromosome = ontology.getOWLOntologyManager()
                .getOWLDataFactory()
                .getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));
        NodeSet<OWLClass> all = reasoner.getSubClasses(chromosome, true);
        Set<OWLClass> allChroms = all.getFlattened();

        getLog().trace("There are " + allChroms.size() + " chromosomes");

        for(OWLClass chrom : allChroms){
            getLog().trace("Trying to render chromosome " + chrom);
            if(getRenderingEvent(chrom) != null){
                Element element = getRenderingEvent(chrom).getRenderedSVG();
                builder.addElement(element);
                getLog().trace("Rendering successful");
            }
            else {
                getLog().trace("Trying to render a chromosome that doesn't exist");
            }
        }
        getLog().trace("Chromsomes rendered");

        HashMap<String, ArrayList<OWLNamedIndividual>> currentBands = new HashMap<String, ArrayList<OWLNamedIndividual>>();

        Set<String> allBands = bandLocations.keySet();

        getLog().trace("Building the local band map");
        for(String band : allBands){
            ArrayList<OWLNamedIndividual> renderedAssociations = bandLocations.get(band).getRenderedAssociations();
            ArrayList<OWLNamedIndividual> toRender = new ArrayList<OWLNamedIndividual>();

            for(OWLNamedIndividual association : individuals){
                if(renderedAssociations.contains(association)){
                    toRender.add(association);
                }
            }
            if(toRender.size() != 0){
                currentBands.put(band,toRender);
            }
        }

        Set<String> bands = currentBands.keySet();

        getLog().trace("Starting to gather SVG elements");
        for(String band : bands){
            getLog().trace("Processing band " + band);
            int index = 1;
            Element associationSVG = null;
            ArrayList<Element> traits = new ArrayList<Element>();
            getLog().trace("There are " +currentBands.get(band).size() + " associations");
            for(OWLNamedIndividual association : currentBands.get(band)){
                if(index == 1){
                    getLog().trace("First association");
                    associationSVG = getRenderingEvent(association).getRenderedSVG();
                    associationSVG.setAttribute("class", "gwas-trait");
                    index++;
                }

                String assocIRI = OntologyUtils.getShortForm(association.getIRI(), ontology);
                OWLNamedIndividual trait = getTrait(association, ontology);
                if(trait != null){
                    getLog().debug("Rendering trait " + trait);
                    String name = getTraitName(trait,ontology,association);
                    boolean queued = false;

//if the SVG for this trait has already been queued for this band...
                    for(Element rendered : traits){
                        if(rendered.getAttribute("gwasname").equals(name)){
                            getLog().debug("SVG for this trait name is already queued for rendering");
//                            String mouseclick = rendered.getAttribute("onclick");
//                            rendered.setAttribute("onclick", mouseclick + "," + assocIRI);

                            String existing = rendered.getAttribute("gwasassociation");
                            rendered.setAttribute("gwasassociation", existing + "," + assocIRI);
                            IRI traitIri = getTraitClass(trait, ontology);
                            String traitClass = OntologyUtils.getShortForm(traitIri, ontology);
                            String existingClass = rendered.getAttribute("class");
                            rendered.setAttribute("class", existingClass + " " + traitClass);

                            queued = true;
                        }
                    }


                    if(!queued){
//scenario 1: this trait individual was rendered in the full rendering
                        if(getRenderingEvent(trait) != null){
                            Element traitSVG = getRenderingEvent(trait).getRenderedSVG();
//                            traitSVG.setAttribute("onclick", "showSummary(" + assocIRI);
                            traitSVG.setAttribute("gwasassociation", assocIRI);
                            traits.add(traitSVG);
                        }
//scenario 2: this trait individual was not rendered in the full rendering --> find a trait of the same name that was
                        else{
                            getLog().debug("Trait " + trait + " was not explicitly rendered in the full diagram");
                            if(bandLocations.get(band).getRenderedTraits().contains(name)){
                                OWLNamedIndividual rep = bandLocations.get(band).getRenderedTrait(name);
                                if(getRenderingEvent(rep) != null){
                                    Element traitSVG = getRenderingEvent(rep).getRenderedSVG();
//                                    traitSVG.setAttribute("onclick", "showSummary(" + assocIRI);
                                    traitSVG.setAttribute("gwasassociation", assocIRI);
                                    IRI traitIri = getTraitClass(rep, ontology);
                                    String traitClass = OntologyUtils.getShortForm(traitIri, ontology);
                                    String existingClass = traitSVG.getAttribute("class");
                                    traitSVG.setAttribute("class", existingClass + " " + traitClass);
                                    traits.add(traitSVG);
                                }

                            }
                        }
                    }
                    IRI traitIRI = getTraitClass(trait, ontology);
                    String traitClass = OntologyUtils.getShortForm(traitIRI, ontology);

                    if(associationSVG != null){
                        String existingClass = associationSVG.getAttribute("class");
                        associationSVG.setAttribute("class", existingClass + " " + traitClass);
                    }
                    else{
                        getLog().debug("Could not add CSS class element for association " + association);
                    }

                }
            }
            builder.addElement(associationSVG);

            for(Element svg : traits){
//                String onclick = svg.getAttribute("onclick");
//                svg.setAttribute("onclick", onclick + "')");

                builder.addElement(svg);
            }
        }
    }

    public void renderSVGFromScratch(SVGBuilder svgBuilder, OWLOntology ontology, Set<OWLNamedIndividual> individuals){

        renderChromosomes(svgBuilder);

        int assocCount=0;
        int dotCount=0;

        ArrayList<String> renderingOrder =  buildTraitMap(individuals, ontology);
        getLog().debug("Starting rendering of trait associations");
        for(String band : renderingOrder){
            ArrayList<Association> assocs = bandLocations.get(band).getAssociations();

            Element associationSVG = null;
            ArrayList<Element> traits = new ArrayList<Element>();

            for(Association assoc : assocs){
                OWLNamedIndividual ind = assoc.getAssociation();
                if(individuals.contains(ind)){
                    for (Renderlet ra : renderlets) {
                        if (ra.canRender(this, ontology, ind)) {
                            getLog().trace("Dispatching render() request to renderlet '" + ra.getName() + "'");

                            Element current =  ra.render(this, ontology, ind, svgBuilder);

                            if(current != null){
                                associationSVG = current;
                            }

                            OWLNamedIndividual trait = getTrait(ind, ontology);
                            if(trait != null){
                                getLog().trace("Trait: " + trait);

                                if(!allTraits.contains(trait))       {
                                    getLog().debug("This is probably a trait association!" + trait);
                                }

                                else{
                                    String traitName = getTraitName(trait, ontology, ind);

                                    if(bandLocations.get(band).getRenderedTraits().contains(traitName)){
                                        getLog().trace("Trait " + traitName + " already rendered at band " + band);
                                        String assocIRI = OntologyUtils.getShortForm(ind.getIRI(), ontology);
                                        for(Element rendered : traits){
                                            if(rendered.getAttribute("gwasname") != null) {
                                                if(rendered.getAttribute("gwasname").equals(traitName)){
                                                    String existing = rendered.getAttribute("gwasassociation");
                                                    rendered.setAttribute("gwasassociation", existing + "," + assocIRI);

                                                    IRI traitIri = getTraitClass(trait, ontology);
                                                    String traitClass = OntologyUtils.getShortForm(traitIri, ontology);

                                                    if(!traitClass.contains("gwas-diagram")){
                                                        String existingClass = rendered.getAttribute("class");

                                                        if(!existingClass.contains(traitClass)){
                                                            rendered.setAttribute("class", existingClass + " " + traitClass);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        getLog().trace("Trait " + traitName + " was not previously rendered at band " + band);
                                        for (Renderlet rt : getRenderlets()){
                                            if (rt.canRender(this, ontology, trait)) {
                                                getLog().trace("Dispatching render() request to renderlet '" + rt.getName() + "'");
                                                Element newTrait = rt.render(this, ontology, trait, svgBuilder);
                                                if(newTrait != null){
                                                    traits.add(newTrait);
                                                    IRI traitIRI = getTraitClass(trait, ontology);
                                                    String traitClass = OntologyUtils.getShortForm(traitIRI, ontology);

                                                    if(associationSVG != null){
                                                        String existingClass = associationSVG.getAttribute("class");
                                                        associationSVG.setAttribute("class", existingClass + " " + traitClass);
                                                    }
                                                    else{
                                                        getLog().debug("Could not add CSS class element for association " + ind);
                                                    }
                                                }
                                                else{
                                                    getLog().debug("No SVG was returned for trait " + trait);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                getLog().debug("The trait for association " + ind + " is null");
                            }
                        }
                    }

                }
            }
            svgBuilder.addElement(associationSVG);

            for(Association assoc : assocs){
                getRenderingEvent(assoc.getAssociation()).updateRenderedSVG(associationSVG);
            }
            getDiagramLogger().info("Number of traits for band " + band + ": " + traits.size());

            for(Element trait : traits){
                String name = trait.getAttribute("gwasname");
                String[] subs = trait.getAttribute("gwasassociation").split(",");

                getDiagramLogger().info("Trait " + name + " contains " + subs.length + " associations");
                dotCount = dotCount+1;
                assocCount = assocCount + subs.length;
                svgBuilder.addElement(trait);
            }
        }

        getDiagramLogger().info("There are " + dotCount + " dots representing " + assocCount + " associations on this diagram");
    }
}
