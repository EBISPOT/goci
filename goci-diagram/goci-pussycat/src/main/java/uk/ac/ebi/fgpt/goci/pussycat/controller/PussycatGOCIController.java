package uk.ac.ebi.fgpt.goci.pussycat.controller;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatSessionManager;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A MVC controller for Pussycat.  This controller can be used to create a new session, load ontology data and create
 * the SVG canvas Pussycat will display.
 *
 * @author Tony Burdett
 * @date 27/02/12
 */
@Controller
@RequestMapping("/views")
public class PussycatGOCIController {
    private PussycatSessionStrategy sessionStrategy;
    private PussycatSessionManager pussycatManager;

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
        return getPussycatSession(session).performRendering(thingCls);
    }

    @RequestMapping(value = "/chromosomes")
    public @ResponseBody String renderChromosomes(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the chromosome class
        IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI);
        OWLClass chromCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(chromIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(chromCls);
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
        return getPussycatSession(session).performRendering(query);
    }

    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the SNP class
        IRI snpIRI = IRI.create(OntologyConstants.SNP_CLASS_IRI);
        OWLClass snpCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(snpIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(snpCls);
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
        return getPussycatSession(session).performRendering(query);
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAssociations(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the trait association class
        IRI taIRI = IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI);
        OWLClass taCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(taIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(taCls);
    }

    @RequestMapping(value = "/traits")
    public @ResponseBody String renderTraits(HttpSession session) throws PussycatSessionNotReadyException {
        // retrieve a reference to the EFO class
        IRI efIRI = IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(efCls);
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException {
        // retrieve a reference to the EFO class with the supplied IRI
        IRI efIRI = IRI.create(efoURI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);
        // render all individuals using the pussycat session for this http session
        return getPussycatSession(session).performRendering(efCls);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(PussycatSessionNotReadyException.class)
    public @ResponseBody String handlePussycatSessionNotReadyException(PussycatSessionNotReadyException e) {
        String responseMsg = "Please wait: the PussycatSession is not yet ready (" + e.getMessage() + ")";
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
