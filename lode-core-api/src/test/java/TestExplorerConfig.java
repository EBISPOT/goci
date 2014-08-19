import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.lode.impl.DefaultExplorerViewConfigImpl;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.LabeledResource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Simon Jupp
 * @date 02/05/2013
 * Functional Genomics Group EMBL-EBI
 */
@Ignore
public class TestExplorerConfig extends TestCase {

    ExplorerViewConfiguration viewConfiguration;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:test-explorer-config.xml");

        viewConfiguration = (ExplorerViewConfiguration) ctx.getBean("testExplorerConfig");

    }

    public void testGetObjectMaxSample() {
        assertEquals(50, viewConfiguration.getObjectMaxSample());
    }

    public void testGetTopRelationships () {
        assertEquals(2, viewConfiguration.getTopRelationships().size());
        boolean hasLabel = false;
        for (URI res : viewConfiguration.getTopRelationships()) {
            URI label = URI.create("http://xmlns.com/foaf/0.1/page");
            if (res.equals(label)) {
                hasLabel = true;
            }
        }
        assertTrue(hasLabel);
    }

    public void testGetIgnoreRelationships () {
        assertEquals(4, viewConfiguration.getIgnoreRelationships().size());
        for (URI res : viewConfiguration.getIgnoreRelationships()) {
            if (URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type").equals(res)) {
                assertEquals(URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), res);
            }
        }
    }

    public void testGetIgnoreTypes () {
        assertEquals(4, viewConfiguration.getIgnoreTypes().size());

        boolean f = false;
        for (URI res : viewConfiguration.getIgnoreTypes()) {

            if (res.equals(URI.create("http://www.w3.org/2002/07/owl#Thing"))) {
                f = true;
            }
        }
        assertTrue(f);
    }

    public void testIgnoreBlankNode() {
        assertTrue(viewConfiguration.ignoreBlankNodes());

    }



}
