package uk.ac.ebi.spot.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.QuerySolution;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 27/08/14
 */
public interface QuerySolutionMapper<T> {
    public T mapQuerySolution(QuerySolution qs);
}
