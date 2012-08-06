package uk.ac.ebi.fgpt.goci.pussycat.session;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyDocumentAlreadyExistsException;
import org.semanticweb.owlapi.reasoner.*;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.utils.OntologyUtils;

/**
 * A simple reasoner session that returns a reasoner over EFO.
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
public class OntologyReasonerSession extends Initializable implements ReasonerSession {
    private OntologyConfiguration ontologyConfiguration;

    private OWLReasoner reasoner;

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public void setOntologyConfiguration(OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    @Override protected void doInitialization() throws Exception {
        IRI efoLocationIRI = IRI.create(getOntologyConfiguration().getEfoResource().getURI());
        IRI efoLogicalIRI = IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI + "/");

        OWLOntology ontology;
        getLog().debug("Trying to create a reasoner over ontology from '" + efoLocationIRI + "'");
        try {
            ontology = getOntologyConfiguration().getOWLOntologyManager().loadOntology(efoLocationIRI);
            OntologyUtils.loadImports(getOntologyConfiguration().getOWLOntologyManager(), ontology);

            StringBuilder loadedOntologies = new StringBuilder();
            int n = 1;
            for (OWLOntology o : ontology.getOWLOntologyManager().getOntologies()) {
                loadedOntologies.append("\t")
                        .append(n++)
                        .append(") ")
                        .append(o.getOntologyID().getOntologyIRI())
                        .append("\n");
            }
            getLog().debug("Imports collected: the following ontologies have been loaded in this session:\n" +
                                   loadedOntologies.toString());
        }
        catch (OWLOntologyDocumentAlreadyExistsException e) {
            getLog().debug("Ontology already exists: attempting to retrieve '" + efoLogicalIRI + "'");
            if (getOntologyConfiguration().getOWLOntologyManager().contains(efoLogicalIRI)) {
                ontology = getOntologyConfiguration().getOWLOntologyManager().getOntology(efoLogicalIRI);
            }
            else {
                String msg = "Ontology from '" + efoLocationIRI + "' has already been loaded, " +
                        "but EFO ('" + efoLogicalIRI + "') was not found";
                getLog().error(msg);
                throw new NullPointerException(msg);
            }
        }
        getLog().info("Classifying ontology from " + efoLogicalIRI);

        getLog().debug("Creating reasoner... ");
        OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        reasoner = factory.createReasoner(ontology, config);

        getLog().debug("Precomputing inferences...");
        reasoner.precomputeInferences();

        getLog().debug("Checking ontology consistency...");
        reasoner.isConsistent();

        getLog().debug("Checking for unsatisfiable classes...");
        if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().size() > 0) {
            throw new OWLConversionException("Once classified, unsatisfiable classes were detected");
        }
        else {
            getLog().info("Reasoning complete! ");
        }
    }

    @Override public boolean isReasonerInitialized() {
        return isReady();
    }

    @Override public OWLReasoner getReasoner() throws OWLConversionException {
        try {
            waitUntilReady();
            return reasoner;
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Reasoning was interrupted", e);
        }
    }
}
