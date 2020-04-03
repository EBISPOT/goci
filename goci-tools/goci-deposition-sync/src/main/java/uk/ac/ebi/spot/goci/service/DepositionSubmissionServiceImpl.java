package uk.ac.ebi.spot.goci.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DepositionSubmissionServiceImpl implements DepositionSubmissionService {

    @Value("${deposition.ingest.uri}")
    private String depositionIngestUri;

    @Value("${deposition.token}")
    private String depositionToken;

    @Value("${deposition.uri}")
    private String depositionBackendUri;

    @Autowired
    @Qualifier("JodaMapper")
    private ObjectMapper mapper;

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

    public void updateSubmission(DepositionSubmission depositionSubmission){
        try {
            String message = mapper.writeValueAsString(depositionSubmission);
            template.put(depositionIngestUri + "/submissions/" + depositionSubmission.getSubmissionId(), depositionSubmission);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}
