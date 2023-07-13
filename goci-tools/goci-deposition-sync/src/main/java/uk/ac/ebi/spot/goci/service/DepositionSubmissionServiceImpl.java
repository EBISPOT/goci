package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmissionDto;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionSampleListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionStudyListWrapper;
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

    private final static String API_V1 = "/v1";

    private final static String API_V2 = "/v2";

    @Autowired
    private RestTemplate template;

    @Override
    public Map<String, DepositionSubmission> getSubmissions() {

        String url = String.format("%s%s%s", depositionIngestUri,API_V1, "/submissions");
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

    public DepositionSampleListWrapper getSamples(String uri , String submissionId) {
        String targetUri = uri;
        if(uri.isEmpty()) {
            uri = String.format("%s%s%s%s%s", depositionIngestUri,API_V1, "/submissions/", submissionId, "/samples");
            MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
            paramsMap.add("size","500");
            targetUri = UriComponentsBuilder.fromHttpUrl(uri).queryParams(paramsMap).build().toUriString();
        }
        DepositionSampleListWrapper depositionSampleListWrapper = null;
        try {
            log.info("The Samples API based in submission is ->"+targetUri);
            depositionSampleListWrapper = template.getForObject(targetUri , DepositionSampleListWrapper.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return depositionSampleListWrapper;
    }


    public DepositionStudyListWrapper getSubmissionStudies(String uri, String submissionId) {
        String targetUri = uri;
        if(uri.isEmpty()) {
            uri = String.format("%s%s%s%s%s", depositionIngestUri,API_V1, "/submissions/", submissionId, "/studies");
            MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
            paramsMap.add("size","500");
            targetUri = UriComponentsBuilder.fromHttpUrl(uri).queryParams(paramsMap).build().toUriString();
        }

        DepositionStudyListWrapper studyListWrapper = null;
        try {
            log.info("The Studies API based in submission is ->"+uri);
            studyListWrapper = template.getForObject(targetUri, DepositionStudyListWrapper.class);
        } catch (Exception e) {
            log.error("Exception in rest API call sor studies API" + e.getMessage(), e);
        }
        return studyListWrapper;
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
            template.put(depositionIngestUri + API_V1 + "/submissions/{submissionID}", depositionSubmission, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
