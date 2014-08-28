package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.QuerySolution;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 28/08/14
 */
public class URIMapper implements QuerySolutionMapper<URI> {
    private final String fieldName;

    public URIMapper(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override public URI mapQuerySolution(QuerySolution querySolution) {
        return URI.create(querySolution.getResource(fieldName).getURI());
    }
}
