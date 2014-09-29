import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * @author Simon Jupp
 * @date 11/09/2013
 * Functional Genomics Group EMBL-EBI
 *
 * Example of querying the Gene Expression Atlas SPARQL endpoint from Java
 * using the Jena API (http://jena.apache.org)
 *
 */
public class JenaSparqlExample {

    String sparqlEndpoint = "http://wwwdev.ebi.ac.uk/rdf/services/atlas/sparql";

    // get expression values for uniprot acc Q16850
    String sparqlQuery =  "" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX atlasterms: <http://rdf.ebi.ac.uk/terms/atlas/>" +
            "SELECT distinct ?expressionValue ?pvalue \n" +
            "WHERE { \n" +
            "?value rdfs:label ?expressionValue . \n" +
            "?value atlasterms:pValue ?pvalue .  \n" +
            "?value atlasterms:isMeasurementOf ?probe . \n" +
            "?probe atlasterms:dbXref ?uniprotAccession .\n" +
            "} \n" +
            "ORDER BY ASC(?pvalue)";


    public JenaSparqlExample() {

        // create the Jena query using the ARQ syntax (has additional support for SPARQL federated queries)
        Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ) ;

        // we want to bind the ?uniprotAccession variable in the query
        // to the URI for Q16850 which is http://purl.uniprot.org/uniprot/Q16850
        QuerySolutionMap querySolutionMap = new QuerySolutionMap();
        querySolutionMap.add("uniprotAccession", new ResourceImpl("http://purl.uniprot.org/uniprot/Q16850"));
        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(query.toString(), querySolutionMap);

        QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,parameterizedSparqlString.asQuery());

        // execute a Select query
        ResultSet results = httpQuery.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            // get the value of the variables in the select clause
            String expressionValue = solution.get("expressionValue").asLiteral().getLexicalForm();
            String pValue = solution.get("pvalue").asLiteral().getLexicalForm();
            // print the output to stdout
            System.out.println(expressionValue + "\t" + pValue);
        }

    }

    public static void main(String[] args) {
        new JenaSparqlExample();
    }

}
