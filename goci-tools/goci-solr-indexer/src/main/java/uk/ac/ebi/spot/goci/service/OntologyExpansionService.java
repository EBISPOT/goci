package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.TraitDocument;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A service that is capable of enriching {@link uk.ac.ebi.spot.goci.model.TraitDocument}s with information obtained
 * from a supplied {@link uk.ac.ebi.spot.goci.owl.OntologyLoader}
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class OntologyExpansionService implements DocumentEnrichmentService<TraitDocument> {
    private OntologyLoader ontologyLoader;

    @Autowired
    public OntologyExpansionService(OntologyLoader ontologyLoader) {
        this.ontologyLoader = ontologyLoader;
    }

    @Override public void doEnrichment(TraitDocument traitDocument) {
        // improve trait document with parent and child terms etc here
        URI traitURI = URI.create(traitDocument.getTraitUri());

        // get additional fields for trait documents
        IRI traitIRI = IRI.create(traitURI);
        traitDocument.setShortForm(ontologyLoader.getAccession(traitIRI));
        traitDocument.setSuperclassLabels(ontologyLoader.getParentLabels(traitIRI));
        traitDocument.setSubclassLabels(ontologyLoader.getChildLabels(traitIRI));
        traitDocument.setSynonyms(ontologyLoader.getSynonyms(traitIRI));

        // get relationships
        Map<String, Set<String>> relations = new HashMap<>();
        ontologyLoader.getRelationships(traitIRI)
                .stream()
                .forEach(relationship -> {
                    if (relations.containsKey(relationship.getPredicateLabel())) {
                        relations.put(relationship.getPredicateLabel(), new HashSet<>());
                    }
                    relations.get(relationship.getPredicateLabel()).add(relationship.getObjectLabel());
                });
        traitDocument.setRelations(relations);
    }
}
