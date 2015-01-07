package uk.ac.ebi.spot.goci.sparql.pussycat.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.spot.goci.pussycat.layout.BandInformation;

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
    private static final String TRAITS_IN_BAND_NAME =
            "SELECT ?trait ?band " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait . " +
                    "?snp ro:located_in ?bandUri . " +
                    "?bandUri rdfs:label ?band . " +
                    "FILTER (?band = ??) }";
    private static final String DATE_OF_TRAIT_ID_FOR_BAND =
            "SELECT DISTINCT ?trait (min(?date) as ?first) " +
                    "WHERE { " +
                    "?association a gt:TraitAssociation ; oban:has_subject ?snp ; oban:has_object ?trait ; ro:part_of ?study . " +
                    "?study gt:has_publication_date ?date . " +
                    "?snp ro:located_in ?band . " +
                    "FILTER ( ?band = ?? ) } " +
                    "GROUP BY ?trait " +
                    "ORDER BY ?first";
    private static final String ASSOCIATIONS_FOR_TRAIT =
            "SELECT ?association " +
                    "WHERE { ?association a gt:TraitAssociation ; oban:has_object ?trait . " +
                    "FILTER (?trait = ??) }";
    private static final String TRAITS_BY_NAME =
            "SELECT DISTINCT ?trait " +
                    "WHERE {{ " +
                    "  ?trait rdfs:label ?label ." +
                    "  FILTER (?label = ??) " +
                    "} " +
                    "UNION { " +
                    "  ?trait efo:alternative_term ?synonym . " +
                    "  FILTER (?synonym = ??) " +
                    "}}";
    private static final String PARENTS_AND_DISTANCE_BY_TRAIT =
            "SELECT ?type (count(DISTINCT ?ancestor) as ?count) " +
                    "WHERE { " +
                    "?? rdf:type ?trait . " +
                    "?trait rdfs:subClassOf* ?type . " +
                    "?type rdfs:subClassOf* ?ancestor . " +
                    "FILTER ( ?trait != owl:Class ) .  " +
                    "FILTER ( ?trait != owl:NamedIndividual ) . } " +
                    "GROUP BY ?type " +
                    "ORDER BY desc(?count)";

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
        Object retrieved = checkCache("getCytogeneticBandForAssociation", sparqlTemplate, association);
        if (retrieved != null) {
            return (URI) retrieved;
        }

        List<URI> results = sparqlTemplate.query(BAND_FOR_ASSOCIATION, new URIMapper("band"), association);
        if (results.size() == 1) {
            return cache(results.get(0), "getCytogeneticBandForAssociation", sparqlTemplate, association);
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
        Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
        if (retrieved != null) {
            return (Set<URI>) retrieved;
        }

        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_IN_BAND, new URIMapper("association"), bandIndividual));
        return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, String bandName) {
        Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName);
        if (retrieved != null) {
            return (Set<URI>) retrieved;
        }

        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_IN_BAND_NAME, new URIMapper("association"), bandName));
        return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName);
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI bandIndividual) {
        Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
        if (retrieved != null) {
            return (Set<URI>) retrieved;
        }

        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(TRAITS_IN_BAND, new URIMapper("trait"), bandIndividual));
        return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, String bandName) {
        Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName);
        if (retrieved != null) {
            return (Set<URI>) retrieved;
        }

        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(TRAITS_IN_BAND_NAME, new URIMapper("trait"), bandName));
        return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName);
    }

    public List<URI> getTraitsOrderedByIdentificationDateForBand(SparqlTemplate sparqlTemplate, URI bandIndividual) {
        Object retrieved = checkCache("getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual);
        if (retrieved != null) {
            return (List<URI>) retrieved;
        }

        List<URI> queryResults =
                sparqlTemplate.query(DATE_OF_TRAIT_ID_FOR_BAND, new URIMapper("trait"), bandIndividual);
        // de-duplicate results; should be handled by the query but just in case...
        List<URI> results = new ArrayList<URI>();
        for (URI queryResult : queryResults) {
            if (!results.contains(queryResult)) {
                results.add(queryResult);
            }
        }
        return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual);
    }

    public Set<URI> getAssociationsForTrait(SparqlTemplate sparqlTemplate, URI trait) {
        Object retrieved = checkCache("getAssociationsForTrait", sparqlTemplate, trait);
        if (retrieved != null) {
            return (Set<URI>) retrieved;
        }

        Set<URI> results = new HashSet<URI>();
        results.addAll(sparqlTemplate.query(ASSOCIATIONS_FOR_TRAIT, new URIMapper("association"), trait));
        return cache(results, "getAssociationsForTrait", sparqlTemplate, trait);
    }

    public URI getTraitByName(SparqlTemplate sparqlTemplate, String name) throws DataIntegrityViolationException {
        Object retrieved = checkCache("getTraitByName", sparqlTemplate, name);
        if (retrieved != null) {
            return (URI) retrieved;
        }

        // todo - lowercase here?
        List<URI> results = sparqlTemplate.query(TRAITS_BY_NAME, new URIMapper("trait"), name, name);
        if (results.size() == 1) {
            return cache(results.get(0), "getTraitByName", sparqlTemplate, name);
        }
        else {
            if (results.size() > 1) {
                throw new DataIntegrityViolationException("More than one trait with label '" + name + "'");
            }
            else {
                throw new DataIntegrityViolationException("No trait with label '" + name + "'");
            }
        }
    }

    /**
     * Gets the list of all types of the supplied traits, ordered by specificity.  In other words,
     * traits are ordered so that the most specific asserted types are first, followed by each of the traits ancestors
     * from immediate parents to top level classes.
     *
     * @param sparqlTemplate the sparql template to use in the query
     * @param trait          the trait to identify types of
     * @return an ordered list of parents, most specific type first
     */
    public List<URI> getOrderedTraitTypes(SparqlTemplate sparqlTemplate, URI trait) {
        Object retrieved = checkCache("getAllTraitTypes", sparqlTemplate, trait);
        if (retrieved != null) {
            return (List<URI>) retrieved;
        }

        List<URI> results = sparqlTemplate.query("SELECT ?type (count(DISTINCT ?ancestor) as ?count) " +
                                                         "WHERE { " +
                                                         "<" + trait.toString() + "> rdf:type ?trait . " +
                                                         "?trait rdfs:subClassOf* ?type . " +
                                                         "?type rdfs:subClassOf* ?ancestor . " +
                                                         "FILTER ( ?trait != owl:Class ) .  " +
                                                         "FILTER ( ?trait != owl:NamedIndividual ) . } " +
                                                         "GROUP BY ?type " +
                                                         "ORDER BY desc(?count) ", new URIMapper("type"));

        return cache(results, "getAllTraitTypes", sparqlTemplate, trait);
    }

    public BandInformation getBandInformation(SparqlTemplate sparqlTemplate, URI bandIndividual)
            throws DataIntegrityViolationException {
        Object retrieved = checkCache("getBandInformation", sparqlTemplate, bandIndividual);
        if (retrieved != null) {
            return (BandInformation) retrieved;
        }

        return cache(new BandInformation(sparqlTemplate.label(bandIndividual)),
                     "getBandInformation",
                     sparqlTemplate,
                     bandIndividual);
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

