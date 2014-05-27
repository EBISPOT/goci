package uk.ac.ebi.fgpt.goci.tree;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.UnexpectedOntologyStructureException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.utils.OntologyUtils;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 08/08/12
 */
public class IRITreeBuilder {
    private String owlNothingIRI;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public IRITree buildIRITree(URL efoLocation) throws URISyntaxException, OWLOntologyCreationException {
        // load efo
        getLog().info("Loading efo...");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology efo = manager.loadOntology(IRI.create(efoLocation));

        owlNothingIRI = manager.getOWLDataFactory().getOWLNothing().getIRI().toString();

        // create a reasoner over efo
        getLog().info("Reasoning over efo...");
        OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        OWLReasoner reasoner = factory.createReasoner(efo, config);

        getLog().info("Precomputing inferences...");
        reasoner.precomputeInferences();

        getLog().info("Checking ontology consistency...");
        reasoner.isConsistent();

        // get 'top' class
        OWLClass topClass = reasoner.getTopClassNode().getRepresentativeElement();
        getLog().info("Reasoner 'top class' element is " + topClass.getIRI());

        IRITree tree = new IRITree();

        // do one level deep manually - should only be experimental factor
        IRINode rootNode = null;
        OWLClass efClass = null;
        NodeSet<OWLClass> subclasses = reasoner.getSubClasses(topClass, true);
        for (Node<OWLClass> node : subclasses) {
            OWLClass cls = node.getRepresentativeElement();
            getLog().debug("Next child of " + topClass + " is " + cls);

            if (cls.getIRI().toString().equals(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI)) {
                efClass = cls;
                rootNode = new IRINode(cls.getIRI(), OntologyUtils.getClassLabel(efo, cls));
            }
        }

        if (rootNode != null) {
            getLog().info("Building tree... walking ontology from " + rootNode.getLabel() + " down...");
            tree.setRootNode(rootNode);
            recurse(reasoner, efo, efClass, rootNode);
            getLog().info("...Tree build complete!");
        }
        else {
            throw new RuntimeException("Could not find Experimental Factor as a child of OWL:Thing");
        }

        return tree;
    }

    private void recurse(OWLReasoner reasoner, OWLOntology efo, OWLClassExpression parentClass, IRINode parentNode) {
        NodeSet<OWLClass> subclasses = reasoner.getSubClasses(parentClass, true);
        // get subclasses
        for (Node<OWLClass> node : subclasses) {
            // get next child class
            OWLClass childClass = node.getRepresentativeElement();
            if (!childClass.getIRI().toString().equals(owlNothingIRI)) {
                getLog().debug("Next child of " + parentClass + " is " + childClass);

                String label;
                try {
                    label = OntologyUtils.getClassLabel(efo, childClass);
                }
                catch (UnexpectedOntologyStructureException e) {
                    label = "<not exactly 1 label>";
                }

                IRINode childNode = new IRINode(childClass.getIRI(), label);
                if (!parentNode.getChildNodes().contains(childNode)) {
                    // only need to recurse deeper if this child was not already added
                    parentNode.addChildNode(childNode);
                    recurse(reasoner, efo, childClass, childNode);
                }
            }
        }
    }
}
