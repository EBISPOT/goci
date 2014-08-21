package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private QueryManager() {
        this.requestCache = new HashMap<List<Object>, Object>();
    }


    // todo - querying code goes here!

    public URI getCytogeneticBandForAssociation(SparqlTemplate sparqlTemplate, URI association) throws
            DataIntegrityViolationException {
        return null;
    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate reasoner, URI bandIndividual) {
        return null;
    }

    public Set<URI> getAssociationsLocatedInCytogeneticBand(SparqlTemplate reasoner, String bandName) {
        return null;
    }

    public Set<URI> getTraitsLocatedInCytogeneticBand(SparqlTemplate sparqlTemplate, URI band) {
        return null;
    }

    public Set<URI> getAssociationsForTrait(SparqlTemplate sparqlTemplate, URI trait) {
        return null;
    }

    public BandInformation getBandInformation(SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        return null;
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

