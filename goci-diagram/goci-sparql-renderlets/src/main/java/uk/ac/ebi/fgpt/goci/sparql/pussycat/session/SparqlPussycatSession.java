package uk.ac.ebi.fgpt.goci.sparql.pussycat.session;

import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.AbstractPussycatSession;
import uk.ac.ebi.fgpt.goci.sparql.exception.SparqlQueryException;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.QueryManager;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utilises an {@link uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate} to load RDF data into session and render
 * it as SVG
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public class SparqlPussycatSession extends AbstractPussycatSession {
    private SparqlTemplate sparqlTemplate;

    private boolean rendering = false;

    public SparqlPussycatSession(SparqlTemplate sparqlTemplate) {
        this.sparqlTemplate = sparqlTemplate;
    }

    public synchronized boolean isRendering() {
        return rendering;
    }

    public synchronized void setRendering(boolean rendering) {
        this.rendering = rendering;
    }

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        // todo - work out how to translate filters into restricted queries

        getLog().debug("Rendering SVG from SPARQL endpoint (filters = '" + filters + "')...");
        try {
            // render
            if (!isRendering()) {
                setRendering(true);

                List<URI> classes = sparqlTemplate.list("SELECT DISTINCT ?uri WHERE { ?uri a owl:Class . }");
                List<URI> individuals = sparqlTemplate.list("SELECT DISTINCT ?uri WHERE { ?uri a owl:Individual . }");

                // render everything that is an owl:class first
                for (URI cls : classes) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, sparqlTemplate, cls)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, sparqlTemplate, cls);
                        }
                    }
                }

                // then render individuals, sorted into the order we need them in for rendering
                List<URI> sortedIndividuals = sortIndividualsIntoRenderingOrder(sparqlTemplate, individuals);
                for (URI individual : sortedIndividuals) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, sparqlTemplate, individual)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, sparqlTemplate, individual);
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
        catch (SparqlQueryException e) {
            throw new RuntimeException("Failed to load data - cannot render SVG", e);
        }
    }

    /**
     * Sorts a set of OWLIndividuals into an order suitable for rendering.  This essentially means associations should
     * be rendered first, followed by traits.  SNP individuals do not get rendered so their order is not important.
     *
     * @param individuals the set of individuals to sort
     * @return a list of individuals, sorted into suitable rendering order
     */
    private List<URI> sortIndividualsIntoRenderingOrder(final SparqlTemplate sparqlTemplate,
                                                        final List<URI> individuals) {
        List<URI> sorted = new ArrayList<URI>();
        sorted.addAll(individuals);

        // sort all individuals of type association first, then of type "trait" second
        final URI associationType = URI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI);
        Collections.sort(sorted, new Comparator<URI>() {
            @Override public int compare(URI o1, URI o2) {
                if (sparqlTemplate.ask(o1, associationType) && !sparqlTemplate.ask(o2, associationType)) {
                    return -1;
                }
                else {
                    if (sparqlTemplate.ask(o2, associationType) && !sparqlTemplate.ask(o1, associationType)) {
                        return 1;
                    }
                    else {
                        if (sparqlTemplate.ask(o1, associationType) && sparqlTemplate.ask(o2, associationType)) {
                            // both associations, sort according to the cytogenetic band
                            BandInformation band1, band2;
                            try {
                                URI bi1 = QueryManager.getCachingInstance()
                                        .getCytogeneticBandForAssociation(sparqlTemplate, o1);
                                band1 = QueryManager.getCachingInstance().getBandInformation(sparqlTemplate, bi1);
                            }
                            catch (DataIntegrityViolationException e) {
                                getLog().debug("Can't properly sort association " + o1 + " - unable to identify band");
                                return 1;
                            }
                            try {
                                URI bi2 = QueryManager.getCachingInstance()
                                        .getCytogeneticBandForAssociation(sparqlTemplate, o2);
                                band2 = QueryManager.getCachingInstance().getBandInformation(sparqlTemplate, bi2);
                            }
                            catch (DataIntegrityViolationException e) {
                                getLog().debug("Can't properly sort association " + o2 + " - unable to identify band");
                                return -1;
                            }
                            return band1.compareTo(band2);
                        }
                        else {
                            // other instance, not an association, so we don't care
                            return 0;
                        }
                    }
                }
            }
        });
        return sorted;
    }
}
