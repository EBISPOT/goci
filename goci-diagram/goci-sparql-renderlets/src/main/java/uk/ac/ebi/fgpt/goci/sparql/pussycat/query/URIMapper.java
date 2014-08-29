package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.QuerySolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 28/08/14
 */
public class URIMapper implements QuerySolutionMapper<URI> {
    private final String fieldName;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public URIMapper(String fieldName) {
        this.fieldName = fieldName;
    }

    protected Logger getLog() {
        return log;
    }

    @Override public URI mapQuerySolution(QuerySolution querySolution) {
        if (querySolution.contains(fieldName)) {
            if (querySolution.get(fieldName).isAnon()) {
                return null;
            }
            else {
                return URI.create(querySolution.getResource(fieldName).getURI());
            }
        }
        else {
            throw new IllegalArgumentException("No resource with variable name ?" + fieldName + " present in results");
        }
    }
}
