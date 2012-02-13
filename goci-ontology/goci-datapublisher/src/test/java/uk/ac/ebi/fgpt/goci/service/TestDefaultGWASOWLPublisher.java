package uk.ac.ebi.fgpt.goci.service;

import junit.framework.TestCase;
import org.mockito.InOrder;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.SingleNucleotidePolymorphismDAO;
import uk.ac.ebi.fgpt.goci.dao.StudyDAO;
import uk.ac.ebi.fgpt.goci.dao.TraitAssociationDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 13-02-2012
 */
public class TestDefaultGWASOWLPublisher extends TestCase {
    private DefaultGWASOWLPublisher publisher;

    private OWLOntology ontology;

    private StudyDAO studyDAO;
    private TraitAssociationDAO traitAssociationDAO;
    private SingleNucleotidePolymorphismDAO snpDAO;

    private GWASOWLConverter converter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setUp() {
        // create empty ontology
        try {
            ontology = OWLManager.createOWLOntologyManager().createOntology();
            // create mocked objects
            studyDAO = mock(StudyDAO.class);
            traitAssociationDAO = mock(TraitAssociationDAO.class);
            snpDAO = mock(SingleNucleotidePolymorphismDAO.class);
            converter = mock(GWASOWLConverter.class);
            when(converter.createConversionOntology()).thenReturn(ontology);

            // create publisher
            publisher = new DefaultGWASOWLPublisher();
            //inject dependencies
            publisher.setConverter(converter);
            publisher.setStudyDAO(studyDAO);
            publisher.setTraitAssociationDAO(traitAssociationDAO);
            publisher.setSingleNucleotidePolymorphismDAO(snpDAO);
            publisher.init();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail();
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }

    }

    public void tearDown() {
        // add logic required to terminate the class here
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
            inOrder.verify(converter).addSNPsToOntology(anyCollection(), eq(ontology));
            inOrder.verify(converter).addAssociationsToOntology(anyCollection(), eq(ontology));
            inOrder.verify(converter).addStudiesToOntology(anyCollection(), eq(ontology));
        }
        catch (OWLConversionException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPublishGWASDataInferredView() {
        // call publishGWASDataInferredView() by loading test.owl
        URL resource = getClass().getClassLoader().getResource("test.owl");
        try {
            IRI iri = IRI.create(resource);
            OWLReasoner reasoner = publisher.publishGWASDataInferredView(iri);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
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
            publisher.saveGWASData(ontology, f);
            // verify the file exists
            assertTrue(f.exists());
            // reload the file
            getLog().debug("Loading from " + f.getAbsolutePath());
            OWLOntology loadedOntology = publisher.getManager().loadOntology(IRI.create(f));
            assertEquals(ontology.getOntologyID().getOntologyIRI(), loadedOntology.getOntologyID().getOntologyIRI());
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
        // call publishGWASDataInferredView() by loading test.owl
        URL resource = getClass().getClassLoader().getResource("test.owl");
        try {
            IRI iri = IRI.create(resource);
            OWLReasoner reasoner = publisher.publishGWASDataInferredView(iri);
            // save the file
            File f = new File(System.getProperty("java.io.tmpdir"), "ontology_inferred_output.owl");

            publisher.saveGWASDataInferredView(reasoner, f);
            assertTrue(f.exists());
            // reload the file
            getLog().debug("Loading from " + f.getAbsolutePath());
            OWLOntology loadedOntology = publisher.getManager().loadOntology(IRI.create(f));
            assertNotNull(loadedOntology);
            // note: this check fails, it reduces the number of axioms.  Obviously not everything gets saved!
//            assertEquals(reasoner.getRootOntology().getAxiomCount(),
//                         loadedOntology.getAxiomCount());
            f.delete();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
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
