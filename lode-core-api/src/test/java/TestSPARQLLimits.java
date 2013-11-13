import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import junit.framework.TestCase;
import uk.ac.ebi.fgpt.lode.impl.JenaSparqlService;

/**
 * @author Simon Jupp
 * @date 23/10/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestSPARQLLimits extends TestCase {

    private String rawQuery = "SELECT * WHERE {?s ?p ?p} ";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testLimits1 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);

        service.setMaxQueryLimit(-1);
        assertEquals(service.getMaxQueryLimit().intValue(), -1);

    }

    public void testLimits2 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);
        assertEquals(service.getMaxQueryLimit().intValue(), 10);
        assertFalse(query.hasLimit());
        query.setLimit(1);
        assertTrue(query.hasLimit());
        assertEquals(query.getLimit(), 1);
    }

    public void testLimits3 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        service.setLimits(query, 1);
        assertTrue(query.getLimit() == 1);
    }

    public void testLimits4 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(2);
        service.setLimits(query, 1);
        assertTrue(query.getLimit() == 1);
    }

    public void testLimits5 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(1);
        service.setLimits(query, 2);
        assertTrue(query.getLimit() == 1);
    }

    public void testLimits6 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(2);
        service.setLimits(query, 2);
        assertTrue(query.getLimit() == 2);
    }

    public void testLimits7 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(3);
        service.setLimits(query, 2);
        assertTrue(query.getLimit() == 2);
    }

    public void testLimits8 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);
        query.setLimit(3);
        service.setLimits(query, 2);
        assertTrue(query.getLimit() == 2);
    }

    public void testLimits10 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);
        service.setLimits(query, null);
        assertTrue(query.getLimit() == 10);
    }

    public void testLimits11 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(2);
        service.setLimits(query, null);
        assertTrue(query.getLimit() == 2);
    }

    public void testLimits12 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);

        query.setLimit(10);
        service.setLimits(query, null);
        assertTrue(query.getLimit() == 10);
    }

    public void testLimits13 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(10);
        query.setLimit(11);
        service.setLimits(query, null);
        assertTrue(query.getLimit() == 10);
    }

    public void testLimits14 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(-1);
        service.setLimits(query, null);
        assertFalse(query.hasLimit());
    }

    public void testLimits15 () {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(-1);
        query.setLimit(1000000);
        service.setLimits(query, null);
        assertTrue(query.hasLimit());
        assertTrue(query.getLimit() == 1000000);
    }

    public void testLimits16() {
            JenaSparqlService service = new JenaSparqlService();
            Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
            service.setMaxQueryLimit(-1);
            query.setLimit(1000000);
            service.setLimits(query, 1000001);
            assertTrue(query.hasLimit());
            assertTrue(query.getLimit() == 1000001);
    }

    public void testLimits17() {
        JenaSparqlService service = new JenaSparqlService();
        Query query = QueryFactory.create(rawQuery, Syntax.syntaxARQ);
        service.setMaxQueryLimit(-1);
        query.setLimit(1000000);
        service.setLimits(query, 999999);
        assertTrue(query.hasLimit());
        assertTrue(query.getLimit() == 999999);
    }
}
