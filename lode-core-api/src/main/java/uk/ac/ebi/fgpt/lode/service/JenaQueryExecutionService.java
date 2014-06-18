package uk.ac.ebi.fgpt.lode.service;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import uk.ac.ebi.fgpt.lode.exception.LodeException;

/**
 * @author Simon Jupp
 * @date 22/05/2013
 * Functional Genomics Group EMBL-EBI
 *
 * An interface for a Jena query execution services
 *
 */
public interface JenaQueryExecutionService {

    /**
     * Get default graph
     */
    Graph getDefaultGraph();

    /**
     * Get named graph
     */
    Graph getNamedGraph(String graphName);


    /**
     * Get a Jena QueryExecution object
     * @param query The SPARQL query
     * @param withInference Use inference (note, this may make no difference on certain implementations)
     * @return A Jena QueryExecution object
     */
    QueryExecution getQueryExecution(Graph g, Query query, boolean withInference) throws LodeException;

    /**
     *
     * @param query The SPARQL query
     * @param binding A Jena QuerySolutionMap containing any binding information for the SPARQL query
     * @param withInference Use inference (note, this may make no difference on certain implementations)
     * @return A Jena QueryExecution object
     * @return
     */
    QueryExecution getQueryExecution(Graph g, String query, QuerySolutionMap binding, boolean withInference) throws LodeException;

}
