package uk.ac.ebi.fgpt.goci.pussycat.controller;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionStrategy;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.fgpt.goci.lang.Filtering.filter;
import static uk.ac.ebi.fgpt.goci.lang.Filtering.refine;
import static uk.ac.ebi.fgpt.goci.lang.Filtering.template;

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
    private PussycatManager pussycatManager;

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

    public PussycatManager getPussycatManager() {
        return pussycatManager;
    }

    @Autowired
    public void setPussycatSessionManager(PussycatManager pussycatManager) {
        this.pussycatManager = pussycatManager;
    }

    @RequestMapping(params = "clear")
    public @ResponseBody boolean clearRendering(HttpSession session) throws PussycatSessionNotReadyException {
        try {
            getRenderletNexus(session).reset();
            return true;
        }
        catch (PussycatSessionNotReadyException e) {
            getLog().error("Attempting to clear a renderlet nexus with no bound pussycat session, nothing happened");
            return false;
        }
    }

    @RequestMapping(value = "/gwasdiagram")
    public @ResponseBody String renderGWASDiagram(HttpSession session) throws PussycatSessionNotReadyException {
        // render all data using the pussycat session for this http session
        return getPussycatSession(session).performRendering(getRenderletNexus(session));
    }

    @RequestMapping(value = "/gwasdiagram/timeseries/{year}/{month}")
    public @ResponseBody String renderGWASDiagramTimeSeries(@PathVariable String year,
                                                            @PathVariable String month,
                                                            HttpSession session)
            throws PussycatSessionNotReadyException {
        // get the subset of studies published before the supplied date
        /*trait association'  and part_of some ('GWAS study' and has_publication_date some dateTime[< "  "^^dateTime])*/
        getLog().debug("Received a new rendering request - " +
                               "putting together the query for year '" + year + "' and month '" + month + "'");

        int monthVar = Integer.parseInt(month);
        int yearVar = Integer.parseInt(year);

        //API call provides date for "up to and including the end of" - must increment month for query
        if (monthVar == 12) {
            month = "01";
            yearVar++;
            year = Integer.toString(yearVar);
        }
        else {
            monthVar++;
            if (monthVar > 9) {
                month = Integer.toString(monthVar);
            }
            else {
                month = "0".concat(Integer.toString(monthVar));
            }
        }

        try {
            DateFormat df = new SimpleDateFormat("YYYY/MM");
            Date from = df.parse("2005/01");
            Date to = df.parse(year + "/" + month);

            Study study = template(Study.class);
            Filter filter = refine(study).on(study.getPublishedDate()).hasRange(from, to);
            return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
        }
        catch (ParseException e) {
            getLog().error("Bad date in URL /gwasdiagram/timeseries/" + year + "/" + month + " - " +
                                   "use /gwasdiagram/timeseries/YYYY/MM", e);
            throw new RuntimeException("Bad date in URL /gwasdiagram/timeseries/" + year + "/" + month + " - " +
                                               "use /gwasdiagram/timeseries/YYYY/MM", e);
        }
    }

    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) throws PussycatSessionNotReadyException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(snp));
    }

    @RequestMapping(value = "/snps/{rsID}")
    public @ResponseBody String renderSNP(@PathVariable String rsID, HttpSession session)
            throws PussycatSessionNotReadyException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        Filter filter = refine(snp).on(snp.getRSID()).hasValue(rsID);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAssociations(HttpSession session) throws PussycatSessionNotReadyException {
        TraitAssociation ta = template(TraitAssociation.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(ta));
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException {
        TraitAssociation ta = template(TraitAssociation.class);
        Filter filter = refine(ta).on(ta.getAssociatedTrait()).hasValue(URI.create(efoURI));
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
    }

    /**
     * This method returns all the children of the provided URI in order to allow filtering based on URIs
     *
     * @param traitName the name of the trait to lookup
     * @param session   the http session in which to perform this request
     * @return a list of URIs, appropriately "shortformed", that describe the classes that are relevant for this trait
     * name
     * @throws PussycatSessionNotReadyException
     * @throws OWLConversionException
     */
    @RequestMapping(value = "/filter/{traitName}")
    public @ResponseBody Set<String> filterTrait(@PathVariable String traitName, HttpSession session)
            throws PussycatSessionNotReadyException, OWLConversionException {
        // todo - replace ontologyDAO and reasoner with something simpler here - maybe still need a single reasoner?

//        // lookup class from label
//        getLog().debug("Filtering on classes with label '" + traitName + "'");
//        Collection<OWLClass> labelledClasses = getOntologyDAO().getOWLClassesByLabel(traitName);

        // get the set of all classes and subclasses
        Set<OWLClass> allClasses = new HashSet<OWLClass>();

//        for (OWLClass labelledClass : labelledClasses) {
//            allClasses.add(labelledClass);
//            allClasses.addAll(getPussycatSession(session).getReasoner()
//                                      .getSubClasses(labelledClass, false)
//                                      .getFlattened());
//        }

        // now get the short forms for each class in allClasses
        Set<String> classNames = new HashSet<String>();
        for (OWLClass cls : allClasses) {
            String shortform = OntologyUtils.getShortForm(cls);
            getLog().trace("Next shortform in subclass set: '" + shortform + "'");
            classNames.add(shortform);
        }

        // return the URIs of all classes that are children, asserted or inferred, of the provided parent class
        return classNames;
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
        if (getPussycatManager().hasAvailablePussycatSession(session)) {
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
            return getPussycatManager().bindPussycatSession(session, pussycatSession);
        }
    }

    protected RenderletNexus getRenderletNexus(HttpSession session) throws PussycatSessionNotReadyException {
        getLog().debug("Attempting to obtain RenderletNexus session for HttpSession '" + session.getId() + "'");

        RenderletNexus renderletNexus;
        if (getPussycatManager().hasAvailableRenderletNexus(session)) {
            getLog().debug("RenderletNexus available for HttpSession '" + session.getId() + "'");
            renderletNexus = getPussycatManager().getRenderletNexus(session);
        }
        else {
            renderletNexus = getPussycatManager().createRenderletNexus(
                    getPussycatManager().getPussycatSession(session));
            getPussycatManager().bindRenderletNexus(session, renderletNexus);
        }

        return renderletNexus;
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
