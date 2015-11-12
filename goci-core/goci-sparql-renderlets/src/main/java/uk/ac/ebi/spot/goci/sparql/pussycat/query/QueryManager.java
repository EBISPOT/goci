package uk.ac.ebi.spot.goci.sparql.pussycat.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.layout.BandInformation;

import java.net.URI;
import java.util.*;

/**
 * Convenience singleton class to access common SPARQL queries required in rendering and to cache the results for
 * subsequent lookup.
 */
public class QueryManager {

    private static final QueryManager instance = new QueryManager();

    public static QueryManager getCachingInstance() {
        return instance;
    }

    private final Map<List<Object>, Object> requestCache;
    private final Logger log = LoggerFactory.getLogger("rendering");

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

        List<URI> results = sparqlTemplate.query(SparqlQueries.BAND_FOR_ASSOCIATION, new URIMapper("band"), association);
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

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI bandIndividual, List<Filter> filters) {

        Set<URI> results = new HashSet<URI>();

        if(filters.size() == 0){
            Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND, new URIMapper("association"), bandIndividual));
            return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);

        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_PVALUE_FILTER, new URIMapper("association"),
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_DATE_FILTER, new URIMapper("association"),
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_PVALUE_DATE_FILTER, new URIMapper("association"),
                    bandIndividual, pval_max, pval_min, date_max, date_min));
            return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                    bandIndividual, pval_max, pval_min, date_max, date_min);
        }

        return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);

    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, String bandName, List<Filter> filters) {

        Set<URI> results = new HashSet<URI>();

        if(filters.size() == 0){
            Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_NAME, new URIMapper("association"), bandName));
            return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName);

        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_NAME_PVALUE_FILTER, new URIMapper("association"),
                            bandName, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                            bandName, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_NAME_DATE_FILTER, new URIMapper("association"),
                            bandName, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                            bandName, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_IN_BAND_NAME_PVALUE_DATE_FILTER, new URIMapper("association"),
                    bandName, pval_max, pval_min, date_max, date_min));
            return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate,
                    bandName, pval_max, pval_min, date_max, date_min);
        }

        return cache(results, "getAssociationsLocatedInCytogeneticBand", sparqlTemplate, bandName);
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI bandIndividual, List<Filter> filters) {

        Set<URI> results = new HashSet<URI>();

        if(filters.size() == 0){
            Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }
            results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND, new URIMapper("trait"), bandIndividual));
            return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);

        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_NAME_PVALUE_FILTER, new URIMapper("trait"),
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_DATE_FILTER, new URIMapper("trait"),
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_PVALUE_DATE_FILTER, new URIMapper("trait"),
                    bandIndividual, pval_max, pval_min, date_max, date_min));
            return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                    bandIndividual, pval_max, pval_min, date_max, date_min);
        }


        return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandIndividual);
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, String bandName, List<Filter> filters) {
        Set<URI> results = new HashSet<URI>();

        if(filters.size() == 0){
            Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_NAME, new URIMapper("trait"), bandName));
            return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName);

        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_NAME_PVALUE_FILTER, new URIMapper("trait"),
                            bandName, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                            bandName, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_NAME_DATE_FILTER, new URIMapper("trait"),
                            bandName, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                            bandName, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.TRAITS_IN_BAND_NAME_PVALUE_DATE_FILTER, new URIMapper("trait"),
                    bandName, pval_max, pval_min, date_max, date_min));
            return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate,
                    bandName, pval_max, pval_min, date_max, date_min);
        }
        return cache(results, "getTraitsLocatedInCytogeneticBand", sparqlTemplate, bandName);
    }

    public List<URI> getTraitsOrderedByIdentificationDateForBand(SparqlTemplate sparqlTemplate, URI bandIndividual, List<Filter> filters) {
        List<URI> results = new ArrayList<URI>();

        if(filters.size() == 0) {
            Object retrieved = checkCache("getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual);
            if (retrieved != null) {
                return (List<URI>) retrieved;
            }

            List<URI> queryResults =
                    sparqlTemplate.query(SparqlQueries.DATE_OF_TRAIT_ID_FOR_BAND, new URIMapper("trait"), bandIndividual);
            // de-duplicate results; should be handled by        List<URI> results = new ArrayList<URI>();
            for (URI queryResult : queryResults) {
                if (!results.contains(queryResult)) {
                    results.add(queryResult);
                }
            }
            return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual);
        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (List<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.DATE_OF_TRAIT_ID_FOR_BAND_PVALUE_FILTER, new URIMapper("trait"),
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (List<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.DATE_OF_TRAIT_ID_FOR_BAND_DATE_FILTER, new URIMapper("trait"),
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (List<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.DATE_OF_TRAIT_ID_FOR_BAND_PVALUE_DATE_FILTER, new URIMapper("trait"),
                    bandIndividual, pval_max, pval_min, date_max, date_min));
            return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate,
                    bandIndividual, pval_max, pval_min, date_max, date_min);
        }

        return cache(results, "getTraitsOrderedByIdentificationDateForBand", sparqlTemplate, bandIndividual);
    }

    public Set<URI> getAssociationsForTrait(SparqlTemplate sparqlTemplate, URI trait, List<Filter> filters) {

        Set<URI> results = new HashSet<URI>();

        if(filters.size() == 0){
            Object retrieved = checkCache("getAssociationsForTrait", sparqlTemplate, trait);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }
            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT, new URIMapper("association"), trait));
            return cache(results, "getAssociationsForTrait", sparqlTemplate, trait);

        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getAssociationsForTrait", sparqlTemplate, trait ,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_PVALUE_FILTER, new URIMapper("association"),
                            trait, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getAssociationsForTrait", sparqlTemplate,
                            trait, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getAssociationsForTrait", sparqlTemplate, trait,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (Set<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_DATE_FILTER, new URIMapper("association"),
                            trait, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getAssociationsForTrait", sparqlTemplate,
                            trait, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getAssociationsForTrait", sparqlTemplate, trait, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (Set<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_PVALUE_DATE_FILTER, new URIMapper("association"),
                    trait, pval_max, pval_min, date_max, date_min));
            return cache(results, "getAssociationsForTrait", sparqlTemplate,
                    trait, pval_max, pval_min, date_max, date_min);
        }
        return cache(results, "getAssociationsForTrait", sparqlTemplate, trait);
    }

    public List<URI> getAssociationForTraitAndBand(SparqlTemplate sparqlTemplate, URI trait, URI bandIndividual, List<Filter> filters){
        List<URI> results = new ArrayList<URI>();

        if(filters.size() == 0) {
            Object retrieved = checkCache("getAssociationsForTraitInBand", sparqlTemplate, trait, bandIndividual);
            if (retrieved != null) {
                return (List<URI>) retrieved;
            }

            List<URI> queryResults =
                    sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_AND_BAND, new URIMapper("association"), trait, bandIndividual);
            // de-duplicate results; should be handled by        List<URI> results = new ArrayList<URI>();
            for (URI queryResult : queryResults) {
                if (!results.contains(queryResult)) {
                    results.add(queryResult);
                }
            }
            return cache(results, "getAssociationsForTraitInBand", sparqlTemplate, trait, bandIndividual);
        }
        else if(filters.size() == 1){
            for(Filter filter : filters){
                if(filter.getFilteredType().equals(Association.class)){
                    Object retrieved = checkCache("getAssociationsForTraitInBand", sparqlTemplate, bandIndividual,filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));
                    if (retrieved != null) {
                        return (List<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_AND_BAND_PVALUE_FILTER, new URIMapper("association"),
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0)));
                    return cache(results, "getAssociationsForTraitInBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredValues().get(1), filter.getFilteredValues().get(0));

                }
                else if(filter.getFilteredType().equals(Study.class)){
                    Object retrieved = checkCache("getAssociationsForTraitInBand", sparqlTemplate, bandIndividual,filter.getFilteredRange().to(), filter.getFilteredRange().from());
                    if (retrieved != null) {
                        return (List<URI>) retrieved;
                    }

                    results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_AND_BAND_DATE_FILTER, new URIMapper("association"),
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from()));
                    return cache(results, "getAssociationsForTraitInBand", sparqlTemplate,
                            bandIndividual, filter.getFilteredRange().to(), filter.getFilteredRange().from());

                }
            }
        }
        else{
            Object pval_min = null, pval_max = null, date_min = null, date_max = null;
            for (Filter filter : filters) {
                if (filter.getFilteredType().equals(Association.class)) {
                    pval_min = filter.getFilteredValues().get(0);
                    pval_max = filter.getFilteredValues().get(1);
                }
                else if(filter.getFilteredType().equals(Study.class)){
                    date_min = filter.getFilteredRange().from();
                    date_max = filter.getFilteredRange().to();
                }
            }
            Object retrieved = checkCache("getAssociationsForTraitInBand", sparqlTemplate, bandIndividual, pval_max, pval_min, date_max, date_min);
            if (retrieved != null) {
                return (List<URI>) retrieved;
            }

            results.addAll(sparqlTemplate.query(SparqlQueries.ASSOCIATIONS_FOR_TRAIT_AND_BAND_PVALUE_DATE_FILTER, new URIMapper("association"),
                    bandIndividual, pval_max, pval_min, date_max, date_min));
            return cache(results, "getAssociationsForTraitInBand", sparqlTemplate,
                    bandIndividual, pval_max, pval_min, date_max, date_min);
        }

        return cache(results, "getAssociationsForTraitInBand", sparqlTemplate, trait, bandIndividual);
    }

    public URI getTraitByName(SparqlTemplate sparqlTemplate, String name) throws DataIntegrityViolationException {
        Object retrieved = checkCache("getTraitByName", sparqlTemplate, name);
        if (retrieved != null) {
            return (URI) retrieved;
        }

        // todo - lowercase here?
        List<URI> results = sparqlTemplate.query(SparqlQueries.TRAITS_BY_NAME, new URIMapper("trait"), name, name);
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

//        List<URI> results = sparqlTemplate.query("SELECT ?type (count(DISTINCT ?ancestor) as ?count) " +
//                                                         "WHERE { " +
//                                                         "<" + trait.toString() + "> rdf:type ?trait . " +
//                                                         "?trait rdfs:subClassOf* ?type . " +
//                                                         "?type rdfs:subClassOf* ?ancestor . " +
//                                                         "FILTER ( ?trait != owl:Class ) .  " +
//                                                         "FILTER ( ?trait != owl:NamedIndividual ) . } " +
//                                                         "GROUP BY ?type " +
//                                                         "ORDER BY desc(?count) ", new URIMapper("type"));

        List<URI> results = sparqlTemplate.query(SparqlQueries.PARENTS_AND_DISTANCE_BY_TRAIT, new URIMapper("type"), trait);
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

