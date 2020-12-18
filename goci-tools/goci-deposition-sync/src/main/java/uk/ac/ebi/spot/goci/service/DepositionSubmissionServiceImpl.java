package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DepositionSubmissionServiceImpl implements DepositionSubmissionService {

    @Value("${deposition.ingest.uri}")
    private String depositionIngestUri;

    @Value("${deposition.token}")
    private String depositionToken;

    @Value("${deposition.uri}")
    private String depositionBackendUri;

    @Autowired
    private RestTemplate template;

    @Override
    public Map<String, DepositionSubmission> getSubmissions() {
        String url = "/submissions";
        Map<String, DepositionSubmission> submissionList = new TreeMap<>();
        Map<String, Integer> params = new HashMap<>();

        try {
            DepositionSubmission[] submissions =
                    template.getForObject(depositionIngestUri + url, DepositionSubmission[].class, params);
            for (DepositionSubmission submission : submissions) {
                submissionList.put(submission.getSubmissionId(), submission);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return submissionList;
    }

    public void updateSubmission(DepositionSubmission depositionSubmission, String submissionStatus) {
        depositionSubmission.setStatus(submissionStatus);
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());

        try {
            template.put(depositionIngestUri + "/submissions/{submissionID}", depositionSubmission, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
