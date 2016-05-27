package uk.ac.ebi.spot.goci.tree;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;
import uk.ac.ebi.spot.goci.ontology.exception.UnexpectedOntologyStructureException;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 08/08/12
 */

@Service
@Component
public class IRITreeBuilder {
    private String owlNothingIRI;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    //    OntologyLoader ontologyLoader;
    //
    //    @Autowired
    //    public IRITreeBuilder(OntologyLoader ontologyLoader){
    //        this.ontologyLoader = ontologyLoader;
    //    }

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
                rootNode = new IRINode(cls.getIRI(), getClassLabel(efo, cls));
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
                    label = getClassLabel(efo, childClass);
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


    private String getClassLabel(OWLOntology ontology, OWLClass cls) {
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        String className = null;
        for (OWLAnnotation annotation : cls.getAnnotations(ontology, label)) {
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral val = (OWLLiteral) annotation.getValue();
                className = val.getLiteral();
            }
            if (cls.getAnnotations(ontology, label).size() != 1) {
                throw new UnexpectedOntologyStructureException("More than one label for class " + cls);
            }
        }

        if (className != null) {
            return className;
        }
        else {
            throw new UnexpectedOntologyStructureException("There is no label for class " + cls);
        }
    }
}
