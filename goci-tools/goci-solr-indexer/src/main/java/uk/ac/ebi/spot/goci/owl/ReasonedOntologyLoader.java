package uk.ac.ebi.spot.goci.owl;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads an ontology using the OWLAPI and a HermiT reasoner to classify the ontology.  This allows for richer typing
 * information on each class to be provided
 *
 * @author Tony Burdett
 * @date 03/06/13
 */
public class ReasonedOntologyLoader extends AbstractOntologyLoader {
    protected OWLOntology indexOntology(OWLOntology ontology) throws OWLOntologyCreationException {
        getLog().debug("Trying to create a reasoner over ontology '" + getOntologyURI() + "'");
        OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
        ReasonerProgressMonitor progressMonitor = new LoggingReasonerProgressMonitor(getLog());
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        OWLReasoner reasoner = factory.createReasoner(ontology, config);

        getLog().debug("Precomputing inferences...");
        reasoner.precomputeInferences();

        getLog().debug("Checking ontology consistency...");
        reasoner.isConsistent();

        getLog().debug("Checking for unsatisfiable classes...");
        if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().size() > 0) {
            throw new OWLOntologyCreationException(
                    "Once classified, unsatisfiable classes were detected in '" + getOntologyIRI() + "'");
        }
        else {
            getLog().debug("Reasoning complete! ");
        }

        Set<OWLClass> allClasses = ontology.getClassesInSignature();
        removeExcludedClasses(ontology,
                              allClasses,
                              superclass -> reasoner.getSubClasses(superclass, false).getFlattened());

        int labelCount = 0;
        int labelledClassCount = 0;
        int synonymCount = 0;
        int synonymedClassCount = 0;
        getLog().debug("Loading labels...");
        for (OWLClass ontologyClass : allClasses) {
            IRI clsIri = ontologyClass.getIRI();

            // get IRI fragment/path
            Optional<String> accession = evaluateAccessionValue(ontology, ontologyClass);
            if (accession.isPresent()) {
                addClassAccession(clsIri, accession.get());
            }

            // get label annotations
            Optional<String> label = evaluateLabelAnnotationValue(ontology, ontologyClass);
            if (label.isPresent()) {
                addClassLabel(clsIri, label.get());
                labelledClassCount++;
                labelCount++;
            }

            // get all synonym annotations
            getLog().debug("Loading synonyms...");
            Set<String> synonyms = evaluateSynonymAnnotationValues(ontology, ontologyClass);
            if (!synonyms.isEmpty()) {
                addSynonyms(clsIri, synonyms);
                synonymCount += synonyms.size();
                synonymedClassCount++;
            }

            // get parent labels
            getLog().debug("Loading parents...");
            Set<OWLClass> parents = reasoner.getSuperClasses(ontologyClass, false).getFlattened();
            // only add type if the parent isn't excluded
            Set<String> parentLabelSet = parents.stream()
                    .filter(allClasses::contains)
                    .peek(parent -> getLog().trace("Next parent of " + label + ": " + parent))
                    .map(parent -> evaluateLabelAnnotationValue(ontology, parent))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            label.ifPresent(parentLabelSet::add); // always also add current class to the parents
            addClassParentLabels(clsIri, parentLabelSet);

            // get child labels
            getLog().debug("Loading children...");
            Set<String> childLabelSet = new HashSet<>();
            label.ifPresent(childLabelSet::add); // always add current class to the parents
            Set<OWLClass> children = reasoner.getSubClasses(ontologyClass, false).getFlattened();
            // only add type if the child isn't excluded
            children.stream()
                    .filter(allClasses::contains)
                    .forEach(childClass -> {
                        // only add type if the parent isn't excluded
                        getLog().debug("Next child of " + label + ": " + childClass);
                        evaluateLabelAnnotationValue(ontology, childClass).ifPresent(childLabelSet::add);
                    });
            addClassChildLabels(clsIri, childLabelSet);

            // todo - get relationships


        }

        getLog().debug("Successfully indexed " + labelCount + " labels on " + labelledClassCount + " classes and " +
                               synonymCount + " synonyms on " + synonymedClassCount + " classes!");

        return ontology;
    }

    protected class LoggingReasonerProgressMonitor implements ReasonerProgressMonitor {
        private final Logger log;
        private int lastPercent = 0;

        public LoggingReasonerProgressMonitor(Logger log) {
            this.log = log;
        }

        protected Logger getLog() {
            return log;
        }

        @Override public void reasonerTaskStarted(String s) {
            getLog().debug(s);
        }

        @Override public void reasonerTaskStopped() {
            getLog().debug("100% done!");
            lastPercent = 0;
        }

        @Override public void reasonerTaskProgressChanged(int value, int max) {
            if (max > 0) {
                int percent = value * 100 / max;
                if (lastPercent != percent) {
                    if (percent % 25 == 0) {
                        getLog().debug("" + percent + "% done...");
                    }
                    lastPercent = percent;
                }
            }
        }

        @Override public void reasonerTaskBusy() {

        }
    }
}
