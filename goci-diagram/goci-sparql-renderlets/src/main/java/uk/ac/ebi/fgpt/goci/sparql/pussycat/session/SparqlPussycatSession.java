package uk.ac.ebi.fgpt.goci.sparql.pussycat.session;

import com.hp.hpl.jena.query.QuerySolution;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.AbstractPussycatSession;
import uk.ac.ebi.fgpt.goci.sparql.exception.SparqlQueryException;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.QuerySolutionMapper;
import uk.ac.ebi.fgpt.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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
        getLog().debug("Rendering SVG from SPARQL endpoint (filters = '" + filters + "')...");
        try {
            // render
            if (!isRendering()) {
                setRendering(true);

                try {
                    getLog().debug("Querying SPARQL endpoint for GWAS data...");
                    List<URI> chromosomes = loadChromosomes(sparqlTemplate);
                    List<URI> individuals = new ArrayList<URI>();
                    individuals.addAll(loadAssociations(sparqlTemplate));
                    individuals.addAll(loadTraits(sparqlTemplate));
                    getLog().debug("GWAS data acquired, starting rendering...");

                    // render chromosomes first
                    for (URI chromosome : chromosomes) {
                        for (Renderlet r : getAvailableRenderlets()) {
                            if (r.canRender(renderletNexus, sparqlTemplate, chromosome)) {
                                getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                                r.render(renderletNexus, sparqlTemplate, chromosome);
                            }
                        }
                    }

                    // then render individuals
                    for (URI individual : individuals) {
                        for (Renderlet r : getAvailableRenderlets()) {
                            if (r.canRender(renderletNexus, sparqlTemplate, individual)) {
                                getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                                r.render(renderletNexus, sparqlTemplate, individual);
                            }
                        }
                    }
                    getLog().debug("SVG rendering complete!");
                    return renderletNexus.getSVG();
                }
                finally {
                    setRendering(false);
                }
            }
            else {
                throw new PussycatSessionNotReadyException("The GWAS diagram is currently being rendered");
            }
        }
        catch (SparqlQueryException e) {
            throw new RuntimeException("Failed to load data - cannot render SVG", e);
        }
    }

    private List<URI> loadChromosomes(SparqlTemplate sparqlTemplate) {
        return sparqlTemplate.query("SELECT DISTINCT ?uri WHERE { ?uri rdfs:subClassOf gt:Chromosome }", "uri");
    }

    private List<URI> loadAssociations(SparqlTemplate sparqlTemplate) {
        List<AssociationLocation> associationLocations =
                sparqlTemplate.query("SELECT ?association ?band " +
                                             "WHERE { " +
                                             "  ?association a gt:TraitAssociation ; " +
                                             "               oban:has_subject ?snp . " +
                                             "  ?snp ro:located_in ?bandUri . " +
                                             "  ?bandUri rdfs:label ?band }",
                                     new QuerySolutionMapper<AssociationLocation>() {
                                         @Override public AssociationLocation mapQuerySolution(QuerySolution qs) {
                                             URI association = URI.create(qs.getResource("association").getURI());
                                             String bandName = qs.getLiteral("band").getLexicalForm();
                                             return new AssociationLocation(association, bandName);
                                         }
                                     });
        Collections.sort(associationLocations);
        List<URI> associations = new ArrayList<URI>();
        for (AssociationLocation al : associationLocations) {
            associations.add(al.getAssociation());
        }
        return associations;
    }

    private List<URI> loadTraits(SparqlTemplate sparqlTemplate) {
        return sparqlTemplate.query("SELECT DISTINCT ?trait " +
                                            "WHERE { " +
                                            "  ?association a gt:TraitAssociation ; " +
                                            "               oban:has_object ?trait . }", "trait");
    }

    private class AssociationLocation implements Comparable<AssociationLocation> {
        private final URI association;
        private final BandInformation band;

        private AssociationLocation(URI association, String bandName) {
            this.association = association;
            this.band = new BandInformation(bandName);
        }

        public URI getAssociation() {
            return association;
        }

        public BandInformation getBand() {
            return band;
        }

        @Override public int compareTo(AssociationLocation o) {
            return getBand().compareTo(o.getBand());
        }
    }
}
