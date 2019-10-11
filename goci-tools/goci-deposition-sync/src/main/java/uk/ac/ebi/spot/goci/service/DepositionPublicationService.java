package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.Map;

public interface DepositionPublicationService {
    DepositionPublication retrievePublication(String id);

    void addPublication(DepositionPublication depositionPublication);

    void updatePublication(DepositionPublication depositionPublication);

    DepositionSubmission retrieveSubmission(String id);

    void addSubmission(DepositionSubmission depositionSubmission);

    Map<String, DepositionPublication> getAllPublications();

    Map<String, DepositionPublication> getAllBackendPublications();
}
