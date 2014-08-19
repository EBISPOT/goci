import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import junit.framework.TestCase;
import org.junit.Ignore;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.impl.JenaHttpExecutorService;
import uk.ac.ebi.fgpt.lode.impl.JenaSparqlService;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestJeneRemoteRespoitoryQueries extends TestCase {


    QueryEngineHTTP endpoint;

    String endpointURL = "http://www.ebi.ac.uk/fgpt/zooma/sparql";
    String selectQuery1 = "SELECT * WHERE {?s ?o ?p} LIMIT 10";

    String describe1 = "describe <http://www.w3.org/2000/01/rdf-schema#label>";



    @Override
    protected void setUp() throws Exception {
        super.setUp();


    }


    public void testQueryType () {

        Query selectQuery = QueryFactory.create(selectQuery1) ;
        assertTrue(selectQuery.isSelectType());
        assertFalse(selectQuery.isDescribeType());
        assertEquals(selectQuery.getLimit(), 10);

        Query describeQuery = QueryFactory.create(describe1) ;
        assertFalse(describeQuery.isSelectType());
        assertTrue(describeQuery.isDescribeType());

        // todo test ask query

        // todo test construct query
    }

    @Ignore
    public void testSelectExecuteQuery() {


        JenaSparqlService service = new JenaSparqlService();
        service.setQueryExecutionService(new JenaHttpExecutorService(endpointURL));
        try {
            service.query(describe1, "", 0, -1, false, System.out);
        } catch (LodeException e) {
            assertEquals(e.getMessage(), "You must specify a SPARQL endpoint URL");
        }

        try {
            service.query(selectQuery1, "XML", false, System.out);
            service.query(selectQuery1, "JSON", false, System.out);

            service.query(selectQuery1, "XML", 0, 10, false, System.out);
            service.query(selectQuery1, "XML", 10, 10, false, System.out);


            service.query(describe1, "RDF/XML", false, System.out );
            service.query(describe1, "TURTLE",false,  System.out );
            service.query(describe1, "N-TRIPLES", false, System.out );


        } catch (LodeException e) {
            e.printStackTrace();
        }




//        this.endpoint = new QueryEngineHTTP(endpointURL, selectQuery1);
//
//        com.hp.hpl.jena.query.ResultSet results = endpoint.execSelect();
//
//        assertFalse(results.isOrdered());
//
//        System.out.println("XML");
//        ResultSetFormatter.outputAsXML(System.out, results);
//
//        results = endpoint.execSelect();
//        System.out.println("JSON");
//        ResultSetFormatter.outputAsJSON(System.out, results);
//
//        this.endpoint = new QueryEngineHTTP(endpointURL, describe1);
//        Model results2 = endpoint.execDescribe();
//
//        System.out.println("RDF/XML");
//        results2.write(System.out, "RDF/XML");
//
//        results2 = endpoint.execDescribe();
//        System.out.println("N-TRIPLES");
//        results2.write(System.out, "N-TRIPLES");
//
//        results2 = endpoint.execDescribe();
//        System.out.println("TURTLE");
//        results2.write(System.out, "TURTLE");

    }
}
