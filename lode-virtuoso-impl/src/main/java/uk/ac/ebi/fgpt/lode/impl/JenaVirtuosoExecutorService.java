package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaVirtuosoExecutorService implements JenaQueryExecutionService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${lode.explorer.virtuoso.user}")
    private String virtuosoUser;

    @Value("${lode.explorer.virtuoso.password}")
    private String virtuosoPassword;

    @Value("${lode.explorer.virtuoso.inferencerule}")
    private String virtuosoInferenceRule;

    @Value("${lode.explorer.virtuoso.allgraphs}")
    private boolean virtuosoAllGraphs;

    public String getEndpointURL() {
        return endpointURL;
    }

    public void close() {

    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    @Value("${lode.sparqlendpoint.url}")
    private String endpointURL;

    public String getVirtuosoUser() {
        return virtuosoUser;
    }

    public String getVirtuosoPassword() {
        return virtuosoPassword;
    }

    public String getVirtuosoInferenceRule() {
        return virtuosoInferenceRule;
    }

    public void setVirtuosoInferenceRule(String virtuosoInferenceRule) {
        this.virtuosoInferenceRule = virtuosoInferenceRule;
    }

    public boolean isVirtuosoAllGraphs() {
        return virtuosoAllGraphs;
    }

    public void setVirtuosoAllGraphs(boolean virtuosoAllGraphs) {
        this.virtuosoAllGraphs = virtuosoAllGraphs;
    }


    public QueryExecution getQueryExecution(Graph g, Query query, boolean withInference) throws LodeException {
        if (isNullOrEmpty(getEndpointURL())) {
            log.error("No sparql endpoint");
            throw new LodeException("You must specify a SPARQL endpoint URL");
        }
        VirtGraph set =  (VirtGraph) g;
        set.setReadFromAllGraphs(isVirtuosoAllGraphs());
        if (withInference) {
            set.setRuleSet(getVirtuosoInferenceRule());
        }
        if (query.isDescribeType()) {
            /** todo this is a hack to get virtuoso describe queries
             *  for concise bound description of given subject (i.e., SPO + CBD of each blank node object found by SPO, recursively);
             **/
            String squery = "DEFINE sql:describe-mode \"CBD\"\n" + query.serialize();
            return virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(squery, set);
        }
        return VirtuosoQueryExecutionFactory.create(query, set);
    }

    public QueryExecution getQueryExecution(Graph g, String query, QuerySolutionMap initialBinding, boolean withInference) throws LodeException {
        if (isNullOrEmpty(getEndpointURL())) {
            log.error("No sparql endpoint");
            throw new LodeException("You must specify a SPARQL endpoint URL");
        }
        VirtGraph set =  (VirtGraph) g;
        set.setReadFromAllGraphs(isVirtuosoAllGraphs());
        if (withInference) {
            set.setRuleSet(getVirtuosoInferenceRule());
        }
        VirtuosoQueryExecution execution = VirtuosoQueryExecutionFactory.create(query, set);
        execution.setInitialBinding(initialBinding);
        return execution;
    }

    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        }
        return "".equals(o);
    }

    public Graph getDefaultGraph() {
        return new VirtGraph(getEndpointURL(), getVirtuosoUser() , getVirtuosoPassword());
    }

    public Graph getNamedGraph(String graphName) {
        return new VirtGraph(graphName, getEndpointURL(), getVirtuosoUser() , getVirtuosoPassword());
    }

}
