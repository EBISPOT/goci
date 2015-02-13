package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class AssociationEnrichmentService implements DocumentEnrichmentService<AssociationDocument> {
    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(AssociationDocument document) {

    }
}
