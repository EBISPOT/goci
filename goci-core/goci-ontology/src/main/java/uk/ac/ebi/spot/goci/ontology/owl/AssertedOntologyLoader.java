package uk.ac.ebi.spot.goci.ontology.owl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads an ontology using the OWLAPI, and considers only axioms that are asserted in the loaded ontology when
 * generating class labels and types
 *
 * @author Tony Burdett
 * @author James Malone
 * @date 15/02/12
 */

public class AssertedOntologyLoader extends AbstractOntologyLoader {
    protected OWLOntology indexOntology(final OWLOntology ontology) throws OWLOntologyCreationException {
        Set<OWLClass> allClasses = ontology.getClassesInSignature();
        removeExcludedClasses(ontology, allClasses, superclass -> getSubClasses(ontology, superclass));

        int labelCount = 0;
        int labelledClassCount = 0;
        int synonymCount = 0;
        int synonymedClassCount = 0;
        getLog().debug("Loading labels and synonyms...");
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
            Set<String> parentLabelSet = new HashSet<>();
            Set<OWLClass> parents = getSuperClasses(ontology, ontologyClass);
            // only add type if the parent isn't excluded
            parents.stream()
                    .filter(allClasses::contains)
                    .forEach(parentClass -> {
                        // only add type if the parent isn't excluded
                        getLog().debug("Next parent of " + label + ": " + parentClass);
                        evaluateLabelAnnotationValue(ontology, parentClass).ifPresent(parentLabelSet::add);
                    });
            addClassParentLabels(clsIri, parentLabelSet);

            // get child labels
            getLog().debug("Loading children...");
            Set<String> childLabelSet = new HashSet<>();
            label.ifPresent(childLabelSet::add); // always add current class to the parents
            Set<OWLClass> children = getSubClasses(ontology, ontologyClass);
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

    protected Set<OWLClass> getSubClasses(OWLOntology owlOntology, OWLClass owlClass) {
        return owlOntology.getSubClassAxiomsForSuperClass(owlClass)
                .stream()
                .map(OWLSubClassOfAxiom::getSubClass)
                .map(OWLClassExpression::asOWLClass)
                .collect(Collectors.toSet());
    }

    protected Set<OWLClass> getSuperClasses(OWLOntology owlOntology, OWLClass owlClass) {
        return owlOntology.getSubClassAxiomsForSubClass(owlClass)
                .stream()
                .map(OWLSubClassOfAxiom::getSuperClass)
                .map(OWLClassExpression::asOWLClass)
                .collect(Collectors.toSet());
    }
}
