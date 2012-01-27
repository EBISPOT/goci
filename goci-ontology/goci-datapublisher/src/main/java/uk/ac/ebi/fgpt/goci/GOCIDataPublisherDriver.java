package uk.ac.ebi.fgpt.goci;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
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
    private static File assertedOntologyFile;
    private static File inferredOntologyFile;

    public static void main(String[] args) {
        // parse arguments
        parseArguments(args);

        // execute publisher
        GOCIDataPublisherDriver driver = new GOCIDataPublisherDriver();
        try {
            driver.publishAndSave(assertedOntologyFile, inferredOntologyFile);
        }
        catch (OWLConversionException e) {
            System.err.println("Failed to publish data to OWL: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void parseArguments(String[] args) {
        // do stuff to parse args
        assertedOntologyFile = new File(args[0]);
        inferredOntologyFile = null;
        if (args.length > 1) {
            inferredOntologyFile = new File(args[1]);
        }
    }

    private GWASOWLPublisher publisher;

    public GOCIDataPublisherDriver() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("goci-datapublisher-dao.xml");
        publisher = ctx.getBean("publisher", GWASOWLPublisher.class);
    }

    public void publishAndSave(File assertedOntologyFile, File inferredOntologyFile)
            throws OWLConversionException {
        // publishAndSave the data
        System.out.println("Attempting to convert and publish GWAS data as OWL...");
        OWLOntology ontology = publisher.publishGWASData();

        // and save the result
        System.out.print("Ontology converted, saving asserted results...");
        publisher.saveGWASData(ontology, assertedOntologyFile);
        System.out.println("..done!");

        if (inferredOntologyFile != null) {
            // now get the inferred view
            System.out.println("Evaluating inferred view...");
            OWLReasoner reasoner = publisher.publishGWASDataInferredView(IRI.create(assertedOntologyFile));
//            OWLReasoner reasoner = publisher.publishGWASDataInferredView(ontology);

            // now save inferred view
            System.out.print("Ontology fully classified, saving inferred results...");
            publisher.saveGWASDataInferredView(reasoner, inferredOntologyFile);
            System.out.println("..done!");
        }
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
}
