package uk.ac.ebi.spot.goci.sparql.pussycat.session;

import com.hp.hpl.jena.query.QuerySolution;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.pussycat.service.OntologyService;
import uk.ac.ebi.spot.goci.pussycat.session.AbstractPussycatSession;
import uk.ac.ebi.spot.goci.pussycat.utils.StringUtils;
import uk.ac.ebi.spot.goci.sparql.exception.SparqlQueryException;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.QuerySolutionMapper;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//import uk.ac.ebi.spot.goci.ui.model.AssociationSummary;

/**
 * Utilises an {@link uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate} to load RDF data into session and render
 * it as SVG
 *
 * @author Tony Burdett
 * @date 21/08/14
 */

@Component
@Qualifier("proxiedSession")
public class SparqlPussycatSession extends AbstractPussycatSession {

    private String sessionID;

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private SparqlTemplate sparqlTemplate;

    private static final String associationQueryMain = "SELECT ?association ?band " +
                                        "WHERE { " +
                                        "  ?association a gt:TraitAssociation ; " +
                                        "               oban:has_subject ?snp . " +
                                        "  ?snp ro:located_in ?bandUri . " +
                                        "  ?bandUri rdfs:label ?band .";

    private static final String associationQueryBandFilter = "  FILTER (STR(?band) != 'NR') }";

    private static final String traitQueryMain = "SELECT DISTINCT ?trait " +
                                    "WHERE { " +
                                    "  ?association a gt:TraitAssociation ; " +
                                    "               oban:has_object ?trait . ";


    private boolean rendering = false;

    private Logger log = LoggerFactory.getLogger("rendering");

    protected Logger getLog() {
        return log;
    }

    public SparqlPussycatSession() {
        this.sessionID = generateSessionID();

    }


    @Override public String getSessionID() {
        return sessionID;
    }

    public OntologyService getOntologyService() {
        return ontologyService;
    }

    public SparqlTemplate getSparqlTemplate() {
        return sparqlTemplate;
    }

    public synchronized boolean isRendering() {
        return rendering;
    }

    public synchronized void setRendering(boolean rendering) {
        this.rendering = rendering;
    }

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {

        String associationQueryString = associationQueryMain;
        String traitQueryString = traitQueryMain;

        getLog().debug("Rendering SVG from SPARQL endpoint (filters = '" + filters + "')...");


        for(Filter filter : filters){
            if(filter.getFilteredType().equals(Association.class)){
                List<Double> values = filter.getFilteredValues();

                associationQueryString = associationQueryString.concat("?association gt:has_p_value ?pvalue .");
                traitQueryString = traitQueryString.concat("?association gt:has_p_value ?pvalue .");

                double from = values.get(0);
                double to = values.get(1);

                associationQueryString = associationQueryString.concat("  FILTER ( ?pvalue < " + to + ")")
                                            .concat("  FILTER ( ?pvalue >= " + from + ")")
                                            .concat(associationQueryBandFilter);

                traitQueryString = traitQueryString.concat("  FILTER ( ?pvalue < " + to + ")")
                                                    .concat("  FILTER ( ?pvalue >= " + from +  ")")
                                                    .concat(" }");

            }
            else if(filter.getFilteredType().equals(Study.class)){

            }
            else {
                associationQueryString = associationQueryString.concat(associationQueryBandFilter);
                traitQueryString = traitQueryString.concat(" }");
            }
            System.out.println(associationQueryString);
            System.out.println(traitQueryString);
        }



        try {
            // render
            if (!isRendering()) {
                setRendering(true);

                try {
                    getLog().debug("Querying SPARQL endpoint for GWAS data...");
                    List<URI> chromosomes = loadChromosomes(getSparqlTemplate());
                    getLog().debug("Acquired " + chromosomes.size() + " chromosomes to render");
                    List<URI> individuals = new ArrayList<URI>();
                    individuals.addAll(loadAssociations(getSparqlTemplate(), associationQueryString));
                    individuals.addAll(loadTraits(getSparqlTemplate(), traitQueryString));
                    getLog().debug("Acquired " + individuals.size() + " individuals to render");

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
                getLog().debug("The GWAS diagram is already being rendered");
                throw new PussycatSessionNotReadyException("The GWAS diagram is currently being rendered");
            }
        }
        catch (SparqlQueryException e) {
            throw new RuntimeException("Failed to load data - cannot render SVG", e);
        }
    }

//    @Override public List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs) {
//        final String query = "SELECT ?pmid ?author ?date ?rsid ?pval ?gwastrait ?label ?trait WHERE { " +
//                "?association a gt:TraitAssociation ; " +
//                "             gt:has_p_value ?pval ; " +
//                "             gt:has_gwas_trait_name ?gwastrait ; " +
//                "             ro:part_of ?study ; " +
//                "             oban:has_subject ?snp ; " +
//                "             oban:has_object ?trait . " +
//                "?snp gt:has_snp_reference_id ?rsid . " +
//                "?study gt:has_author ?author ; " +
//                "       gt:has_publication_date ?date ; " +
//                "       gt:has_pubmed_id ?pmid . " +
//                "?trait rdfs:label ?label . " +
//                "FILTER (?association = ??)" +
//                "}";
//
//        List<AssociationSummary> results = new ArrayList<AssociationSummary>();
//        for (URI uri : associationURIs) {
//            List<AssociationSummary> summaries =
//                    getSparqlTemplate().query(query, new QuerySolutionMapper<AssociationSummary>() {
//                        @Override public AssociationSummary mapQuerySolution(QuerySolution qs) {
//                            String pubmedID = qs.getLiteral("pmid").getLexicalForm();
//                            String firstAuthor = qs.getLiteral("author").getLexicalForm();
//                            String publicationDate = qs.getLiteral("date").getLexicalForm().substring(0, 4);
//                            String snp = qs.getLiteral("rsid").getLexicalForm();
//                            String pValue = qs.getLiteral("pval").getLexicalForm();
//                            String gwasTraitName = qs.getLiteral("gwastrait").getLexicalForm();
//                            String efoTraitLabel = qs.getLiteral("label").getLexicalForm();
//                            URI efoTraitURI = URI.create(qs.getResource("trait").getURI());
//                            return new SparqlAssociationSummary(pubmedID, firstAuthor, publicationDate, snp, pValue,
//                                                                gwasTraitName, efoTraitLabel, efoTraitURI);
//                        }
//                    }, uri);
//            if (summaries.size() == 0) {
//                results.add(null);
//            }
//            else {
//                results.add(summaries.iterator().next());
//            }
//        }
//        return results;
//    }

    @Override public Set<URI> getRelatedTraits(String traitName) {
        // get OWLClasses by name
        Collection<OWLClass> traitClasses = getOntologyService().getOWLClassesByLabel(traitName);

        Set<URI> results = new HashSet<URI>();
        // check reasoner

        OWLReasoner reasoner = getOntologyService().getOntologyLoader().getOWLReasoner();
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
//        return sparqlTemplate.query("SELECT DISTINCT ?uri WHERE { ?uri a gt:Chromosome }", "uri");
    }

    private List<URI> loadAssociations(SparqlTemplate sparqlTemplate, String queryString) {
        List<AssociationLocation> associationLocations =
                sparqlTemplate.query(queryString,   /*had to add this line in to exclude "NR" bands as they break the AssociationLocation bit below
                                                                                    and can't be rendered anyway*/
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

    private List<URI> loadTraits(SparqlTemplate sparqlTemplate, String queryString) {
        return sparqlTemplate.query(queryString, "trait");
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


    private String generateSessionID() {
        if(this.sessionID == null) {
            String timestamp = Long.toString(System.currentTimeMillis());
            getLog().debug("Generating new session ID for session created at " + timestamp);
            try {
                // encode the email using SHA-1
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                byte[] digest = messageDigest.digest(timestamp.getBytes("UTF-8"));

                // now translate the resulting byte array to hex
                String restKey = StringUtils.getHexRepresentation(digest);
                getLog().debug("Session ID was generated: " + restKey);
                return restKey;
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("UTF-8 not supported!");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-1 algorithm not available, required to generate session ID");
            }
        }
        else{
            getLog().debug("Session ID already exists " + this.sessionID);
            return this.sessionID;
        }
    }

}
