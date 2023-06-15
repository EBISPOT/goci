package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DepositionSubmissionServiceImpl implements DepositionSubmissionService {

    private static final Logger log = LoggerFactory.getLogger(DepositionSubmissionServiceImpl.class);
    @Value("${deposition.ingest.uri}")
    private String depositionIngestUri;

    @Value("${deposition.token}")
    private String depositionToken;

    @Value("${deposition.uri}")
    private String depositionBackendUri;

    private final static String API_V1 = "/v1";

    private final static String API_V2 = "/v2";

    @Autowired
    private RestTemplate template;

    @Override
    public Map<String, DepositionSubmission> getSubmissions() {

        String url = depositionIngestUri + API_V2 + "/submissions/all";
        Map<String, DepositionSubmission> submissionList = new TreeMap<>();
        Map<String, Integer> params = new HashMap<>();
        Integer count = getSubmissionCount();
        log.info("The Submission count is "+count);
        int noOfPages = count/10;
        log.info("The noOfPages  is "+noOfPages);
        int pageSize = 10;
        try {
            for(int i = 0; i <=  noOfPages; i++ ) {
                DepositionSubmission[] submissions =
                        template.getForObject(buildPaginationParams(url, i , pageSize), DepositionSubmission[].class, params);
                for (DepositionSubmission submission : submissions) {
                   //log.info("SubmissionId in the loop is ->"+submission.getSubmissionId());
                    submissionList.put(submission.getSubmissionId(), submission);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return submissionList;
    }

    public Integer getSubmissionCount() {
        String url = API_V2 + "/submissions/count";
        Map<String, Integer> params = new HashMap<>();
        Integer countSubmissions = null;
        try {
          String countSub =  template.getForObject(depositionIngestUri + url, String.class, params);
          countSubmissions = Integer.parseInt(countSub);

        }catch (Exception e) {
            log.error("Error in Calling API for Submission Count"+e.getMessage(),e);
        }
        return countSubmissions;
    }

    public void updateSubmission(DepositionSubmission depositionSubmission, String submissionStatus) {
        depositionSubmission.setStatus(submissionStatus);
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());

        try {
            template.put(depositionIngestUri + API_V1 + "/submissions/{submissionID}", depositionSubmission, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildPaginationParams(String uri , Integer page, Integer size) {
        return UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("page",page )
                .queryParam("size", size)
                .build()
                .toUriString();

    }

}
