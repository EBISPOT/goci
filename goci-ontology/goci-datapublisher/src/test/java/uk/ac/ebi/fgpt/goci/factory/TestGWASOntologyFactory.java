package uk.ac.ebi.fgpt.goci.factory;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 26-01-2012
 */
public class TestGWASOntologyFactory extends TestCase {
    private GWASOntologyFactory factory;
    private OWLOntology ontology;

    public void setUp() {
        try {
            ontology = OWLManager.createOWLOntologyManager().createOntology();
            factory = Mockito.mock(GWASOntologyFactory.class);
            Mockito.when(factory.loadOntology()).thenReturn(ontology);
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        ontology = null;
        factory = null;
    }

    public void testLoadOntology() {
        OWLOntology loadedOntology = factory.loadOntology();
        Mockito.verify(factory).loadOntology();
        assertEquals("Loaded ontology didn't match expected", loadedOntology, ontology);
    }
}
