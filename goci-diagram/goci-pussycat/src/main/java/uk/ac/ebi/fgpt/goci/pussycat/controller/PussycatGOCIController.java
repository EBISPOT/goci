package uk.ac.ebi.fgpt.goci.pussycat.controller;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatSessionManager;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexusFactory;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * A MVC controller for Pussycat.  This controller can be used to create a new session, load ontology data and create
 * the SVG canvas Pussycat will display.
 *
 * @author Tony Burdett Date 27/02/12
 */
@Controller
@RequestMapping("/views")
public class PussycatGOCIController {
    private PussycatSessionStrategy sessionStrategy;
    private PussycatSessionManager pussycatManager;

    private Map<HttpSession, RenderletNexus> nexusMap = new HashMap<HttpSession, RenderletNexus>();

    private OntologyConfiguration ontologyConfiguration;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public PussycatSessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    @Autowired
    public void setSessionStrategy(PussycatSessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public PussycatSessionManager getPussycatManager() {
        return pussycatManager;
    }

    @Autowired
    public void setPussycatSessionManager(PussycatSessionManager pussycatManager) {
        this.pussycatManager = pussycatManager;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    @Autowired
    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    @RequestMapping(params = "clear")
    public @ResponseBody boolean clearRendering(HttpSession session) {
        return getPussycatSession(session).clearRendering();
    }

    @RequestMapping(value = "/gwasdiagram")
    public @ResponseBody String renderGWASDiagram(HttpSession session) throws PussycatSessionNotReadyException {
        // get OWLThing, to indicate that we want to draw all data in the GWAS catalog
        OWLClass thingCls = getOntologyConfiguration().getOWLDataFactory().getOWLThing();
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(thingCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/gwasdiagram/timeseries/{year}/{month}")
    public @ResponseBody String renderGWASDiagramTimeSeries(@PathVariable String year,
                                                            @PathVariable String month,
                                                            HttpSession session)
            throws PussycatSessionNotReadyException {
        // get OWLThing, to indicate that we want to draw all data in the GWAS catalog
        OWLClassExpression timeCls = getOntologyConfiguration().getOWLDataFactory().getOWLThing();
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(timeCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/gwasdiagram/pre2009")
    public @ResponseBody String renderGWASDiagramBefore2009(HttpSession session)
            throws PussycatSessionNotReadyException, ParserException {
        // get the subset of studies published before 2009
        getLog().debug("Received a new rendering request - putting together the query");
        OWLOntologyManager manager = getOntologyConfiguration().getOWLOntologyManager();
        OWLDataFactory df = getOntologyConfiguration().getOWLDataFactory();

        List<OWLAnnotationProperty> properties = Arrays.asList(df.getRDFSLabel());
        AnnotationValueShortFormProvider annoSFP = new AnnotationValueShortFormProvider(
                properties, new HashMap<OWLAnnotationProperty, List<String>>(), manager);
        ShortFormEntityChecker checker = new ShortFormEntityChecker(
                new BidirectionalShortFormProviderAdapter(manager, manager.getOntologies(), annoSFP));
        ManchesterOWLSyntaxClassExpressionParser parser = new ManchesterOWLSyntaxClassExpressionParser(df, checker);

        String date = "has_publication_date some dateTime[< \"2009-01-01T00:00:00+00:00\"^^dateTime]";

        OWLClassExpression pre2009 = parser.parse(date);

        OWLClass study = df.getOWLClass(IRI.create(OntologyConstants.STUDY_CLASS_IRI));
        OWLClassExpression pre2009_studies = df.getOWLObjectIntersectionOf(study, pre2009);

        OWLObjectProperty part_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.PART_OF_IRI));
        OWLObjectSomeValuesFrom part_of_assoc = df.getOWLObjectSomeValuesFrom(part_of, pre2009_studies);

        OWLClass association = df.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        OWLClassExpression trait_associations = df.getOWLObjectIntersectionOf(association, part_of_assoc);

        OWLObjectProperty has_about = df.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_ABOUT_IRI));
        OWLObjectSomeValuesFrom some_snps = df.getOWLObjectSomeValuesFrom(has_about, trait_associations);

        OWLClass snp = df.getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
        OWLClassExpression pre2009_snps = df.getOWLObjectIntersectionOf(snp, some_snps);

        OWLObjectProperty location_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATION_OF_PROPERTY_IRI));
        OWLObjectSomeValuesFrom some_bands = df.getOWLObjectSomeValuesFrom(location_of, pre2009_snps);

        OWLClass cyto_band = df.getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));
        OWLClassExpression timeCls = df.getOWLObjectIntersectionOf(cyto_band, some_bands);

        getLog().debug("Query put together succesfully");
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(timeCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/chromosomes")
    public @ResponseBody String renderChromosomes(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the chromosome class
        IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI);
        OWLClass chromCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(chromIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(chromCls, getRenderletNexus(session));
    }

    /*this currently doesn't work as all chromosomes are rendered by default, not if a given chromosome individual exists
    *as some chromosomes don't have any instances and would therefore never be rendered*/
    @RequestMapping(value = "/chromosomes/{chromosomeName}")
    public @ResponseBody String renderChromosome(@PathVariable String chromosomeName, HttpSession session)
            throws PussycatSessionNotReadyException {
        // retrieve a reference to the chromosome class
        IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI);
        OWLClass chromCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(chromIRI);
        // retrieve has_name {value} restriction
        OWLDataProperty has_name = getOntologyConfiguration().getOWLDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
        OWLLiteral chr_name = getOntologyConfiguration().getOWLDataFactory().getOWLLiteral(chromosomeName);
        OWLDataHasValue hasNameValue =
                getOntologyConfiguration().getOWLDataFactory().getOWLDataHasValue(has_name, chr_name);
        // retrieve the intersection of these classes
        OWLClassExpression query =
                getOntologyConfiguration().getOWLDataFactory().getOWLObjectIntersectionOf(chromCls, hasNameValue);

        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(query, getRenderletNexus(session));
    }

    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the SNP class
        IRI snpIRI = IRI.create(OntologyConstants.SNP_CLASS_IRI);
        OWLClass snpCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(snpIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(snpCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/snps/{rsID}")
    public @ResponseBody String renderSNP(@PathVariable String rsID, HttpSession session)
            throws PussycatSessionNotReadyException {
        // retrieve a reference to the SNP class
        IRI snpIRI = IRI.create(OntologyConstants.SNP_CLASS_IRI);
        OWLClass snpCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(snpIRI);
        // retrieve has_name {value} restriction
        OWLDataProperty has_snp_rsid = getOntologyConfiguration().getOWLDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_SNP_REFERENCE_ID_PROPERTY_IRI));
        OWLLiteral snpRSID = getOntologyConfiguration().getOWLDataFactory().getOWLLiteral(rsID);
        OWLDataHasValue hasRsidValue =
                getOntologyConfiguration().getOWLDataFactory().getOWLDataHasValue(has_snp_rsid, snpRSID);
        // retrieve the intersection of these classes
        OWLClassExpression query =
                getOntologyConfiguration().getOWLDataFactory().getOWLObjectIntersectionOf(snpCls, hasRsidValue);

        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(query, getRenderletNexus(session));
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAssociations(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the trait association class
        IRI taIRI = IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI);
        OWLClass taCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(taIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(taCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/traits")
    public @ResponseBody String renderTraits(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the EFO class
        IRI efIRI = IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(efCls, getRenderletNexus(session));
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException {
        // retrieve a reference to the EFO class with the supplied IRI
        IRI efIRI = IRI.create(efoURI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(efCls, getRenderletNexus(session));
    }

    /*This method returns all the children of the provided URI in order to allow filtering based on URIs*/
    @RequestMapping(value = "/filter/{efoURI}")
    public @ResponseBody ArrayList<String> filterTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException, OWLConversionException {
        ArrayList<String> childClasses = new ArrayList<String>();
        // retrieve a reference to the EFO class with the supplied IRI
        IRI efIRI = IRI.create(efoURI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);

        Set<OWLClass> allChildren =
                getPussycatSession(session).getReasoner().getSubClasses(efCls, false).getFlattened();
        for (OWLClass child : allChildren) {
            childClasses.add(child.getIRI().toString());
        }
        // return the URIs of all classes that are children, asserted or inferred, of the provided parent class
        return childClasses;
    }


    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(PussycatSessionNotReadyException.class)
    public @ResponseBody String handlePussycatSessionNotReadyException(PussycatSessionNotReadyException e) {
        String responseMsg = "Please wait while Pussycat starts up!<br/>" + e.getMessage();
        getLog().error(responseMsg, e);
        return responseMsg;
    }

    protected PussycatSession getPussycatSession(HttpSession session) {
        getLog().debug("Attempting to obtain Pussycat session for HttpSession '" + session.getId() + "'");
        if (getPussycatManager().hasAvailableSession(session)) {
            getLog().debug("Pussycat manager has an available session for HttpSession '" + session.getId() + "'");
            return getPussycatManager().getPussycatSession(session);
        }
        else {
            PussycatSession pussycatSession;
            if (getSessionStrategy() == PussycatSessionStrategy.JOIN &&
                    getPussycatManager().getPussycatSessions().size() > 0) {
                pussycatSession = getPussycatManager().getPussycatSessions().iterator().next();
            }
            else {
                pussycatSession = getPussycatManager().createPussycatSession();
                getLog().debug("Created new pussycat session, id '" + pussycatSession.getSessionID() + "'");
            }
            getLog().debug("Pussycat manager has no available session, but can join HttpSession " +
                                   "'" + session.getId() + "' to pussycat session " +
                                   "'" + pussycatSession.getSessionID() + "'");
            return getPussycatManager().joinPussycatSession(session, pussycatSession);
        }
    }

    protected RenderletNexus getRenderletNexus(HttpSession session) throws PussycatSessionNotReadyException {
        getLog().debug("Attempting to obtain RenderletNexus session for HttpSession '" + session.getId() + "'");

        try {
            RenderletNexus renderletNexus;

            if (nexusMap.containsKey(session)) {
                renderletNexus = nexusMap.get(session);
                getLog().debug("RenderletNexus available for HttpSession '" + session.getId() + "'");
                return renderletNexus;
            }
            else {
//           renderletNexus = RenderletNexusFactory.createDefaultRenderletNexus();
                renderletNexus = RenderletNexusFactory.createDefaultRenderletNexus(
                        getOntologyConfiguration().getOWLOntologyManager(),
                        getPussycatSession(session).getReasoner(),
                        getOntologyConfiguration().getEfoLabels());

                nexusMap.put(session, renderletNexus);

                Collection<Renderlet> renderlets = getPussycatSession(session).getAvailableRenderlets();

                for (Renderlet r : renderlets) {
                    renderletNexus.register(r);
                }

                getLog().debug("Created new RenderletNexus for HttpSession '" + session + "'");
                return renderletNexus;
            }
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Unexpected exception occurred obtaining reasoner", e);
        }
    }

    protected Collection<OWLIndividual> fetchInstancesOf(final OWLOntology ontology, final OWLClass cls) {
        final Collection<OWLIndividual> individuals = new HashSet<OWLIndividual>();

        OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(ontology));
        OWLOntologyWalkerVisitor<Object> visitor = new OWLOntologyWalkerVisitor<Object>(walker) {
            @Override public Object visit(OWLClassAssertionAxiom axiom) {
                if (!axiom.getClassExpression().isAnonymous()) {
                    OWLClass assertedClass = axiom.getClassExpression().asOWLClass();
                    if (assertedClass.equals(cls)) {
                        individuals.add(axiom.getIndividual());
                    }
                }
                return null;
            }
        };
        walker.walkStructure(visitor);
        return individuals;
    }
}
