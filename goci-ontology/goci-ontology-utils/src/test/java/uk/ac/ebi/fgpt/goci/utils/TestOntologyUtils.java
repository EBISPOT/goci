package uk.ac.ebi.fgpt.goci.utils;

import junit.framework.TestCase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 28-08-2012
 */
public class TestOntologyUtils extends TestCase {
    public String testClassURI = "http://www.ebi.ac.uk/fgpt/ontologies/test/TEST_00001";

    public String testLabel = "test class";
    public String testShortform = "test:TEST_00001";

    private OWLOntology testOntology;
    private OWLClass testClass;

    public void setUp() {
        try {
            // load the ontology
            URL resource = getClass().getClassLoader().getResource("test.owl");
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            IRI iri = IRI.create(resource.toURI());
            testOntology = manager.loadOntologyFromOntologyDocument(iri);

            for (OWLClass cls : testOntology.getClassesInSignature()) {
                if (cls.getIRI().equals(IRI.create(testClassURI))) {
                    testClass = cls;
                }
                if (testClass != null) {
                    break;
                }
            }
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        testOntology = null;
    }

    public void testGetShortFormString() {
        String shortform = OntologyUtils.getShortForm(testClassURI, testOntology);
        assertEquals("Shortform does not match expected", testShortform, shortform);
    }

    public void testGetShortFormURI() {
        String shortform = OntologyUtils.getShortForm(URI.create(testClassURI), testOntology);
        assertEquals("Shortform does not match expected", testShortform, shortform);
    }

    public void testGetShortFormIRI() {
        String shortform = OntologyUtils.getShortForm(IRI.create(testClassURI), testOntology);
        assertEquals("Shortform does not match expected", testShortform, shortform);
    }

    public void testGetShortFormEntity() {
        String shortform = OntologyUtils.getShortForm(testClass);
        assertEquals("Shortform does not match expected", testShortform, shortform);
    }

    public void testGetClassLabel() {
        String label = OntologyUtils.getClassLabel(testOntology, testClass);
        assertEquals("Class label does not match expected", testLabel, label);
    }
}
