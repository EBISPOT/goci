package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.ResultSet;

import java.util.List;

/**
 * Maps the results of a sparql query to a corresponding object as determined by the type parameter.
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public interface ResultSetMapper<T> {
    public List<T> mapResults(ResultSet resultSet);
}
