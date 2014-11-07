package uk.ac.ebi.fgpt.goci.sparql.pussycat.reasoning;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import uk.ac.ebi.fgpt.goci.dao.DefaultOntologyDAO;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.reasoning.ReasonerSession;

/**
 * A reasoner session that uses loads an OWL ontology from the provided resource, and then uses the GOCI DataPublisher
 * to generate the inferred view using the reasoning mechanisms provided by that data publisher.
 * <p/>
 * The resulting reasoner is cached in-memory using ehcache to provide caching functionality.
 *
 * @author Tony Burdett
 * @date 13/04/12
 */
public class DAOBasedReasonerSession extends Initializable implements ReasonerSession {
    private final DefaultOntologyDAO ontologyDAO;

    private OWLReasoner reasoner;

    public DAOBasedReasonerSession(DefaultOntologyDAO ontologyDAO) {
        this.ontologyDAO = ontologyDAO;
    }

    public DefaultOntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    @Override
    protected void doInitialization() throws Exception {
        getLog().debug("Initializing reasoner session, this may take some time...");
        createReasoner();
    }

    @Override public boolean isReasonerInitialized() {
        return isReady();
    }

    @Override
    public synchronized OWLReasoner getReasoner() {
        if (!isReady()) {
            // lazy init
            init();
            try {
                waitUntilReady();
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Interrupted waiting for reasoner to be ready");
            }
        }
        return reasoner;
    }

    private void createReasoner() {
        OWLOntology efo = getOntologyDAO().getOntology();
        reasoner = reasonOver(efo);
    }

    private OWLReasoner reasonOver(OWLOntology ontology) {
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
                throw new RuntimeException("Once classified, unsatisfiable classes were detected");
            }
            else {
                getLog().info("Reasoning complete! ");
                return reasoner;
            }
        }
        catch (UnloadableImportException e) {
            throw new RuntimeException("Failed to load imports", e);
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
