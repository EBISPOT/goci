package uk.ac.ebi.fgpt.lode.service;

import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.utils.QueryType;

import java.io.OutputStream;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 *
 * A interface to for a basic SPARQL service.
 *
 */
public interface SparqlService {

    /**
     * Set the underlying SPARQL endpoint URL
     * @param endpointURL SPARQL endpoint URL
     */
    void setEndpointURL(String endpointURL);

    /**
     * Get the underlying SPARQL endpoint URL
     * @return SPARQL endpoint URL
     */
    String getEndpointURL();

    /**
     * Set the max number of results returned by the SPARQL service
     * @param limit max number of results returned
     */
    void setMaxQueryLimit(Integer limit);

    /**
     * Ger the max number of results returned by the SPARQL service
     * @return Integer max number of results returned
     */
    Integer getMaxQueryLimit();

    /**
     * Execute a SPARQL query and render results to an OutputStream
     *
     * @param query The SPARQL query
     * @param format The output format See GraphQueryFormats and TupleQueryFormats
     * @param offset Any offest set or the query, default is zero
     * @param limit Any limit set on this query
     * @param inference Use inference to answer query (not supported by all implementations)
     * @param output The output stream for the results
     * @throws LodeException All exceptions are wrapped in a LodeException
     */
    void query(String query, String format, Integer offset, Integer limit, boolean inference, OutputStream output) throws LodeException;

    /**
     * Execute a SPARQL query and render results to an OutputStream
     *
     * @param query The SPARQL query
     * @param format The output format See GraphQueryFormats and TupleQueryFormats
     * @param inference Use inference to answer query (not supported by all implementations)
     * @param output The output stream for the results
     * @throws LodeException All exceptions are wrapped in a LodeException
     */
    void query(String query, String format, boolean inference, OutputStream output) throws LodeException;

    /**
     * Returns a description of the SPARQL endpoint (http://www.w3.org/TR/sparql11-service-description/)
     *
     * @param outputStream The output stream for the results
     * @param format  format of the description
     */
    void getServiceDescription(OutputStream outputStream, String format);

    /**
     * Get the type of query, one of
     * QueryType.DESCRIBEQUERY,
     * QueryType.CONSTRUCTQUERY,
     * QueryType.TUPLEQUERY,
     * QueryType.BOOLEANQUERY,
     * @param query
     * @return
     */
    QueryType getQueryType(String query);
}
