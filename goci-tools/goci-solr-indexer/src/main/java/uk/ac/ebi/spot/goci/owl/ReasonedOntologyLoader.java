package uk.ac.ebi.spot.goci.owl;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.semanticweb.owlapi.search.Searcher.annotations;

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
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
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

        // remove excluded classes from allClasses by subclass
        if (getExclusionClassURI() != null) {
            OWLClass excludeClass = getFactory().getOWLClass(IRI.create(getExclusionClassURI()));
            Set<OWLClass> subclasses = reasoner.getSubClasses(excludeClass, false).getFlattened();
            subclasses.forEach(allClasses::remove);
        }

        // remove excluded classes from allClasses by annotation property
        if (getExclusionAnnotationURI() != null) {
            OWLAnnotationProperty excludeAnnotation =
                    getFactory().getOWLAnnotationProperty(IRI.create(getExclusionAnnotationURI()));
            Iterator<OWLClass> allClassesIt = allClasses.iterator();
            while (allClassesIt.hasNext()) {
                OWLClass owlClass = allClassesIt.next();
                Collection<OWLAnnotation> annotations =
                        annotations(ontology.getAnnotationAssertionAxioms(owlClass.getIRI()), excludeAnnotation);
                if (!annotations.isEmpty()) {
                    allClassesIt.remove();
                }
            }
        }

        OWLAnnotationProperty rdfsLabel = getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Collection<OWLAnnotationProperty> synonyms = getSynonymURIs().stream()
                .map(ap -> getFactory().getOWLAnnotationProperty(IRI.create(ap)))
                .collect(Collectors.toSet());

        int labelCount = 0;
        int labelledClassCount = 0;
        int synonymCount = 0;
        int synonymedClassCount = 0;
        getLog().debug("Loading labels...");
        for (OWLClass ontologyClass : allClasses) {
            IRI clsIri = ontologyClass.getIRI();

            // get label annotations
            Set<String> labels = getStringLiteralAnnotationValues(ontology, ontologyClass, rdfsLabel);
            String label = null;
            if (labels.isEmpty()) {
                getLog().warn("OWLClass " + ontologyClass + " contains no label. " +
                                      "No labels for this class will be loaded.");
            }
            else {
                if (labels.size() > 1) {
                    getLog().warn("OWLClass " + ontologyClass + " contains more than one label " +
                                          "(including '" + labels.iterator().next() + "'). " +
                                          "No labels for this class will be loaded.");
                }
                else {
                    label = labels.iterator().next();
                    addClassLabel(clsIri, label);
                    labelledClassCount++;
                    labelCount++;
                }
            }

            // get types
            getLog().debug("Loading types...");
            Set<String> ontologyTypeLabelSet = new HashSet<>();
            Set<OWLClass> parents = reasoner.getSuperClasses(ontologyClass, false).getFlattened();
            for (OWLClass parentClass : parents) {
                if (allClasses.contains(parentClass)) {
                    // only add type if the parent isn't excluded
                    getLog().debug("Next parent of " + label + ": " + parentClass);
                    Set<String> typeVals = getStringLiteralAnnotationValues(ontology, parentClass, rdfsLabel);
                    ontologyTypeLabelSet.addAll(typeVals);
                }
            }
            addClassTypes(clsIri, ontologyTypeLabelSet);

            // get all synonym annotations
            getLog().debug("Loading synonyms...");
            for (OWLAnnotationProperty synonym : synonyms) {
                Set<String> synonymVals = getStringLiteralAnnotationValues(ontology, ontologyClass, synonym);
                if (synonymVals.isEmpty()) {
                    getLog().trace("OWLClass " + ontologyClass + " contains no synonyms. " +
                                           "No synonyms for this class will be loaded.");
                }
                else {
                    addSynonyms(clsIri, synonymVals);
                    synonymCount += synonymVals.size();
                    synonymedClassCount++;
                }
            }
        }

        getLog().debug("Successfully indexed " + labelCount + " labels on " + labelledClassCount + " classes and " +
                               synonymCount + " synonyms on " + synonymedClassCount + " classes!");

        return ontology;
    }
}
