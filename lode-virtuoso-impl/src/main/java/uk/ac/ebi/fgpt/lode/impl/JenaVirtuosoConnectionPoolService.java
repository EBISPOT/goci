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
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolutionMap;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.fgpt.lode.utils.DatasourceProvider;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Simon Jupp
 * @date 19/07/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaVirtuosoConnectionPoolService implements JenaQueryExecutionService {


    @Value("${lode.explorer.virtuoso.inferencerule}")
    private String virtuosoInferenceRule;

    @Value("${lode.explorer.virtuoso.allgraphs}")
    private boolean virtuosoAllGraphs;

    @Value("${lode.explorer.virtuoso.querytimeout}")
    private int virtuosoQueryTimeout;


    public String getVirtuosoInferenceRule() {
        return virtuosoInferenceRule;
    }

    public int getVirtuosoQueryTimeout() {
        return virtuosoQueryTimeout;
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

    private DatasourceProvider datasourceProvider;

    public JenaVirtuosoConnectionPoolService(DatasourceProvider provider) {
        this.datasourceProvider  = provider;

    }

    public Graph getNamedGraph(String graphName) {
        virtuoso.jena.driver.VirtGraph set = null;
        DataSource source = null;
        try {

            source = datasourceProvider.getDataSource();
            VirtGraph g;
            if (graphName != null) {
                g = new VirtGraph(graphName, source);
            }
            else {
                g = new VirtGraph(source);
            }
            g.setQueryTimeout(getVirtuosoQueryTimeout());
            g.setReadFromAllGraphs(isVirtuosoAllGraphs());

            return g;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Can't create Virtuoso graph from datasource");
    }

    public Graph getDefaultGraph() {
        return getNamedGraph(null);
    }

    public VirtuosoQueryExecution getQueryExecution(Graph g, Query query, boolean withInference) throws LodeException {

        VirtGraph set = (VirtGraph) g;
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
        return virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(query, set);


    }

    public VirtuosoQueryExecution getQueryExecution(Graph g, String query, QuerySolutionMap initialBinding, boolean withInference) throws LodeException {

        virtuoso.jena.driver.VirtGraph set = (VirtGraph) g;

        set.setReadFromAllGraphs(isVirtuosoAllGraphs());
        if (withInference) {
            set.setRuleSet(getVirtuosoInferenceRule());
        }
        virtuoso.jena.driver.VirtuosoQueryExecution execution = virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(query, set);
        execution.setInitialBinding(initialBinding);
        return execution;
    }
}


