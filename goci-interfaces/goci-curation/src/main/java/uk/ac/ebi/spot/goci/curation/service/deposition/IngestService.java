package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;

@Service
public class IngestService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private RestTemplate template;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;

    @Value("${deposition.token}")
    private String depositionToken;

    public String updateSubmissionStatus(DepositionSubmission depositionSubmission, String submissionStatus, String pubStatus) {
        getLog().info("Updating submission status: {} | {}", submissionStatus, pubStatus);
        depositionSubmission.setStatus(submissionStatus);
        depositionSubmission.getPublication().setStatus(pubStatus);
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());

        getLog().info("[IMPORT] Sending request to update submission in the Deposition App.");
        try {
            template.put(depositionIngestURL + "/submissions/{submissionID}", depositionSubmission, params);
        } catch (Exception e) {
            getLog().error("Unable to call Ingest service: {}", e.getMessage(), e);
            return "Unable to call Ingest service: " + e.getMessage();
        }

        return null;
    }
}
