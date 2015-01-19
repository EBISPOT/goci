package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.TraitDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
@Service
public class OntologyExpansionService implements DocumentEnrichmentService<TraitDocument> {
    @Override public void doEnrichment(TraitDocument traitDocument) {
        // improve trait document with parent and child terms etc here




    }
}
