package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.fgpt.lode.utils.ParameterizedSparqlString;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaHttpExecutorService implements JenaQueryExecutionService {

    public QueryExecution getQueryExecution(String serviceUri, Query q1, boolean withInerence) {
        return new QueryEngineHTTP(serviceUri, q1);
    }

    public QueryExecution getQueryExecution(String serviceUri, String query, QuerySolutionMap initialBinding, boolean withInerence) {

        ParameterizedSparqlString sparql = new ParameterizedSparqlString(query, initialBinding);
        System.out.println(sparql.asQuery());
        return new QueryEngineHTTP(serviceUri, sparql.asQuery());
    }
}
