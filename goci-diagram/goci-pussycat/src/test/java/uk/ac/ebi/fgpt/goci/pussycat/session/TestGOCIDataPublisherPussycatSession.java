package uk.ac.ebi.fgpt.goci.pussycat.session;

import junit.framework.TestCase;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 02/03/12
 */
public class TestGOCIDataPublisherPussycatSession extends TestCase {
    private OntologyConfiguration ontologyConfiguration;
    private PussycatSession pussycatSession;

    @Override protected void setUp() throws Exception {
        super.setUp();
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-pussycat.xml", "goci-datapublisher.xml");
        ontologyConfiguration = ctx.getBean("config", OntologyConfiguration.class);
//        PussycatSessionManager pussycatSessionManager =
//                ctx.getBean("pussycatSessionManager", PussycatSessionManager.class);
//        pussycatSession = (GOCIDataPublisherPussycatSession) pussycatSessionManager.createPussycatSession();
        pussycatSession = ctx.getBean("pussycatSession", PussycatSession.class);
    }

    @Override protected void tearDown() throws Exception {
        super.tearDown();
        pussycatSession = null;
    }

    public void testSessionID() {
        assertNotNull("Session ID should not be null", pussycatSession.getSessionID());
        System.out.println("Session ID: " + pussycatSession.getSessionID());
    }

    public void testQueryReasoner() {
        // get owl:thing class and use it to query reasoner
        OWLClass thingCls = ontologyConfiguration.getOWLDataFactory().getOWLThing();
        try {
            Collection<OWLNamedIndividual> individuals = pussycatSession.query(thingCls);
            assertNotSame("There should not be zero individuals that are members of OWL:Thing", 0, individuals.size());
            System.out.println("Found " + individuals.size() + " instances of OWL:Thing");
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRepeatReasonerQuerying() {
        // get owl:thing class and use it to query reasoner
        OWLClass thingCls = ontologyConfiguration.getOWLDataFactory().getOWLThing();
        try {
            long start, end;
            double time;

            start = System.currentTimeMillis();
            Collection<OWLNamedIndividual> individuals = pussycatSession.query(thingCls);
            assertNotSame("There should not be zero individuals that are members of OWL:Thing", 0, individuals.size());
            System.out.println("Found " + individuals.size() + " instances of OWL:Thing");
            end = System.currentTimeMillis();

            time = ((double) (end - start)) / 1000;
            System.out.println("Performed first reasoner query in " + time + "s.");

            synchronized (this) {
                try {
                    wait(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            start = System.currentTimeMillis();
            Collection<OWLNamedIndividual> individuals2 = pussycatSession.query(thingCls);
            assertNotSame("There should not be zero individuals that are members of OWL:Thing", 0, individuals2.size());
            System.out.println("Found " + individuals2.size() + " instances of OWL:Thing");
            end = System.currentTimeMillis();

            time = ((double) (end - start)) / 1000;
            System.out.println("Performed second reasoner query in " + time + "s.");
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }

    }
}
