import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.Syntax;
import junit.framework.TestCase;
import uk.ac.ebi.fgpt.lode.utils.GraphQueryFormats;
import uk.ac.ebi.fgpt.lode.utils.QueryType;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestJenaQueryType extends TestCase {

    String select1 = "SELECT * WHERE {?s ?p ?p}";
    String describe = "describe <http://wwww.example.com>";
    String ask = "ask {?s ?p ?o}";
    String construct = "construct {?s ?p ?o} where {?s1 ?p2 ?o3}";
    String insert = "insert {?s ?p ?o} where {?s1 ?p2 ?o3}";
    String update = "update {?s ?p ?o} where {?s1 ?p2 ?o3}";
    String delete = "delete where {?s1 ?p2 ?o3}";
    String service = "PREFIX : <http://example/>\n" +
            "PREFIX  dc:     <http://purl.org/dc/elements/1.1/>\n" +
            "\n" +
            "SELECT ?a\n" +
            "FROM <mybooks.rdf>\n" +
            "{\n" +
            "  ?b dc:title ?title .\n" +
            "  SERVICE <http://sparql.org/books>\n" +
            "     { ?s dc:title ?title . ?s dc:creator ?a }\n" +
            "}";


    public void testSelectQueryType ()  {

        Query s1 = QueryFactory.create(select1);
        assertEquals(QueryType.getQueryType(s1), QueryType.TUPLEQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.DESCRIBEQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.BOOLEANQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.UNKOWN);

        // check limit
        assertFalse(s1.hasLimit());
        Query s2 = QueryFactory.create(select1 + " LIMIT 1");
        assertTrue(s2.hasLimit());
        assertEquals(s2.getLimit(), 1);

        s1.setLimit(10);
        assertTrue(s1.hasLimit());
        assertEquals(s1.getLimit(), 10);

        try {
            Query s3 = QueryFactory.create(select1 + " guiquwhdiuqwhd ");
        } catch (Exception e) {
            assertEquals(e.getClass(), QueryParseException.class);

        }

    }

    public void testDescribeQuery () {
        Query d1 = QueryFactory.create(describe);
        assertEquals(QueryType.getQueryType(d1), QueryType.DESCRIBEQUERY);
        assertNotSame(QueryType.getQueryType(d1), QueryType.TUPLEQUERY);
        assertNotSame(QueryType.getQueryType(d1), QueryType.BOOLEANQUERY);
        assertNotSame(QueryType.getQueryType(d1), QueryType.UNKOWN);
    }

    public void testAskQuery () {
        Query a1 = QueryFactory.create(ask);
        assertEquals(QueryType.getQueryType(a1), QueryType.BOOLEANQUERY);
        assertNotSame(QueryType.getQueryType(a1), QueryType.DESCRIBEQUERY);
        assertNotSame(QueryType.getQueryType(a1), QueryType.TUPLEQUERY);
        assertNotSame(QueryType.getQueryType(a1), QueryType.UNKOWN);
    }

    public void testConstructQuery() {
        Query c1 = QueryFactory.create(construct);
        assertEquals(QueryType.getQueryType(c1), QueryType.CONSTRUCTQUERY);
        assertNotSame(QueryType.getQueryType(c1), QueryType.DESCRIBEQUERY);
        assertNotSame(QueryType.getQueryType(c1), QueryType.TUPLEQUERY);
        assertNotSame(QueryType.getQueryType(c1), QueryType.BOOLEANQUERY);
        assertNotSame(QueryType.getQueryType(c1), QueryType.UNKOWN);
    }

    public void testServiceQuery () {
        Query s1 = QueryFactory.create(service, Syntax.syntaxARQ);
        assertEquals(QueryType.getQueryType(s1), QueryType.TUPLEQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.DESCRIBEQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.BOOLEANQUERY);
        assertNotSame(QueryType.getQueryType(s1), QueryType.UNKOWN);

        // check limit
        assertFalse(s1.hasLimit());
        Query s2 = QueryFactory.create(service + " LIMIT 1", Syntax.syntaxARQ);
        assertTrue(s2.hasLimit());
        assertEquals(s2.getLimit(), 1);

        s1.setLimit(10);
        assertTrue(s1.hasLimit());
        assertEquals(s1.getLimit(), 10);

        try {
            Query s3 = QueryFactory.create(service + " guiquwhdiuqwhd ");
        } catch (Exception e) {
            assertEquals(e.getClass(), QueryParseException.class);
        }
    }

    public void testRDFFormats () {
        assertEquals("RDF/XML", GraphQueryFormats.RDFXML.toString());
//        assertTrue(GraphQueryFormats.isValid("RDF/XML-ABBREV"));
//        assertFalse(GraphQueryFormats.isValid("XML"));
//        assertFalse(GraphQueryFormats.isValid("JSON"));

    }

}
