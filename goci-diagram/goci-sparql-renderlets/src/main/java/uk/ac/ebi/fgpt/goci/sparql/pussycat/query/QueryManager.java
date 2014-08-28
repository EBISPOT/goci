package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.QuerySolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convenience singleton class to access common SPARQL queries required in rendering and to cache the results for
 * subsequent lookup.
 */
public class QueryManager {
    private static final String BAND_FOR_ASSOCIATION =
            "SELECT ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER (?association = ??) }";
    private static final String ASSOCIATIONS_IN_BAND =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER (?band = ??) }";
    private static final String ASSOCIATIONS_IN_BAND_NAME =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp . " +
                    "?snp ro:located_in ?bandUri . " +
                    "?bandUri rdfs:label ?band . " +
                    "FILTER ( ?band = ?? ) }";
    private static final String TRAITS_IN_BAND =
            "SELECT ?trait ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait . " +
                    "?snp ro:located_in ?band ; " +
                    "FILTER (?band = ??) }";
    private static final String ASSOCIATIONS_FOR_TRAIT =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_object ?trait . " +
                    "FILTER (?trait = ??) }";

    private static final QueryManager instance = new QueryManager();

    public static QueryManager getCachingInstance() {
        return instance;
    }

    private final Map<List<Object>, Object> requestCache;
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private QueryManager() {
        this.requestCache = new HashMap<List<Object>, Object>();
    }

    public URI getCytogeneticBandForAssociation(SparqlTemplate sparqlTemplate, URI association) throws
            DataIntegrityViolationException {
        List<URI> results = sparqlTemplate.query(BAND_FOR_ASSOCIATION, new URIMapper("band"), association);
        if (results.size() == 1) {
            return results.get(0);
        }
        else {
            if (results.size() > 1) {
                throw new DataIntegrityViolationException("More than one band for association '" + association + "'");
            }
            else {
                throw new DataIntegrityViolationException("No band for association '" + association + "'");
            }
        }
    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI bandIndividual) {
        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_IN_BAND, new URIMapper("association"), bandIndividual));
        return results;
    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, String bandName) {
        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_IN_BAND_NAME, new URIMapper("association"), bandName));
        return results;
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI band) {
        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(TRAITS_IN_BAND, new URIMapper("trait"), band));
        return results;
    }

    public Set<URI> getAssociationsForTrait(SparqlTemplate sparqlTemplate, URI trait) {
        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_FOR_TRAIT, new URIMapper("association"), trait));
        return results;
    }

    public BandInformation getBandInformation(SparqlTemplate sparqlTemplate, URI bandIndividual)
            throws DataIntegrityViolationException {
        return new BandInformation(sparqlTemplate.label(bandIndividual));
    }

    private Object checkCache(String methodName, Object... arguments) {
        synchronized (requestCache) {
            List<Object> key = new ArrayList<Object>();
            key.add(methodName);
            Collections.addAll(key, arguments);
            if (requestCache.containsKey(key)) {
                return requestCache.get(key);
            }
            else {
                return null;
            }
        }
    }

    private <O> O cache(O result, String methodName, Object... arguments) {
        synchronized (requestCache) {
            List<Object> key = new ArrayList<Object>();
            key.add(methodName);
            Collections.addAll(key, arguments);
            requestCache.put(key, result);
            return result;
        }
    }
}

