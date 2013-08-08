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
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import javax.sql.ConnectionPoolDataSource;
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

    private VirtuosoDatasourceProvider datasourceProvider;

    public JenaVirtuosoConnectionPoolService(VirtuosoDatasourceProvider provider) {
        this.datasourceProvider  = provider;

    }

    public Graph getDefaultGraph() {

        virtuoso.jena.driver.VirtGraph set = null;
        ConnectionPoolDataSource source = null;
        try {
            source = datasourceProvider.getVirtuosoDataSource();
            return new virtuoso.jena.driver.VirtGraph(source);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public VirtuosoQueryExecution getQueryExecution(Graph g, Query query, boolean withInference) throws LodeException {

        if (g instanceof VirtGraph) {
            virtuoso.jena.driver.VirtGraph set = (VirtGraph) g;

            set.setReadFromAllGraphs(isVirtuosoAllGraphs());
            if (withInference) {
                set.setRuleSet(getVirtuosoInferenceRule());
            }
            return virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(query, set);
        }
        return null;


    }

    public VirtuosoQueryExecution getQueryExecution(Graph g, String query, QuerySolutionMap initialBinding, boolean withInference) throws LodeException {

        if (g instanceof VirtGraph) {
            virtuoso.jena.driver.VirtGraph set = (VirtGraph) g;

            set.setReadFromAllGraphs(isVirtuosoAllGraphs());
            if (withInference) {
                set.setRuleSet(getVirtuosoInferenceRule());
            }
            virtuoso.jena.driver.VirtuosoQueryExecution execution = virtuoso.jena.driver.VirtuosoQueryExecutionFactory.create(query, set);
            execution.setInitialBinding(initialBinding);
            return execution;
        }
        return null;
    }

}
