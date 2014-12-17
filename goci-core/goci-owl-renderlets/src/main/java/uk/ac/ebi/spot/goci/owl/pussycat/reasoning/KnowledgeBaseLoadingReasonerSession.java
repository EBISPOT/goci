package uk.ac.ebi.spot.goci.owl.pussycat.reasoning;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.springframework.core.io.Resource;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;
import uk.ac.ebi.spot.goci.lang.Initializable;
import uk.ac.ebi.spot.goci.lang.OntologyConfiguration;
import uk.ac.ebi.spot.goci.reasoning.ReasonerSession;

import java.io.IOException;

/**
 * A reasoner session that uses loads an OWL ontology from the provided resource, and then uses the GOCI DataPublisher
 * to generate the inferred view using the reasoning mechanisms provided by that data publisher.
 * <p/>
 * The resulting reasoner is cached in-memory using ehcache to provide caching functionality.
 *
 * @author Tony Burdett
 * @date 13/04/12
 */
public class KnowledgeBaseLoadingReasonerSession extends Initializable implements ReasonerSession {
    private OntologyConfiguration configuration;

    private OWLReasoner reasoner;

    @Override
    protected void doInitialization() throws Exception {
        getLog().debug("Initializing reasoner session, this may take some time...");
        createReasoner();
    }

    public OntologyConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OntologyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override public boolean isReasonerInitialized() {
        return isReady();
    }

    @Override
    public OWLReasoner getReasoner()  {
        if (isReady()) {
            return reasoner;
        }
        else {
            throw new RuntimeException("Reasoner has not yet been initialized: check reasoner session is ready " +
                                               "with 'isReasonerInitialized()' before requesting the reasoner");
        }
    }

    private void createReasoner() throws OWLConversionException {
        try {
            Resource gwasDataResource = getConfiguration().getGwasDiagramDataResource();
            getLog().info("Loading GWAS data from " + gwasDataResource.toString());
            OWLOntology gwasData;
            try {
                gwasData = getConfiguration().getOWLOntologyManager().loadOntologyFromOntologyDocument(
                        gwasDataResource.getInputStream());
            }
            catch (OWLOntologyAlreadyExistsException e) {
                getLog().info("GWAS data was already loaded (" + e.getOntologyID().getOntologyIRI() + ")");
                gwasData = getConfiguration().getOWLOntology(e.getOntologyID().getOntologyIRI());
            }
            getLog().debug("Publishing GWAS data (inferred view)");
            reasoner = reasonOver(gwasData);

        }
        catch (IOException e) {
            getLog().error("Failed to load ontology resource", e);
            throw new OWLConversionException("Failed to load ontology resource", e);
        }
        catch (OWLOntologyCreationException e) {
            getLog().error("Failed to load ontology resource", e);
            throw new OWLConversionException("Failed to load ontology resource", e);
        }
    }

    private OWLReasoner reasonOver(OWLOntology ontology) throws OWLConversionException {
        try {
            getLog().debug("Loading any missing imports...");
//            OntologyUtils.loadImports(ontology.getOWLOntologyManager(), ontology);
            OWLOntologyLoaderConfiguration loaderConfiguration = new OWLOntologyLoaderConfiguration();
            for (OWLImportsDeclaration declaration : ontology.getImportsDeclarations()) {
                ontology.getOWLOntologyManager().makeLoadImportRequest(declaration, loaderConfiguration);
            }

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
            getLog().info("Classifying ontology from " + ontology.getOntologyID().getOntologyIRI());

            getLog().debug("Creating reasoner... ");
            OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
            ReasonerProgressMonitor progressMonitor = new LoggingProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
            OWLReasoner reasoner = factory.createReasoner(ontology, config);

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
                return reasoner;
            }
        }
        catch (UnloadableImportException e) {
            throw new OWLConversionException("Failed to load imports", e);
        }
    }

    private class LoggingProgressMonitor implements ReasonerProgressMonitor {
        private String taskName;
        private int lastPercent;

        @Override public void reasonerTaskStarted(String taskName) {
            this.taskName = taskName;
            getLog().debug(taskName);
        }

        @Override public void reasonerTaskStopped() {
            getLog().debug(taskName + "complete!");
            this.taskName = null;
            this.lastPercent = 0;
        }

        @Override public void reasonerTaskProgressChanged(int value, int max) {
            if (taskName != null) {
                int percent = value * 100 / max;
                if (percent != lastPercent) {
                    if (percent % 10 == 0) {
                        getLog().trace("" + percent + "% done...");
                    }
                    lastPercent = percent;
                }
            }
        }

        @Override public void reasonerTaskBusy() {

        }
    }
}
