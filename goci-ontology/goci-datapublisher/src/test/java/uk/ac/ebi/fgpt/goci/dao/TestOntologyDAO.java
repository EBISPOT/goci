package uk.ac.ebi.fgpt.goci.dao;

import junit.framework.TestCase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.core.io.ClassPathResource;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 26-01-2012
 */
public class TestOntologyDAO extends TestCase {
    private String testClassURI = "http://www.ebi.ac.uk/fgpt/ontologies/test/TEST_00001";
    private String obsoleteTestClassURI = "http://www.ebi.ac.uk/fgpt/ontologies/test/TEST_00002";

    private String testLabel = "test class";
    private String testLabel2 = "TEST CLASS";
    private String testLabel3 = "TesTcLAss";
    private String testSynonym = "test";

    private OWLClass testClass;
    private OWLClass obsoleteClass;

    private OntologyDAO dao;

    public void setUp() {
        try {
            // load the ontology
            URL resource = getClass().getClassLoader().getResource("test.owl");
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            IRI iri = IRI.create(resource.toURI());
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);

            for (OWLClass cls : ontology.getClassesInSignature()) {
                if (cls.getIRI().equals(IRI.create(testClassURI))) {
                    testClass = cls;
                }
                if (cls.getIRI().equals(IRI.create(obsoleteTestClassURI))) {
                    obsoleteClass = cls;
                }
                if (testClass != null && obsoleteClass != null) {
                    break;
                }
            }

            dao = new OntologyDAO();
            dao.setEfoResource(new ClassPathResource("test.owl"));
            dao.setEfoURI("http://www.ebi.ac.uk/fgpt/ontologies/test");
            dao.init();
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
        dao = null;
    }

    public void testGetOWLClassesByLabel() {
        Collection<OWLClass> classes = dao.getOWLClassesByLabel(testLabel);
        assertEquals("More than one class with label '" + testLabel + "'", 1, classes.size());
        assertEquals("Label search returned wrong class", testClass, classes.iterator().next());
    }

    public void testGetClassNames() {
        List<String> classNames = dao.getClassNames(testClass);
        assertEquals("Wrong number of names for class '" + testClass + "'", 2, classNames.size());
        assertTrue("Class name lookup didn't return an expected name", classNames.contains(testLabel));
        assertTrue("Class name lookup didn't return an expected name", classNames.contains(testSynonym));
    }

    public void testGetClassSynonyms() {
        Set<String> classSynonyms = dao.getClassSynonyms(testClass);
        assertEquals("Wrong number of synonyms for class '" + testClass + "'", 1, classSynonyms.size());
        assertTrue("Class synonym lookup didn't return an expected synonym", classSynonyms.contains(testSynonym));
    }

    public void testGetClassRDFSLabels() {
        Set<String> classRDFSLabels = dao.getClassRDFSLabels(testClass);
        assertEquals("Wrong number of lables for class '" + testClass + "'", 1, classRDFSLabels.size());
        assertTrue("Class synonym lookup didn't return an expected synonym", classRDFSLabels.contains(testLabel));
    }

    public void testGetOWLClassByURI() {
        OWLClass owlClass = dao.getOWLClassByURI(testClassURI);
        assertNotNull("OWL class should not be null", owlClass);
        assertEquals("Query should return test class '" + testClass + "'", testClass, owlClass);
    }
}
