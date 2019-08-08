package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.DepositionPublication;
import uk.ac.ebi.spot.goci.model.DepositionSubmission;

import java.util.Map;

public interface DepositionPublicationService {
    DepositionPublication retrievePublication(String id);

    void addPublication(DepositionPublication depositionPublication);

    DepositionSubmission retrieveSubmission(String id);

    void addSubmission(DepositionSubmission depositionSubmission);

    Map<String, DepositionPublication> getAllPublications();
}
