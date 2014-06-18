package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaHttpExecutorService implements JenaQueryExecutionService {

    private Logger log = LoggerFactory.getLogger(getClass());

    public JenaHttpExecutorService() {
    }

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    @Value("${lode.sparqlendpoint.url}")
    private String endpointURL;

    public JenaHttpExecutorService(String sparqlEndpoint) {
        this.endpointURL = sparqlEndpoint;
    }

    public QueryExecution getQueryExecution(Graph g, Query q1, boolean withInference) throws LodeException{
        if (isNullOrEmpty(getEndpointURL())) {
            log.error("No sparql endpoint");
            throw new LodeException("You must specify a SPARQL endpoint URL");
        }
        return new QueryEngineHTTP(getEndpointURL(), q1);
    }

    public QueryExecution getQueryExecution(Graph g, String query, QuerySolutionMap initialBinding, boolean withInference)  throws LodeException{
        if (isNullOrEmpty(getEndpointURL())) {
            log.error("No sparql endpoint");
            throw new LodeException("You must specify a SPARQL endpoint URL");
        }
        ParameterizedSparqlString sparql = new ParameterizedSparqlString(query, initialBinding);
        System.out.println(sparql.asQuery());
        return new QueryEngineHTTP(getEndpointURL(), sparql.asQuery());
    }


    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        }
        return "".equals(o);
    }

    public Graph getDefaultGraph() {
        return null;

    }

    public Graph getNamedGraph(String graphName) {
        return null;
    }

}
