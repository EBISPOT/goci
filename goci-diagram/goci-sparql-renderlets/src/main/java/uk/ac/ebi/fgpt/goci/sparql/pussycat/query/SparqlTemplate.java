package uk.ac.ebi.fgpt.goci.sparql.pussycat.query;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import uk.ac.ebi.fgpt.goci.sparql.exception.SparqlQueryException;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
public class SparqlTemplate {
    private JenaQueryExecutionService queryService;

    public JenaQueryExecutionService getQueryService() {
        return queryService;
    }

    public void setQueryService(JenaQueryExecutionService queryService) {
        this.queryService = queryService;
    }

    public boolean ask(URI instance, URI type) {
        String sparql = "ASK {<" + instance.toString() + "> a <" + type.toString() + ">}";
        Graph g = getQueryService().getDefaultGraph();
        Query q1 = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution execute = null;
        try {
            execute = getQueryService().getQueryExecution(g, q1, false);
            return execute.execAsk();
        }
        catch (LodeException e) {
            throw new SparqlQueryException("Failed to execute ask '" + sparql + "'", e);
        }
        finally {
            if (execute != null) {
                execute.close();
                if (g != null) {
                    g.close();
                }
            }
        }
    }

    public <T> List<T> query(String sparql, ResultSetMapper<T> rsm) {
        Graph g = getQueryService().getDefaultGraph();
        Query q1 = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution execute = null;
        try {
            execute = getQueryService().getQueryExecution(g, q1, false);
            ResultSet results = execute.execSelect();
            return rsm.mapResults(results);
        }
        catch (LodeException e) {
            throw new SparqlQueryException("Failed to execute query '" + sparql + "'", e);
        }
        finally {
            if (execute != null) {
                execute.close();
                if (g != null) {
                    g.close();
                }
            }
        }
    }

    public <T> List<T> query(String sparql, ResultSetMapper<T> rsm, Object... args) throws SparqlQueryException {
        Graph g = getQueryService().getDefaultGraph();
        Query q1 = QueryFactory.create(sparql, Syntax.syntaxARQ);

        if (args.length % 2 != 0) {
            throw new SparqlQueryException("Illegal number of arguments (" + args.length + ") - " +
                                                   "you must specify a series of bindings followed by the value");
        }

        Map<String, String> bindingMap = new HashMap<String, String>();
        String nextVar = null;
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) {
                if (!(args[i] instanceof String)) {
                    throw new SparqlQueryException(
                            "Invalid argument (binding of type " + args[i].getClass() + ") - " +
                                    "specify binding parameter name (e.g. ?s) followed by it's value");
                }
                else {
                    nextVar = (String) args[i];
                }
            }
            else {
                if (nextVar != null) {
                    bindingMap.put(nextVar, args[i].toString());
                }
            }
        }

        QuerySolutionMap initialBinding = new QuerySolutionMap();
        for (String variable : bindingMap.keySet()) {
            initialBinding.add(variable, new ResourceImpl(bindingMap.get(variable)));
        }
        ParameterizedSparqlString queryString = new ParameterizedSparqlString(q1.toString(), initialBinding);

        QueryExecution execute = null;
        try {
            execute = getQueryService().getQueryExecution(g, queryString.asQuery(), false);
            ResultSet results = execute.execSelect();
            return rsm.mapResults(results);
        }
        catch (LodeException e) {
            throw new SparqlQueryException("Failed to execute query '" + sparql + "'", e);
        }
        finally {
            if (execute != null) {
                execute.close();
                if (g != null) {
                    g.close();
                }
            }
        }
    }

    public List<URI> list(String s) {
        return null;
    }
}
