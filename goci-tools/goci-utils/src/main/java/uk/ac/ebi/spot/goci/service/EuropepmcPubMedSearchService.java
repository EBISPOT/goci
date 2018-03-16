package uk.ac.ebi.spot.goci.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mashape.unirest.http.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.RestResponseResult;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.exception.PubmedLookupException;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;
import uk.ac.ebi.spot.goci.utils.EuropePMCDeserializer;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cinzia on 20/09/2017.
 */

@Service
@Component
public class EuropepmcPubMedSearchService implements PubMedSearchService {

    @Value("${europepmc.root}")
    private String europepmcRoot;

    @Value("${europepmc.search.pubmed}")
    private String europepmcSearch;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Study findPublicationSummary(String pubmedId) throws PubmedLookupException {
        return new Study();
    }


    public EuropePMCData createStudyByPubmed(String pubmedId) throws PubmedLookupException {
        EuropePMCData europePMCData = new EuropePMCData();

        String urlRequest;
        if (europepmcRoot != null && europepmcSearch != null) {
            urlRequest = europepmcRoot.concat(europepmcSearch);
        }
        else {
            throw new PubmedLookupException(
                    "Unable to search pubmed - no URL configured. " +
                            "Set europepmc properties in your config!");
        }


        String queryUrl = urlRequest.replace("{idlist}", pubmedId);
        ResponseEntity<String> out;
        RestResponseResult result = new RestResponseResult();
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML));

        HttpEntity<Object> entity = new HttpEntity<Object>(headers);

        getLog().debug("Querying " + queryUrl);

        //and do I need this JSON media type for my use case?
        try {
            out = restTemplate.exchange(queryUrl, HttpMethod.GET, entity, String.class);
            result.setStatus(out.getStatusCode().value());
            result.setUrl(queryUrl);
            System.out.println(queryUrl);
            JsonNode body = new JsonNode(out.getBody().toString());
            result.setRestResult(body);
        }
        catch (Exception e) {
            throw new PubmedLookupException("EuropePMC : REST API Failed");
        }

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(EuropePMCData.class, new EuropePMCDeserializer());
        mapper.registerModule(module);

        try {
            europePMCData = mapper.readValue(result.getRestResult().toString(), EuropePMCData.class);

        } catch (IOException ioe) {
            System.out.println("EuropePMC : IO Exception - JSON conversion");
            throw new PubmedLookupException("EuropePMC : IO Exception - JSON conversion");

        }
        catch (Exception e) {
            System.out.println("EuropePMC : Generic Error conversion JSON");
            throw new PubmedLookupException("EuropePMC : Generic Error conversion JSON");
        }
        return europePMCData;
    }


}