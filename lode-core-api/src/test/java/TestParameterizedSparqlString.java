import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import junit.framework.TestCase;
import uk.ac.ebi.fgpt.lode.utils.ParameterizedSparqlString;

/**
 * @author Simon Jupp
 * @date 25/05/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestParameterizedSparqlString extends TestCase {


    public void testParameterizedSparqlString () {

        Query query = QueryFactory.create("SELECT * WHERE {?s ?p ?o}");

        QuerySolutionMap initialBinding = new QuerySolutionMap();
        initialBinding.add("s", new ResourceImpl("http://www.example.org"));

        ParameterizedSparqlString sparql = new ParameterizedSparqlString(query.toString(), initialBinding);
        System.out.println(sparql);
        assertTrue(sparql.toString().contains("http://www.example.org"));


    }

}
