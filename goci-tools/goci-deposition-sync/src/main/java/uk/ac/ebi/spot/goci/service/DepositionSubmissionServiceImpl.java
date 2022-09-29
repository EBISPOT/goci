package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmissionDto;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.model.deposition.SubmissionViewDto;
import uk.ac.ebi.spot.goci.util.DepositionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DepositionSubmissionServiceImpl implements DepositionSubmissionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        String url = String.format("%s%s", depositionIngestUri, "/submissions");
        int page = 0; int pageSize = 100;
        Pageable pageable = new PageRequest(page, pageSize);

        Map<String, DepositionSubmission> submissionList = new TreeMap<>();
        URI uri = DepositionUtil.buildUrl(url, pageable);
        DepositionSubmissionDto depositionSubmissionDto = getSubmissionsWithPagination(uri);
        depositionSubmissionDto.getWrapper().getSubmissions().forEach(submission -> {
            submissionList.put(submission.getSubmissionId(), submission);
        });

        int batchSize = depositionSubmissionDto.getPage().getTotalPages();
        for (int nextPage=1; nextPage<batchSize; nextPage++){
            uri = DepositionUtil.buildUrl(url, new PageRequest(nextPage, pageSize));
            depositionSubmissionDto = getSubmissionsWithPagination(uri);
            depositionSubmissionDto.getWrapper().getSubmissions().forEach(submission -> {
                submissionList.put(submission.getSubmissionId(), submission);
            });
        }

        return submissionList;
    }

    private DepositionSubmissionDto getSubmissionsWithPagination(URI targetUrl) {
        DepositionSubmissionDto depositionSubmissionDto = DepositionSubmissionDto.builder().build();
        try {
            depositionSubmissionDto = template.getForObject(targetUrl, DepositionSubmissionDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return depositionSubmissionDto;
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
