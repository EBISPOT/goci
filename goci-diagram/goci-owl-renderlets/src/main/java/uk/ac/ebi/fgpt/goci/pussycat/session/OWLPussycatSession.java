package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.lang.OWLAPIFilterInterpreter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.reasoning.ReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.ArrayList;
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

    private Logger diagramLogger = LoggerFactory.getLogger("diagram.log");

    protected Logger getDiagramLogger() {
        return diagramLogger;
    }

    public OWLPussycatSession(OWLAPIFilterInterpreter filterInterpreter,
                              OntologyConfiguration ontologyConfiguration,
                              ReasonerSession reasonerSession) {
        super();
        this.filterInterpreter = filterInterpreter;
        this.ontologyConfiguration = ontologyConfiguration;
        this.reasonerSession = reasonerSession;
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

        try {
            // attempt to query the reasoner for data
            OWLReasoner reasoner = getReasoner();
            OWLOntology ontology = reasoner.getRootOntology();
            Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();

            // sort them into the order we need them in for rendering
            List<OWLIndividual> sortedIndividuals = sortIndividualsIntoRenderingOrder(individuals);

            // and render
            for (OWLIndividual individual : sortedIndividuals) {
                for (Renderlet r : getAvailableRenderlets()) {
                    if (r.canRender(renderletNexus, ontology, individual)) {
                        getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                        r.render(renderletNexus, ontology, individual);
                    }
                }
            }
            return renderletNexus.getSVG();
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Failed to render SVG", e);
        }
    }

    /**
     * Sorts a set of OWLIndividuals into an order suitable for rendering.  This essentially means chromosomes must be
     * rendered first, followed by bands, then snp-trait associations
     *
     * @param individuals the set of individuals to sort
     * @return a list of individuals, sorted into suitable rendering order
     */
    private List<OWLIndividual> sortIndividualsIntoRenderingOrder(Set<OWLNamedIndividual> individuals) {
        List<OWLIndividual> sorted = new ArrayList<OWLIndividual>();

        // todo - do sort properly!
        sorted.addAll(individuals);

        return sorted;
    }
}