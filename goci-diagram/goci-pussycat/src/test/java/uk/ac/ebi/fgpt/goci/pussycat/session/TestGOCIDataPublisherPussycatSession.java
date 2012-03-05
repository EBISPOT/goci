package uk.ac.ebi.fgpt.goci.pussycat.session;

import net.sf.ehcache.CacheManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 02/03/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:goci-pussycat.xml", "classpath:goci-datapublisher.xml"})
public class TestGOCIDataPublisherPussycatSession {
    @Autowired
    private OntologyConfiguration ontologyConfiguration;
    @Autowired
    private PussycatSession pussycatSession;
    @Autowired
    private CacheManager cacheManager;

    @Before
    public void before() {
        cacheManager.clearAll();
    }

    @After
    public void after() {
        pussycatSession.clearRendering();
    }

    @Test
    public void testSessionID() {
        assertNotNull("Session ID should not be null", pussycatSession.getSessionID());
        System.out.println("Session ID: " + pussycatSession.getSessionID());
    }

    @Test
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

    @Test
    public void testRepeatReasonerAcquisition() {
        try {
            long start, end;
            double time;

            OWLReasoner reasoner;
            start = System.currentTimeMillis();
            System.out.println("Trying to get a reasoner...");
            reasoner = pussycatSession.getReasoner();
            end = System.currentTimeMillis();

            time = ((double) (end - start)) / 1000;
            System.out.println("Obtained first reasoner in " + time + "s.");

            System.out.print("Waiting a few moments");
            synchronized (this) {
                for (int i = 0; i < 5; i++) {
                    try {
                        wait(1000);
                        System.out.print(".");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("ok!");

            start = System.currentTimeMillis();
            System.out.println("Trying to get another reasoner...");
            reasoner = pussycatSession.getReasoner();
            end = System.currentTimeMillis();

            time = ((double) (end - start)) / 1000;
            System.out.println("Obtained second reasoner in " + time + "s.");
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRepeatReasonerQuerying() {
        // get owl:thing class and use it to query reasoner
        OWLClass thingCls = ontologyConfiguration.getOWLDataFactory().getOWLThing();
        try {
            long start, end;
            double time;

            start = System.currentTimeMillis();
            System.out.println("Querying pussycat session...");
            Collection<OWLNamedIndividual> individuals = pussycatSession.query(thingCls);
            assertNotSame("There should not be zero individuals that are members of OWL:Thing", 0, individuals.size());
            System.out.println("Found " + individuals.size() + " instances of OWL:Thing");
            end = System.currentTimeMillis();

            time = ((double) (end - start)) / 1000;
            System.out.println("Performed first reasoner query in " + time + "s.");

            System.out.print("Waiting a few moments");
            synchronized (this) {
                for (int i = 0; i < 5; i++) {
                    try {
                        wait(1000);
                        System.out.print(".");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("ok!");

            start = System.currentTimeMillis();
            System.out.println("Querying pussycat session again...");
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
