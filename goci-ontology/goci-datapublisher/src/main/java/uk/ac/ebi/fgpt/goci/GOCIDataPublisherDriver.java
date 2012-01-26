package uk.ac.ebi.fgpt.goci;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.service.GWASOWLPublisher;

import java.io.File;

/**
 * A driver class, containing a {@link #main(String[])} method, that can be used to run the datapublisher to convert the
 * GWAS catalog data into OWL.
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class GOCIDataPublisherDriver {
    private GWASOWLPublisher publisher;

    public GOCIDataPublisherDriver() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-datapublisher-dao.xml");
        publisher = ctx.getBean("publisher", GWASOWLPublisher.class);
    }

    public OWLOntology publish() throws OWLConversionException {
        return publisher.publishGWASData();
    }

    public void writeOWLFile(OWLOntology ontology, File outputFile) throws OWLOntologyStorageException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
        if (format.isPrefixOWLOntologyFormat()) {
            owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
        }
        manager.saveOntology(ontology, owlxmlFormat, IRI.create(outputFile.toURI()));
    }

    public static void main(String[] args) {
        GOCIDataPublisherDriver driver = new GOCIDataPublisherDriver();
        try {
            OWLOntology ontology = driver.publish();
            driver.writeOWLFile(ontology, new File("gwas-catalog.owl"));
        }
        catch (OWLOntologyStorageException e) {
            System.err.println("Failed to save ontology: " + e.getMessage());
            System.exit(1);
        }
        catch (OWLConversionException e) {
            System.err.println("Failed to convert data to OWL: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void parseArguments(String[] args) {
        // do stuff to parse args
    }
}
