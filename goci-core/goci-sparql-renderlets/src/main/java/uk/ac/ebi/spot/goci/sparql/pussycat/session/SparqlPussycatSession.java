package uk.ac.ebi.spot.goci.sparql.pussycat.session;

import com.hp.hpl.jena.query.QuerySolution;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.spot.goci.dao.DefaultOntologyDAO;
import uk.ac.ebi.spot.goci.lang.Filter;
import uk.ac.ebi.spot.goci.ui.model.AssociationSummary;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.pussycat.session.AbstractPussycatSession;
import uk.ac.ebi.spot.goci.reasoning.ReasonerSession;
import uk.ac.ebi.spot.goci.sparql.exception.SparqlQueryException;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.QuerySolutionMapper;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate;
import uk.ac.ebi.spot.goci.sparql.pussycat.reasoning.DAOBasedReasonerSession;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilises an {@link uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate} to load RDF data into session and render
 * it as SVG
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public class SparqlPussycatSession extends AbstractPussycatSession {
    private DefaultOntologyDAO ontologyDAO;
    private SparqlTemplate sparqlTemplate;

    private ReasonerSession reasonerSession;

    private boolean rendering = false;

    public SparqlPussycatSession(DefaultOntologyDAO ontologyDAO, SparqlTemplate sparqlTemplate) {
        this.ontologyDAO = ontologyDAO;
        this.sparqlTemplate = sparqlTemplate;

        reasonerSession = new DAOBasedReasonerSession(getOntologyDAO());
    }

    public DefaultOntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    public SparqlTemplate getSparqlTemplate() {
        return sparqlTemplate;
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
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
                    List<URI> chromosomes = loadChromosomes(getSparqlTemplate());
                    List<URI> individuals = new ArrayList<URI>();
                    individuals.addAll(loadAssociations(getSparqlTemplate()));
                    individuals.addAll(loadTraits(getSparqlTemplate()));
                    getLog().debug("GWAS data acquired, starting rendering...");

                    // render chromosomes first
                    for (URI chromosome : chromosomes) {
                        for (Renderlet r : getAvailableRenderlets()) {
                            if (r.canRender(renderletNexus, getSparqlTemplate(), chromosome)) {
                                getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                                r.render(renderletNexus, getSparqlTemplate(), chromosome);
                            }
                        }
                    }

                    // then render individuals
                    for (URI individual : individuals) {
                        for (Renderlet r : getAvailableRenderlets()) {
                            if (r.canRender(renderletNexus, getSparqlTemplate(), individual)) {
                                getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                                r.render(renderletNexus, getSparqlTemplate(), individual);
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

    @Override public List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs) {
        final String query = "SELECT ?pmid ?author ?date ?rsid ?pval ?gwastrait ?label ?trait WHERE { " +
                "?association a gt:TraitAssociation ; " +
                "             gt:has_p_value ?pval ; " +
                "             gt:has_gwas_trait_name ?gwastrait ; " +
                "             ro:part_of ?study ; " +
                "             oban:has_subject ?snp ; " +
                "             oban:has_object ?trait . " +
                "?snp gt:has_snp_reference_id ?rsid . " +
                "?study gt:has_author ?author ; " +
                "       gt:has_publication_date ?date ; " +
                "       gt:has_pubmed_id ?pmid . " +
                "?trait rdfs:label ?label . " +
                "FILTER (?association = ??)" +
                "}";

        List<AssociationSummary> results = new ArrayList<AssociationSummary>();
        for (URI uri : associationURIs) {
            List<AssociationSummary> summaries =
                    getSparqlTemplate().query(query, new QuerySolutionMapper<AssociationSummary>() {
                        @Override public AssociationSummary mapQuerySolution(QuerySolution qs) {
                            String pubmedID = qs.getLiteral("pmid").getLexicalForm();
                            String firstAuthor = qs.getLiteral("author").getLexicalForm();
                            String publicationDate = qs.getLiteral("date").getLexicalForm().substring(0, 4);
                            String snp = qs.getLiteral("rsid").getLexicalForm();
                            String pValue = qs.getLiteral("pval").getLexicalForm();
                            String gwasTraitName = qs.getLiteral("gwastrait").getLexicalForm();
                            String efoTraitLabel = qs.getLiteral("label").getLexicalForm();
                            URI efoTraitURI = URI.create(qs.getResource("trait").getURI());
                            return new SparqlAssociationSummary(pubmedID, firstAuthor, publicationDate, snp, pValue,
                                                                gwasTraitName, efoTraitLabel, efoTraitURI);
                        }
                    }, uri);
            if (summaries.size() == 0) {
                results.add(null);
            }
            else {
                results.add(summaries.iterator().next());
            }
        }
        return results;
    }

    @Override public Set<URI> getRelatedTraits(String traitName) {
        // get OWLClasses by name
        Collection<OWLClass> traitClasses = getOntologyDAO().getOWLClassesByLabel(traitName);

        Set<URI> results = new HashSet<URI>();
        // check reasoner
        OWLReasoner reasoner = getReasonerSession().getReasoner();
        for (OWLClass traitClass : traitClasses) {
            results.add(traitClass.getIRI().toURI());
            Set<OWLClass> subclasses = reasoner.getSubClasses(traitClass, false).getFlattened();
            for (OWLClass subclass : subclasses) {
                results.add(subclass.getIRI().toURI());
            }
        }
        return results;
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

    private class SparqlAssociationSummary implements AssociationSummary {
        private final String pubmedID;
        private final String firstAuthor;
        private final String publicationDate;
        private final String snp;
        private final String pValue;
        private final String gwasTraitName;
        private final String efoTraitLabel;
        private final URI efoTraitURI;

        public SparqlAssociationSummary(String pubmedID,
                                        String firstAuthor,
                                        String publicationDate,
                                        String snp,
                                        String pValue,
                                        String gwasTraitName,
                                        String efoTraitLabel,
                                        URI efoTraitURI) {
            this.pubmedID = pubmedID;
            this.firstAuthor = firstAuthor;
            this.publicationDate = publicationDate;
            this.snp = snp;
            this.pValue = pValue;
            this.gwasTraitName = gwasTraitName;
            this.efoTraitLabel = efoTraitLabel;
            this.efoTraitURI = efoTraitURI;
        }

        @Override public String getPubMedID() {
            return pubmedID;
        }

        @Override public String getFirstAuthor() {
            return firstAuthor;
        }

        @Override public String getPublicationDate() {
            return publicationDate;
        }

        public String getSNP() {
            return snp;
        }

        @Override public String getPvalue() {
            return pValue;
        }

        @Override public String getGWASTraitName() {
            return gwasTraitName;
        }

        @Override public String getEFOTraitLabel() {
            return efoTraitLabel;
        }

        @Override public URI getEFOTraitURI() {
            return efoTraitURI;
        }
    }
}
