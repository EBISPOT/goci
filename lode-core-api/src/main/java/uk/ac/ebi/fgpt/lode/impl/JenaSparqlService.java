/*
 * Copyright (c) 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.service.SparqlServiceDescription;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;
import uk.ac.ebi.fgpt.lode.utils.TupleQueryFormats;

import java.io.OutputStream;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaSparqlService implements SparqlService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${lode.sparql.query.maxlimit}")
    private int maxQueryLimit = -1;

    private SparqlServiceDescription sparqlServiceDescription;

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Integer getMaxQueryLimit() {
        return maxQueryLimit;
    }

    public void setMaxQueryLimit(Integer maxQueryLimit) {
        this.maxQueryLimit = maxQueryLimit;
    }

    private JenaQueryExecutionService queryExecutionService;

    public JenaQueryExecutionService getQueryExecutionService() {
        return queryExecutionService;
    }

    public void setQueryExecutionService(JenaQueryExecutionService queryExecutionService) {
        this.queryExecutionService = queryExecutionService;
    }

    public void query(String query, String format, Integer offset, Integer limit, boolean inference, OutputStream output) throws LodeException {

        try {

            Query q1 = QueryFactory.create(query, Syntax.syntaxARQ);
            QueryType qtype = getQueryType(query);

            // detect format

            if (qtype.equals(QueryType.TUPLEQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = TupleQueryFormats.XML.toString();
                }
                executeTupleQuery(q1, format, offset, limit, inference, output);
            }
            else if (qtype.equals(QueryType.DESCRIBEQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = GraphQueryFormats.RDFXML.toString();
                }
                executeDescribeQuery(q1, format, output);
            }
            else if (qtype.equals(QueryType.CONSTRUCTQUERY)) {
                if (isNullOrEmpty(format)) {
                    format = GraphQueryFormats.RDFXML.toString();
                }
                executeConstructQuery(q1, format, output);
            }
            else if (qtype.equals((QueryType.BOOLEANQUERY))) {
                if (isNullOrEmpty(format)) {
                    format = TupleQueryFormats.XML.toString();
                }
                executeBooleanQuery(q1, format, output);
            }
            else {
                // unknown query type
                log.error("Invalid query type: " + query);
                throw new LodeException("Invalid query type, must be one of TUPLE, DESCRIBE, CONSTRUCT or BOOLEAN");
            }


        } catch (QueryParseException e) {
            throw new LodeException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LodeException(e.getMessage());
        }

    }

    public void query(String query, String format, boolean inference, OutputStream output) throws LodeException {
        query(query, format, 0, getMaxQueryLimit(), inference, output);
    }

    public void getServiceDescription(OutputStream outputStream, String format) {

        // todo implement this properly
        String  q = "CONSTRUCT  {\n" +
                "?s ?p ?o\n" +
                "}\n" +
                "WHERE {\n" +
                "?s a <http://www.w3.org/ns/sparql-service-description#Service> .\n" +
                "?s ?p ?o \n" +
                "}";
        Query q1 = QueryFactory.create(q);
        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();
        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model m = endpoint.execConstruct();

            if (!m.listStatements().hasNext()) {
                //            Model model = ModelFactory.createDefaultModel();

            }
            else {
                m.write(outputStream, format);
            }
            endpoint.close();
            if (g!=null) {
                g.close();
            }
        } catch (LodeException e) {
            log.error(e.getMessage(), e);
        }

    }

    public QueryType getQueryType(String query) {
        // detect query type
        Query q1 = QueryFactory.create(query, Syntax.syntaxARQ);

        return QueryType.getQueryType(q1);

    }

    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        }
        return "".equals(o);
    }

    private void executeConstructQuery(Query q1, String format, OutputStream output) {
        long startTime = System.currentTimeMillis();
        log.info("preparing to execute describe query: " + startTime+ "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model model = endpoint.execConstruct();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("describe query" +  startTime+ " finished in :" + elapsedTime  + " milliseconds");

            model.write(output, format);
            model.close();

        }  catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }

    }


    private void executeTupleQuery(Query q1, String format, Integer offset, Integer limit, boolean inference, OutputStream output)  {

        // check the limit is not greater that the max
        if (getMaxQueryLimit() > -1) {
            if (limit != null) {
                if (limit < getMaxQueryLimit()) {
                    q1.setLimit(limit);
                }
                else {
                    q1.setLimit(getMaxQueryLimit());
                }
            }
            else if (q1.hasLimit()) {
                if (q1.getLimit() > getMaxQueryLimit()) {
                    q1.setLimit(getMaxQueryLimit());
                }
            }
        }
        else {
            if (limit!= null &&limit >-1) {
                q1.setLimit(limit);
            }
        }


        // set any offset
        if (offset != null) {
            q1.setOffset(offset);
        }

        long startTime = System.currentTimeMillis();
        log.info("preparing to execute tuple query: " + startTime + "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();
        try {

            endpoint = getQueryExecutionService().getQueryExecution(g, q1, inference);
            ResultSet results = endpoint.execSelect();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("query" + startTime + " finished in :" + elapsedTime + " milliseconds");

            if (format.equals(TupleQueryFormats.JSON.toString())) {
                ResultSetFormatter.outputAsJSON(output, results);
            }
            else if (format.equals(TupleQueryFormats.CSV.toString())) {
                ResultSetFormatter.outputAsCSV(output, results);
            }
            else if (format.equals(TupleQueryFormats.TSV.toString())) {
                ResultSetFormatter.outputAsTSV(output, results);
            }
            else {
                ResultSetFormatter.outputAsXML(output, results);
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }

    }


    private void executeDescribeQuery(Query q1, String format, OutputStream output)  {

        long startTime = System.currentTimeMillis();
        log.info("preparing to execute describe query: " + startTime+ "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            Model model = endpoint.execDescribe();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("describe query" + startTime + " finished in :" + elapsedTime + " milliseconds");
            model.write(output, format);
            model.close();

        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }
    }


    private void executeBooleanQuery(Query q1, String format, OutputStream output) {

        long startTime = System.currentTimeMillis();
        log.info("preparing to execute ASK query: " + startTime+ "\n" + q1.serialize());

        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, q1, false);
            boolean value = endpoint.execAsk();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            log.info("ASK query" +  startTime+ " finished in :" + elapsedTime  + " milliseconds");
            if (format.equals(TupleQueryFormats.JSON.toString())) {
                ResultSetFormatter.outputAsJSON(output, value);
            }
            else if (format.equals(TupleQueryFormats.CSV.toString())) {
                ResultSetFormatter.outputAsCSV(output, value);
            }
            else if (format.equals(TupleQueryFormats.TSV.toString())) {
                ResultSetFormatter.outputAsTSV(output, value);
            }
            else {
                ResultSetFormatter.outputAsXML(output, value);
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + q1, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g!=null) {
                    g.close();
                }
            }
        }
    }







}
