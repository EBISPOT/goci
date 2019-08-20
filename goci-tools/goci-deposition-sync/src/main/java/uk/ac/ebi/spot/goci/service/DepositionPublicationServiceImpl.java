package uk.ac.ebi.spot.goci.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.deposition.DepositionPublication;
import uk.ac.ebi.spot.goci.model.deposition.util.DepositionPublicationListWrapper;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;

@Service
public class DepositionPublicationServiceImpl implements DepositionPublicationService {

    private static final Logger log = LoggerFactory.getLogger(DepositionPublicationServiceImpl.class);

    @Value("${deposition.uri}")
    private String depositionUri;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestUri;

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
        String url = depositionUri + "/publications/{pmID}?pmid=true";
        try {
            String response = template.getForObject(url, String.class, params);
            publication = template.getForObject(url, DepositionPublication.class, params);
        }catch(HttpClientErrorException e){
            System.out.println(e.getMessage());
        }
        return publication;
    }

    @Override
    public void addPublication(DepositionPublication depositionPublication) {
        String url = depositionIngestUri + "/publications";
        try {
            String message = mapper.writeValueAsString(depositionPublication);
            System.out.println(message);
            DepositionPublication response = template.postForObject(url, depositionPublication, DepositionPublication.class);
            System.out.println("created " + response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePublication(DepositionPublication depositionPublication) {
        String url = depositionIngestUri + "/publications";
        try {
            String message = mapper.writeValueAsString(depositionPublication);
            System.out.println(message);
            template.put(url, depositionPublication);
            System.out.println("updated " + depositionPublication);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DepositionSubmission retrieveSubmission(String id) {
        log.info("Retrieving submission using id [{}]", id);
        DepositionSubmission submission = null;
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", id);
        String url = depositionIngestUri + "/submissions/{submissionID}";
        try {
            String response = template.getForObject(url, String.class, params);
            submission = template.getForObject(url, DepositionSubmission.class, params);
        }catch(HttpClientErrorException e){
            System.out.println(e.getMessage());
        }
        return submission;
    }

    @Override
    public void addSubmission(DepositionSubmission depositionSubmission) {
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());
        String url = depositionUri + "/submissions/{submissionID}";
        template.put(url, depositionSubmission, params);
    }

    @Override
    public Map<String, DepositionPublication> getAllPublications() {
        log.info("Retrieving publications");
        Map<String, DepositionPublication> publicationMap = new HashMap<>();
        String url = depositionUri + "/publications?page={page}&size=100";
        try {
            int i = 0;
            Map<String, Integer> params = new HashMap<>();
            params.put("page", i);
            String response = template.getForObject(url, String.class, params);
            DepositionPublicationListWrapper publications = template.getForObject(url, DepositionPublicationListWrapper.class,
                    params);
            while(i < publications.getPage().getTotalPages()){
                addPublications(publicationMap, publications);
                params.put("page", ++i);
                publications = template.getForObject(url, DepositionPublicationListWrapper.class,
                        params);
            }
        }catch(HttpClientErrorException e){
            System.out.println(e.getMessage());
        }
        return publicationMap;
    }

    private void addPublications(Map<String, DepositionPublication> publicationMap,
                                 DepositionPublicationListWrapper publications){
        if(publications != null && publications.getPublications() != null) {
            for (DepositionPublication publication : publications.getPublications().getPublications()) {
                publicationMap.put(publication.getPmid(), publication);
            }
        }
    }
}