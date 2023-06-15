package uk.ac.ebi.spot.goci.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.BodyOfWorkDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPublicationListWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class DepositionPublicationServiceImpl implements DepositionPublicationService {

    private static final Logger log = LoggerFactory.getLogger(DepositionPublicationServiceImpl.class);

    @Value("${deposition.ingest.uri}")
    private String depositionIngestUri;

    @Value("${deposition.token}")
    private String depositionToken;

    @Value("${deposition.uri}")
    private String depositionBackendUri;

    private final static String API_V1 = "/v1";

    private final static String API_V2 = "/v2";

    @Autowired
    @Qualifier("JodaMapper")
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate template;

    @Override
    public DepositionPublication retrievePublication(String id) {
        log.info("Retrieving publication using id [{}]", id);
        DepositionPublication publication = null;
        Map<String, String> params = new HashMap<>();
        params.put("pmID", id);
        String url = depositionIngestUri + API_V1 + "/publications/{pmID}?pmid=true";
        try {
            String response = template.getForObject(url, String.class, params);
            publication = template.getForObject(url, DepositionPublication.class, params);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return publication;
    }

    @Override
    public void addPublication(DepositionPublication depositionPublication) {
        String url = depositionIngestUri + API_V1 + "/publications";
        template.postForObject(url, depositionPublication, DepositionPublication.class);
    }

    @Override
    public void updatePublication(DepositionPublication depositionPublication) {
        String url = depositionIngestUri + API_V1 + "/publications/" + depositionPublication.getPmid();
        template.put(url, depositionPublication);
    }

    @Override
    public void deletePublication(DepositionPublication depositionPublication) {
        String url = depositionIngestUri + API_V1 + "/publications/" + depositionPublication.getPmid();
        try {
            template.delete(url);
        } catch (Exception e) {
            System.out.println("Encoutered exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public DepositionSubmission retrieveSubmission(String id) {
        log.info("Retrieving submission using id [{}]", id);
        DepositionSubmission submission = null;
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", id);
        String url = depositionIngestUri + API_V1 + "/submissions/{submissionID}";
        try {
            String response = template.getForObject(url, String.class, params);
            submission = template.getForObject(url, DepositionSubmission.class, params);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return submission;
    }

    @Override
    public void addSubmission(DepositionSubmission depositionSubmission) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());
        String url = depositionIngestUri + API_V1 + "/submissions/{submissionID}";
        template.put(url, depositionSubmission, params);
    }

    @Override
    public Map<String, DepositionPublication> getAllPublications() {
        log.info("Retrieving publications");
        String url = depositionIngestUri + API_V1 + "/publications?page={page}&size=100";
        return getAllPublications(url);
    }

    @Override
    public Map<String, BodyOfWorkDto> getAllBodyOfWork() {
        log.info("Retrieving publications");
        String url = depositionIngestUri + API_V1 + "/bodyofwork/?status=UNDER_SUBMISSION&page={page}&size=100";
        Map<String, BodyOfWorkDto> bomMap = new HashMap<>();
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);
            String response = template.getForObject(url, String.class, params);
            BodyOfWorkDto[] bomArray = template.getForObject(url, BodyOfWorkDto[].class,
                    params);
            //while(i < publications.getPage().getTotalPages()){
            if (bomArray != null) {
                Arrays.stream(bomArray).forEach(bom -> bomMap.put(bom.getBodyOfWorkId(), bom));
            }
            params.put("page", ++i);
//                publications = template.getForObject(url, DepositionPublicationListWrapper.class,
//                        params);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return bomMap;
    }


    @Override
    public Map<String, DepositionPublication> getAllBackendPublications() {
        log.info("Retrieving publications");
        String url = depositionBackendUri + API_V1 + "/publications?page={page}&size=100";
        Map<String, DepositionPublication> publicationMap = new HashMap<>();
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);
            String response = template.getForObject(url, String.class, params);
            DepositionPublicationListWrapper publications = template.getForObject(url, DepositionPublicationListWrapper.class,
                    params);
            while (i < publications.getPage().getTotalPages()) {
                addPublications(publicationMap, publications);
                params.put("page", ++i);
                publications = template.getForObject(url, DepositionPublicationListWrapper.class, params);
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return publicationMap;
    }

    private Map<String, DepositionPublication> getAllPublications(String url) {
        Map<String, DepositionPublication> publicationMap = new HashMap<>();
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);
            String response = template.getForObject(url, String.class, params);
            DepositionPublication[] publications = template.getForObject(url, DepositionPublication[].class,
                    params);
            //while(i < publications.getPage().getTotalPages()){
            addPublications(publicationMap, publications);
            params.put("page", ++i);
//                publications = template.getForObject(url, DepositionPublicationListWrapper.class,
//                        params);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }
        return publicationMap;
    }

    private void addPublications(Map<String, DepositionPublication> publicationMap,
                                 DepositionPublicationListWrapper publications) {
        addPublications(publicationMap,
                publications.getPublications().getPublications().toArray(new DepositionPublication[0]));
    }

    private void addPublications(Map<String, DepositionPublication> publicationMap,
                                 DepositionPublication[] publications) {
        if (publications != null) {// && publications.getPublications() != null) {
            for (DepositionPublication publication : publications) {//.getPublications().getPublications()) {
                publicationMap.put(publication.getPmid(), publication);
            }
        }
    }
}