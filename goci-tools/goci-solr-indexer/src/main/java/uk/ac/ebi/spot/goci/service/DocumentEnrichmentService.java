package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Document;
import uk.ac.ebi.spot.goci.model.EnrichableDocument;

/**
 * A service that can be used to add enrich an {@link EnrichableDocument} prior to indexing by adding additional
 * information to it, which may be obtained from an external source
 *
 * @author Tony Burdett
 * @date 19/01/15
 */
public interface DocumentEnrichmentService<D extends Document> {
    void doEnrichment(D document);
}
