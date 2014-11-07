package uk.ac.ebi.fgpt.goci.service;

import junit.framework.TestCase;
import org.mockito.InOrder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.JDBCSingleNucleotidePolymorphismDAO;
import uk.ac.ebi.fgpt.goci.dao.JDBCStudyDAO;
import uk.ac.ebi.fgpt.goci.dao.JDBCTraitAssociationDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * Date 13-02-2012
 */
public class TestDefaultGWASOWLPublisher extends TestCase {
    private DefaultGWASOWLPublisher publisher;

    private URL testOntologyResource;
    private IRI testOntologyIRI;
    private OWLOntology testOntology;

    private JDBCStudyDAO studyDAO;
    private JDBCTraitAssociationDAO traitAssociationDAO;
    private JDBCSingleNucleotidePolymorphismDAO snpDAO;

    private GWASOWLConverter converter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setUp() {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            // create test ontology
            testOntologyResource = getClass().getClassLoader().getResource("test.owl");
            testOntologyIRI = IRI.create(testOntologyResource);
            testOntology = manager.loadOntology(testOntologyIRI);

            // create publisher
            publisher = new DefaultGWASOWLPublisher();

            // create mocked objects and inject dependencies
            OntologyConfiguration config = mock(OntologyConfiguration.class);
            when(config.getOWLOntologyManager()).thenReturn(manager);
            publisher.setConfiguration(config);

            converter = mock(GWASOWLConverter.class);
            when(converter.createConversionOntology()).thenReturn(testOntology);
            publisher.setConverter(converter);

            studyDAO = mock(JDBCStudyDAO.class);
            publisher.setStudyDAO(studyDAO);

            traitAssociationDAO = mock(JDBCTraitAssociationDAO.class);
            publisher.setTraitAssociationDAO(traitAssociationDAO);

            snpDAO = mock(JDBCSingleNucleotidePolymorphismDAO.class);
            publisher.setSingleNucleotidePolymorphismDAO(snpDAO);
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        publisher = null;

        testOntologyResource = null;
        testOntologyIRI = null;

        testOntology = null;

        studyDAO = null;
        traitAssociationDAO = null;
        snpDAO = null;

        converter = null;
    }

    public void testSaveEmptyReasonedOntology() {
        try {
            File f = new File(System.getProperty("java.io.tmpdir"), "test.owl");
            publisher.getManager().saveOntology(testOntology, IRI.create(f));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPublishGWASData() {
        try {
            // call publishGWASData()
            publisher.publishGWASData();
            //verify our interactions with DAOs
            verify(converter).createConversionOntology();
            verify(studyDAO).retrieveAllStudies();
            verify(traitAssociationDAO).retrieveAllTraitAssociations();
            verify(snpDAO).retrieveAllSNPs();

            // verify converter interactions (should be in order, deepest to shallowest)
            InOrder inOrder = inOrder(converter);
            inOrder.verify(converter).addSNPsToOntology(anyCollection(), eq(testOntology));
            inOrder.verify(converter).addAssociationsToOntology(anyCollection(), eq(testOntology));
            inOrder.verify(converter).addStudiesToOntology(anyCollection(), eq(testOntology));
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPublishGWASDataInferredView() {
        // call publishGWASDataInferredView() on loaded test ontology
        try {
            OWLReasoner reasoner = publisher.publishGWASDataInferredView(testOntology);
            assertNotNull(reasoner.getRootOntology());
            assertTrue(reasoner.isConsistent());
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSaveGWASData() {
        try {
            // save the file
            File f = new File(System.getProperty("java.io.tmpdir"), "ontology_output.owl");
            publisher.saveGWASData(testOntology, f);
            // verify the file exists
            assertTrue(f.exists());
            // reload the file
            getLog().debug("Loading from " + f.getAbsolutePath());
            // create a new manager to reload the same ontology into memory twice
            OWLOntology loadedOntology = OWLManager.createOWLOntologyManager().loadOntology(IRI.create(f));
            assertEquals(testOntology.getOntologyID().getOntologyIRI(),
                         loadedOntology.getOntologyID().getOntologyIRI());
            f.delete();
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSaveGWASDataInferredView() {
        // call publishGWASDataInferredView() on loaded test ontology
        try {
            OWLReasoner reasoner = publisher.publishGWASDataInferredView(testOntology);
            // save the file
            File f = new File(System.getProperty("java.io.tmpdir"), "ontology_inferred_output.owl");
            publisher.saveGWASDataInferredView(reasoner, f);
            assertTrue(f.exists());
            // reload the file
            getLog().debug("Loading from " + f.getAbsolutePath());
            // create a new manager to reload the same ontology into memory twice
            OWLOntology loadedOntology = OWLManager.createOWLOntologyManager().loadOntology(IRI.create(f));
            assertNotNull(loadedOntology);
            f.delete();
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
    }
}
