package uk.ac.ebi.fgpt.goci.pussycat.controller;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatSessionManager;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.utils.SVGUtils;

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
public class PussycatController {
    private PussycatSession.Strategy sessionStrategy;
    private PussycatSessionManager pussycatManager;

    private OntologyConfiguration ontologyConfiguration;

    public PussycatSession.Strategy getSessionStrategy() {
        return sessionStrategy;
    }

    @Autowired
    public void setSessionStrategy(PussycatSession.Strategy sessionStrategy) {
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

    @RequestMapping(value = "/gwasdiagram")
    public @ResponseBody String renderGWASDiagram(HttpSession session) {
        PussycatSession pussycatSession = getPussycatSession(session);
        OWLClass thingCls = getOntologyConfiguration().getOWLDataFactory().getOWLThing();
        // fetch all chromosome individuals
        Collection<OWLIndividual> individuals = fetchInstancesOf(pussycatSession.getLoadedData(), thingCls);

        // render these individuals
        StringBuilder svg = new StringBuilder();
        svg.append(SVGUtils.buildSVGHeader("gwas-diagram"));
        for (OWLIndividual ind : individuals) {
            for (Renderlet r : pussycatSession.getAvailableRenderlets()) {
                if (r.canRender(pussycatSession.getRenderletNexus(), ind)) {
                    svg.append(r.render(pussycatSession.getRenderletNexus(), ind));
                }
            }
        }
        svg.append(SVGUtils.closeSVG());
        return svg.toString();
    }

    @RequestMapping(value = "/chromosomes")
    public @ResponseBody String renderChromosomes(HttpSession session) {
        // fetch our pussycat session
        PussycatSession pussycatSession = getPussycatSession(session);
        // retrieve a reference to the chromosome class
        IRI chromIRI = IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI);
        OWLClass chromCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(chromIRI);

        // fetch all chromosome individuals
        Collection<OWLIndividual> chroms = fetchInstancesOf(pussycatSession.getLoadedData(), chromCls);

        // render these individuals
        StringBuilder svg = new StringBuilder();
        svg.append(SVGUtils.buildSVGHeader("chromosomes"));
        for (OWLIndividual chrom : chroms) {
            for (Renderlet r : pussycatSession.getAvailableRenderlets()) {
                if (r.canRender(pussycatSession.getRenderletNexus(), chrom)) {
                    svg.append(r.render(pussycatSession.getRenderletNexus(), chrom));
                }
            }
        }
        svg.append(SVGUtils.closeSVG());
        return svg.toString();
    }

    @RequestMapping(value = "/chromosomes/{chromosomeName}")
    public @ResponseBody String renderChromosome(@PathVariable String chromosomeName, HttpSession session) {
        PussycatSession pussycatSession = getPussycatSession(session);
        return null;
    }

    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) {
        // fetch our pussycat session
        PussycatSession pussycatSession = getPussycatSession(session);
        // retrieve a reference to the chromosome class
        IRI snpIRI = IRI.create(OntologyConstants.SNP_CLASS_IRI);
        OWLClass snpCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(snpIRI);

        // fetch all chromosome individuals
        Collection<OWLIndividual> snps = fetchInstancesOf(pussycatSession.getLoadedData(), snpCls);

        // render these individuals
        StringBuilder svg = new StringBuilder();
        svg.append(SVGUtils.buildSVGHeader("SNPs"));
        for (OWLIndividual snp : snps) {
            for (Renderlet r : pussycatSession.getAvailableRenderlets()) {
                if (r.canRender(pussycatSession.getRenderletNexus(), snp)) {
                    svg.append(r.render(pussycatSession.getRenderletNexus(), snp));
                }
            }
        }
        svg.append(SVGUtils.closeSVG());
        return svg.toString();
    }

    @RequestMapping(value = "/snps/{rsID}")
    public @ResponseBody String renderSNP(@PathVariable String rsID, HttpSession session) {
        PussycatSession pussycatSession = getPussycatSession(session);
        return null;
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAssociations(HttpSession session) {
        // fetch our pussycat session
        PussycatSession pussycatSession = getPussycatSession(session);
        // retrieve a reference to the chromosome class
        IRI taIRI = IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI);
        OWLClass taCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(taIRI);

        // fetch all chromosome individuals
        Collection<OWLIndividual> tas = fetchInstancesOf(pussycatSession.getLoadedData(), taCls);

        // render these individuals
        StringBuilder svg = new StringBuilder();
        svg.append(SVGUtils.buildSVGHeader("trait-associations"));
        for (OWLIndividual ta : tas) {
            for (Renderlet r : pussycatSession.getAvailableRenderlets()) {
                if (r.canRender(pussycatSession.getRenderletNexus(), ta)) {
                    svg.append(r.render(pussycatSession.getRenderletNexus(), ta));
                }
            }
        }
        svg.append(SVGUtils.closeSVG());
        return svg.toString();
    }

    @RequestMapping(value = "/associations/{associationID}")
    public @ResponseBody String renderAssociation(@PathVariable String associationID, HttpSession session) {
        PussycatSession pussycatSession = getPussycatSession(session);
        return null;
    }

    @RequestMapping(value = "/traits")
    public @ResponseBody String renderTraits(HttpSession session) {
        // fetch our pussycat session
        PussycatSession pussycatSession = getPussycatSession(session);
        // retrieve a reference to the chromosome class
        IRI efIRI = IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI);
        OWLClass efCls = getOntologyConfiguration().getOWLDataFactory().getOWLClass(efIRI);

        // fetch all chromosome individuals
        Collection<OWLIndividual> efs = fetchInstancesOf(pussycatSession.getLoadedData(), efCls);

        // render these individuals
        StringBuilder svg = new StringBuilder();
        svg.append(SVGUtils.buildSVGHeader("traits"));
        for (OWLIndividual ef : efs) {
            for (Renderlet r : pussycatSession.getAvailableRenderlets()) {
                if (r.canRender(pussycatSession.getRenderletNexus(), ef)) {
                    svg.append(r.render(pussycatSession.getRenderletNexus(), ef));
                }
            }
        }
        svg.append(SVGUtils.closeSVG());
        return svg.toString();
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session) {
        PussycatSession pussycatSession = getPussycatSession(session);
        return null;
    }

    protected PussycatSession getPussycatSession(HttpSession session) {
        if (getPussycatManager().hasAvailableSession(session)) {
            return getPussycatManager().getPussycatSession(session);
        }
        else {
            if (sessionStrategy == PussycatSession.Strategy.JOIN &&
                    getPussycatManager().getPussycatSessions().size() > 0) {
                PussycatSession firstSession = getPussycatManager().getPussycatSessions().iterator().next();
                return getPussycatManager().joinPussycatSession(session, firstSession.getSessionID());
            }
            else {
                return getPussycatManager().createPussycatSession(session);
            }
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
