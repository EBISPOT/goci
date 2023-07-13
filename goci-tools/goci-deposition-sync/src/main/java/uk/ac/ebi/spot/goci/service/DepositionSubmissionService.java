package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSampleListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;

import java.util.Map;

public interface DepositionSubmissionService {
    Map<String, DepositionSubmission> getSubmissions();

    void updateSubmission(DepositionSubmission depositionSubmission, String submissionStatus);

    DepositionSampleListWrapper getSamples(String uri , String submissionId);

    DepositionStudyListWrapper getSubmissionStudies(String uri, String submissionId);
}
