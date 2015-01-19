package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.TraitDocument;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;

import java.net.URI;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class OntologyExpansionService implements DocumentEnrichmentService<TraitDocument> {
    @Autowired OntologyLoader ontologyLoader;

    @Override public void doEnrichment(TraitDocument traitDocument) {
        // improve trait document with parent and child terms etc here
        URI traitURI = URI.create(traitDocument.getTraitUri());

        IRI traitIRI = IRI.create(traitURI);
        traitDocument.setSuperclassLabels(ontologyLoader.getTypeLabels(traitIRI));
        traitDocument.setSynonyms(ontologyLoader.getSynonyms(traitIRI));
    }
}
