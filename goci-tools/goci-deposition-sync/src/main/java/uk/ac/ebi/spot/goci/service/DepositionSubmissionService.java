package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.Map;

public interface DepositionSubmissionService {
    Map<String, DepositionSubmission> getSubmissions();
    void updateSubmission(DepositionSubmission depositionSubmission);
    }
