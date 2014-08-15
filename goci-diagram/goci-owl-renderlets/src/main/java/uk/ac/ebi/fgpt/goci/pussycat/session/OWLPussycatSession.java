package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.lang.OWLAPIFilterInterpreter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.reasoning.ReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * An OWL-based implementation of a Pussycat session.  Loads a knowledgebase from an OWL file, reasons over the whole
 * knowledgebase, and derives OWLClassExpressions from filters to perform renderings of the knowledge contained.
 * <p/>
 * Utilises reasoners that are capable of rendering OWLOntology and OWLIndividual data
 *
 * @author Tony Burdett
 * @date 28/07/14
 */
public class OWLPussycatSession extends AbstractSVGIOPussycatSession {
    private final OWLAPIFilterInterpreter filterInterpreter;

    private final OntologyConfiguration ontologyConfiguration;
    private final ReasonerSession reasonerSession;

    private boolean rendering = false;

    public OWLPussycatSession(OWLAPIFilterInterpreter filterInterpreter,
                              OntologyConfiguration ontologyConfiguration,
                              ReasonerSession reasonerSession) {
        super();
        this.filterInterpreter = filterInterpreter;
        this.ontologyConfiguration = ontologyConfiguration;
        this.reasonerSession = reasonerSession;
    }

    public synchronized boolean isRendering() {
        return rendering;
    }

    public synchronized void setRendering(boolean rendering) {
        this.rendering = rendering;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
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

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        // derive class expression
        OWLClassExpression classExpression = filterInterpreter.interpretFilters(filters);

        getLog().debug("Rendering SVG for OWLClass '" + classExpression + "'...");
        try {
            // attempt to query the reasoner for data
            getLog().trace("Attempting to obtain reasoner to use in rendering...");
            OWLReasoner reasoner = getReasoner();
            getLog().trace("Acquired reasoner OK!");

            // now render
            if (!isRendering()) {
                setRendering(true);
                Set<OWLClass> classes = reasoner.getSubClasses(classExpression, false).getFlattened();
                Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();

                // render classes first
                for (OWLClass cls : classes) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, reasoner, cls)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, reasoner, cls);
                        }
                    }
                }

                // then render individuals, sorted into the order we need them in for rendering
                List<OWLNamedIndividual> sortedIndividuals = sortIndividualsIntoRenderingOrder(reasoner, individuals);
                for (OWLNamedIndividual individual : sortedIndividuals) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, reasoner, individual)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, reasoner, individual);
                        }
                    }
                }
                setRendering(false);
                return renderletNexus.getSVG();
            }
            else {
                throw new PussycatSessionNotReadyException("The GWAS diagram is currently being rendered");
            }
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Failed to initialize reasoner - cannot render SVG", e);
        }
    }

    /**
     * Sorts a set of OWLIndividuals into an order suitable for rendering.  This essentially means associations should
     * be rendered first, followed by traits.  SNP individuals do not get rendered so their order is not important.
     *
     * @param individuals the set of individuals to sort
     * @return a list of individuals, sorted into suitable rendering order
     */
    private List<OWLNamedIndividual> sortIndividualsIntoRenderingOrder(OWLReasoner reasoner,
                                                                       Set<OWLNamedIndividual> individuals) {
        List<OWLNamedIndividual> sorted = new ArrayList<OWLNamedIndividual>();
        sorted.addAll(individuals);

        // get all individuals of type association, then of type "trait"
        OWLDataFactory factory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLClass associationCls = factory.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        final Set<OWLNamedIndividual> associations = reasoner.getInstances(associationCls, false).getFlattened();

        Collections.sort(sorted, new Comparator<OWLNamedIndividual>() {
            @Override public int compare(OWLNamedIndividual o1, OWLNamedIndividual o2) {
                if (associations.contains(o1) && !associations.contains(o2)) {
                    return -1;
                }
                else {
                    if (associations.contains(o2) && !associations.contains(o1)) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            }
        });
        return sorted;
    }
}