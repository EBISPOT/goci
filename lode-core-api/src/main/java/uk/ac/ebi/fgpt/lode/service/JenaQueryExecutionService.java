package uk.ac.ebi.fgpt.lode.service;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolutionMap;

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
     * Gte a Jena QueryExecution object
     * @param serviceUri The SPARQL endpoint URI
     * @param query The SPARQL query
     * @param withInference Use inference (note, this may make no difference on certain implementations)
     * @return A Jena QueryExecution object
     */
    QueryExecution getQueryExecution(String serviceUri, Query query, boolean withInference);

    /**
     *
     * @param serviceUri The SPARQL endpoint URI
     * @param query The SPARQL query
     * @param binding A Jena QuerySolutionMap containing any binding information for the SPARQL query
     * @param withInference Use inference (note, this may make no difference on certain implementations)
     * @return A Jena QueryExecution object
     * @return
     */
    QueryExecution getQueryExecution(String serviceUri, String query, QuerySolutionMap binding, boolean withInference);
}
