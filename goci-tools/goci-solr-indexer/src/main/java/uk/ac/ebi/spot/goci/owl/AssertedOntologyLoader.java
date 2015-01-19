package uk.ac.ebi.spot.goci.owl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
    protected OWLOntology indexOntology(OWLOntology ontology) throws OWLOntologyCreationException {
        Set<OWLClass> allClasses = ontology.getClassesInSignature();

        // remove excluded classes from allClasses by subclass
        if (getExclusionClassURI() != null) {
            OWLClass excludeClass = getFactory().getOWLClass(IRI.create(getExclusionClassURI()));
            for (OWLClassExpression subClassExpression : getSubClasses(ontology, excludeClass)) {
                OWLClass subclass = subClassExpression.asOWLClass();
                allClasses.remove(subclass);
            }
        }

        // remove excluded classes from allClasses by annotation property
        if (getExclusionAnnotationURI() != null) {
            OWLAnnotationProperty excludeAnnotation =
                    getFactory().getOWLAnnotationProperty(IRI.create(getExclusionAnnotationURI()));
            Iterator<OWLClass> allClassesIt = allClasses.iterator();
            while (allClassesIt.hasNext()) {
                OWLClass owlClass = allClassesIt.next();
                Collection<OWLAnnotation> annotations = owlClass.getAnnotations(ontology, excludeAnnotation);
                if (!annotations.isEmpty()) {
                    allClassesIt.remove();
                }
            }
        }

        OWLAnnotationProperty rdfsLabel =
                getFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Collection<OWLAnnotationProperty> synonyms = getSynonymURIs().stream()
                .map(ap -> getFactory().getOWLAnnotationProperty(IRI.create(ap)))
                .collect(Collectors.toSet());

        int labelCount = 0;
        int labelledClassCount = 0;
        int synonymCount = 0;
        int synonymedClassCount = 0;
        getLog().debug("Loading labels and synonyms...");
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
            Set<String> ontologyTypeLabelSet = new HashSet<>();
            for (OWLClassExpression parentClassExpression : getSuperClasses(ontology, ontologyClass)) {
                if (!parentClassExpression.isAnonymous()) {
                    OWLClass parentClass = parentClassExpression.asOWLClass();
                    getLog().debug("Next parent of " + label + ": " + parentClass);
                    Set<String> typeVals = getStringLiteralAnnotationValues(ontology, parentClass, rdfsLabel);
                    ontologyTypeLabelSet.addAll(typeVals);
                }
                else {
                    getLog().trace("OWLClassExpression " + parentClassExpression + " is an anonymous class. " +
                                           "No synonyms for this class will be loaded.");
                }
            }
            addClassTypes(clsIri, ontologyTypeLabelSet);

            // get all synonym annotations
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

    protected Set<OWLClassExpression> getSubClasses(OWLOntology owlOntology, OWLClass owlClass) {
        return owlOntology.getSubClassAxiomsForSuperClass(owlClass)
                .stream()
                .map(OWLSubClassOfAxiom::getSubClass)
                .collect(Collectors.toSet());
    }

    protected Set<OWLClassExpression> getSuperClasses(OWLOntology owlOntology, OWLClass owlClass) {
        return owlOntology.getSubClassAxiomsForSubClass(owlClass)
                .stream()
                .map(OWLSubClassOfAxiom::getSuperClass)
                .collect(Collectors.toSet());
    }
}
