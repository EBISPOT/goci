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

    @Override public int getPriority() {
        return 2;
    }

    @Override public void doEnrichment(TraitDocument traitDocument) {
        // improve trait document with parent and child terms etc here
        for (String traitUriString : traitDocument.getTraitUris()) {
            URI traitURI = URI.create(traitUriString);

            // get additional fields for trait documents
            IRI traitIRI = IRI.create(traitURI);
            traitDocument.addShortForm(ontologyLoader.getAccession(traitIRI));
            traitDocument.addLabel(ontologyLoader.getLabel(traitIRI));

            String efolink = ontologyLoader.getLabel(traitIRI).concat("|").concat(ontologyLoader.getAccession(traitIRI)).concat("|").concat(traitIRI.toString());
            traitDocument.addEfoLink(efolink);
            ontologyLoader.getParentLabels(traitIRI).forEach(traitDocument::addSuperclassLabel);
            ontologyLoader.getChildLabels(traitIRI).forEach(traitDocument::addSubclassLabel);
            ontologyLoader.getSynonyms(traitIRI).forEach(traitDocument::addSynonym);

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
            for (Map.Entry<String, Set<String>> entry : relations.entrySet()) {
                traitDocument.addRelation(entry.getKey(), entry.getValue());
            }
        }
    }
}
