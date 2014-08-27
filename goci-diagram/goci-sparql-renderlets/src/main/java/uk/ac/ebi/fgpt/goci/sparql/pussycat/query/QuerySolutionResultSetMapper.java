package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.query.ResultSet;
import uk.ac.ebi.fgpt.goci.sparql.exception.SparqlQueryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 27/08/14
 */
public class QuerySolutionResultSetMapper<T> implements ResultSetMapper<List<T>> {
    private final QuerySolutionMapper<T> querySolutionMapper;

    public QuerySolutionResultSetMapper(QuerySolutionMapper<T> querySolutionMapper) {
        this.querySolutionMapper = querySolutionMapper;
    }

    public List<T> mapResultSet(ResultSet rs) throws SparqlQueryException {
        List<T> results = new ArrayList<T>();
        while (rs.hasNext()) {
            results.add(this.querySolutionMapper.mapQuerySolution(rs.next()));
        }
        return results;
    }
}
