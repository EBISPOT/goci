package uk.ac.ebi.spot.goci.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.DepositionPublication;
import uk.ac.ebi.spot.goci.model.DepositionSubmission;

import java.util.HashMap;
import java.util.Map;

@Service
public class DepositionPublicationServiceImpl implements DepositionPublicationService {

    private static final Logger log = LoggerFactory.getLogger(DepositionPublicationServiceImpl.class);

    @Value("${deposition.uri}")
    private String depositionUri;

    @Autowired
    @Qualifier("JodaMapper")
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate template;

    @Override
    public DepositionPublication retrievePublication(String id) {
        log.info("Retrieving publication using id [{}]", id);
        RestTemplate template = new RestTemplate();
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
        String url = depositionUri + "/publications";
        try {
            String message = mapper.writeValueAsString(depositionPublication);
            System.out.println(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DepositionPublication response = template.postForObject(url, depositionPublication, DepositionPublication.class);
        System.out.println("created " + response);
    }

    @Override
    public DepositionSubmission retrieveSubmission(String id) {
        log.info("Retrieving submission using id [{}]", id);
        RestTemplate template = new RestTemplate();
        DepositionSubmission submission = null;
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", id);
        String url = depositionUri + "/submissions/{submissionID}";
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
        RestTemplate template = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("submissionID", depositionSubmission.getSubmissionId());
        String url = depositionUri + "/submissions/{submissionID}";
        template.put(url, depositionSubmission, params);
    }
}