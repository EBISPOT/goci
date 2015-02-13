package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.SnpDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class SnpEnrichmentService implements DocumentEnrichmentService<SnpDocument> {
    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(SnpDocument document) {

    }
}
