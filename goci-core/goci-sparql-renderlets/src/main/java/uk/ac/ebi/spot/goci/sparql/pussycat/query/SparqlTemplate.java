package uk.ac.ebi.spot.goci.sparql.pussycat.query;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.spot.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.spot.goci.sparql.exception.SparqlQueryException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@Component
public class SparqlTemplate {

    private JenaQueryExecutionService queryService;

    private String prefixes;

    private Properties prefixProperties;

    public JenaQueryExecutionService getQueryService() {
        return queryService;
    }

    @Autowired
    @Required
    public void setQueryService(JenaQueryExecutionService queryService) {
        this.queryService = queryService;
    }

    public String getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }

    public Properties getPrefixProperties() {
        return prefixProperties;
    }

    @Autowired
    @Required
    public void setPrefixProperties(Properties prefixProperties) {
        this.prefixProperties = prefixProperties;
    }

    public String getPrefixString() {
        if (getPrefixes() != null) {
            return getPrefixes();
        }
        else if (getPrefixProperties() != null) {
            StringBuilder sb = new StringBuilder();
            for (String prefix : getPrefixProperties().stringPropertyNames()) {
                sb.append("PREFIX ")
                        .append(prefix)
                        .append(":<")
                        .append(getPrefixProperties().get(prefix))
                        .append(">\n");
            }
            return sb.toString();
        }
        else {
            return "";
        }
    }

    public boolean ask(URI instance, URI type) {
        String sparql = "ASK {<" + instance.toString() + "> a <" + type.toString() + ">}";
        return ask(sparql);
    }

    public boolean ask(String sparql) {
        sparql = getPrefixString().concat(sparql);
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

    public List<URI> query(String sparql, String uriFieldName) {
        return query(sparql, new URIMapper(uriFieldName));
    }

    public <T> List<T> query(String sparql, QuerySolutionMapper<T> qsm) {
        return query(sparql, new QuerySolutionResultSetMapper<T>(qsm));
    }

    public <T> T query(String sparql, ResultSetMapper<T> rsm) {
        sparql = getPrefixString().concat(sparql);
        Graph g = getQueryService().getDefaultGraph();
        Query q1 = QueryFactory.create(sparql, Syntax.syntaxARQ);
        QueryExecution execute = null;
        try {
            execute = getQueryService().getQueryExecution(g, q1, false);
            ResultSet results = execute.execSelect();
            return rsm.mapResultSet(results);
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

    public List<URI> query(String sparql, String uriFieldName, Object... args) {
        return query(sparql, new URIMapper(uriFieldName), args);
    }

    public <T> List<T> query(String sparql, QuerySolutionMapper<T> qsm, Object... args) {
        return query(sparql, new QuerySolutionResultSetMapper<T>(qsm), args);
    }

    public <T> T query(String sparql, ResultSetMapper<T> rsm, Object... args) {
        sparql = getPrefixString().concat(sparql);
        Graph g = getQueryService().getDefaultGraph();

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        int i = 0;
        for (Object o : args) {
            String argName = "?_arg" + i++;
            sparql = sparql.replaceFirst("\\?\\?", argName);
            bindingMap.put(argName, o);
        }
        Query q1 = QueryFactory.create(sparql, Syntax.syntaxARQ);

        QuerySolutionMap initialBinding = new QuerySolutionMap();
        for (String argName : bindingMap.keySet()) {
            Object argValue = bindingMap.get(argName);
            RDFNode arg;
            if (argValue instanceof URI) {
                arg = new ResourceImpl(argValue.toString());
            }
            else {
                arg = getLiteralNode(argValue);
            }
            initialBinding.add(argName, arg);
        }
        ParameterizedSparqlString queryString = new ParameterizedSparqlString(q1.toString(), initialBinding);

        QueryExecution execute = null;
        try {
            execute = getQueryService().getQueryExecution(g, queryString.asQuery(), false);
            ResultSet results = execute.execSelect();
            return rsm.mapResultSet(results);
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

    public List<URI> list(URI type) {
        return query("SELECT DISTINCT ?uri WHERE { ?uri a <" + type.toString() + "> . FILTER (!isBlank(?uri)) }",
                     new QuerySolutionMapper<URI>() {
                         @Override public URI mapQuerySolution(QuerySolution qs) {
                             return URI.create(qs.getResource("uri").getURI());
                         }
                     });
    }

    public String label(final URI entity) {
        String sparql = getPrefixString().concat(
                "SELECT DISTINCT ?label WHERE { <" + entity.toString() + "> rdfs:label ?label . }");
        return query(sparql,
                     new ResultSetMapper<String>() {
                         @Override public String mapResultSet(ResultSet rs) {
                             String result = null;
                             while (rs.hasNext()) {
                                 if (result != null) {
                                     throw new SparqlQueryException(new DataIntegrityViolationException(
                                             "More than one rdfs:label for' " + entity.toString() + "'"));
                                 }
                                 QuerySolution qs = rs.next();
                                 result = qs.getLiteral("label").getLexicalForm();
                             }
                             return result;
                         }
                     });
    }

    public URI type(final URI entity) {
        String sparql =
                getPrefixString().concat("SELECT DISTINCT ?type WHERE { <" + entity.toString() + "> rdf:type ?type . " +
                                                 "FILTER ( ?type != owl:Class ) . " +
                                                 "FILTER ( ?type != owl:NamedIndividual ) }");
        return query(sparql,
                     new ResultSetMapper<URI>() {
                         @Override public URI mapResultSet(ResultSet rs) {
                             URI result = null;
                             while (rs.hasNext()) {
                                 if (result != null) {
                                     throw new SparqlQueryException(new DataIntegrityViolationException(
                                             "More than one non-owl rdf:type for' " + entity.toString() + "'"));
                                 }
                                 QuerySolution qs = rs.next();
                                 result = URI.create(qs.getResource("type").getURI());
                             }
                             return result;
                         }
                     });
    }

    public List<URI> types(final URI entity) {
        String sparql =
                getPrefixString().concat("SELECT DISTINCT ?type " +
                                                 "WHERE { " +
                                                 "<" + entity.toString() + "> rdf:type ?target . " +
                                                 "?target rdfs:subClassOf* ?type . " +
                                                 "FILTER ( !isBlank(?type) ) . " +
                                                 "FILTER ( ?type != owl:Class ) . " +
                                                 "FILTER ( ?type != owl:NamedIndividual ) . }");
        return query(sparql,
                     new QuerySolutionMapper<URI>() {
                         @Override public URI mapQuerySolution(QuerySolution qs) {
                             return URI.create(qs.getResource("type").getURI());
                         }
                     });
    }

    protected RDFNode getLiteralNode(Object o) {
        Model m = ModelFactory.createDefaultModel();
        return m.createTypedLiteral(o);
    }
}
