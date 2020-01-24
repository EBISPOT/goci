package uk.ac.ebi.spot.goci.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.exception.CustomResponseErrorHandler;
import uk.ac.ebi.spot.goci.exception.EnsemblRestClientException;
import uk.ac.ebi.spot.goci.model.RestResponseResult;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.mashape.unirest.http.JsonNode;

/**
 * Created by emma on 08/04/2017.
 *
 * @author Cinzia
 *         <p>
 *         Simple service to use RestTemplate with Ensembl Rest API
 */
@Service
public class EnsemblRestTemplateService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @NotNull
    @Value("${ensembl.server}")
    private String server;

    private Hashtable<String, String> endpoints = new Hashtable<String, String>();

    private RestTemplate restTemplate;

    @Autowired
    public void createEndpoints() {
        Hashtable<String, String> endpointsToCreate = new Hashtable<String, String>();
        String species = "homo_sapiens";
        endpointsToCreate.put("variation", "/variation/" + species + "/");
        endpointsToCreate.put("lookup_symbol", "/lookup/symbol/" + species + "/");
        endpointsToCreate.put("overlap_region", "/overlap/region/" + species + "/");
        endpointsToCreate.put("info_assembly", "/info/assembly/" + species + "/");
        endpointsToCreate.put("info_variation", "/info/variation/" + species + "/");
        endpointsToCreate.put("info_data", "/info/data/");
        setEndpoints(endpointsToCreate);
    }


    public String getRelease() {
        String eRelease = null;
        RestTemplate restTemplate = this.getRestTemplate();
        HttpEntity<Object> entity = this.getEntity();
        String url = createUrl("/info/data/","");
        getLog().debug("Querying " + url);

        try {
            ResponseEntity<String> output = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode body = new JsonNode(output.getBody().toString());
            if ( (body != null) && (body.getObject() != null)) {
                JSONObject release = body.getObject();
                if (release.has("releases")){
                    JSONArray releases = release.getJSONArray("releases");
                    eRelease = String.valueOf(releases.get(0));
                }
            }
        } catch (Exception e) { /*No release */ }

        return eRelease;
    }

    public String createUrl(String endpoint, String query) {
        String url = getServer().concat(endpoint).concat(query);
        return url;
    }


    public HttpEntity<Object> getEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML));

        HttpEntity<Object> entity = new HttpEntity<Object>(headers);

        return entity;
    }

    public RestTemplate getRestTemplate() {
        if(restTemplate == null) {
            // Create a new RestTemplate instance
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            // Add the Jackson message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        }
        return restTemplate;
    }

    public String getServer() {
        return server;
    }

    public RestResponseResult getRestCall(String endpoint_type, String data, String rest_parameters) {

        String endpoint = getEndpoints().get(endpoint_type);
        URL url = null;
        RestResponseResult restResponseResult = new RestResponseResult();

        try {

            // Build URL
            if (!Objects.equals(rest_parameters, "")) {
                Matcher matcher = Pattern.compile("^\\?").matcher(rest_parameters);
                if (!matcher.matches()) {
                    rest_parameters = '?' + rest_parameters;
                }
            }

            url = new URL(getServer() + endpoint + data + rest_parameters);
            restResponseResult = fetch(url.toString());
        }

        catch (InterruptedException | MalformedURLException  e) {
         //   getLog().error("Encountered a " + e.getClass().getSimpleName() +
         //           " whilst trying to run mapping of SNP", e);
        //    throw new EnsemblRestIOException("Encountered a " + e.getClass().getSimpleName() +
        //            " whilst trying to run mapping of SNP", e);
        }


        return restResponseResult;
    }

    public RestResponseResult exec(String url) {
        ResponseEntity<String> out;
        RestResponseResult result = new RestResponseResult();
        RestTemplate restTemplate = this.getRestTemplate();
        HttpEntity<Object> entity = this.getEntity();

        getLog().debug("Querying " + url);

        //and do I need this JSON media type for my use case?
        try {
            out = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            result.setStatus(out.getStatusCode().value());
            result.setUrl(url);
            JsonNode body = new JsonNode(out.getBody().toString());
            result.setRestResult(body);
            getLog().debug("Response: 200");
            //result.setValue(out.getBody());
        }
        catch(EnsemblRestClientException erce) {
            getLog().debug("EnsemblRestClientException");
            result = erce.getEnsemblLookup();
            result.setUrl(url);
        }
        catch (Exception e) {
            getLog().debug("Exception not managed");
        }

        return result;

    }

    public RestResponseResult fetch(String url)
            throws InterruptedException{
        ResponseEntity<String> out;
        Boolean ensemblDone = false;
        int maxTries = 0;


        RestResponseResult ensembl = new RestResponseResult();
        while ((!ensemblDone)  && (maxTries < 5 )){
            ensembl = this.exec(url);
            if (ensembl.getStatus() == 429) {
                maxTries = maxTries+1;
                Thread.sleep(ensembl.getWaitSeconds() * 1000);
            }
            else { ensemblDone = true;}
        }

        if (maxTries > 4) {
            getLog().debug("Failed to obtain a result from from '" + url + "' after after " + maxTries + " attempts");
            String extendedError = "Failed to obtain a result from from '" + url + "' after after " + maxTries + " attempts";
            ensembl.setError(extendedError);
        }

        return ensembl;
    }

    public Hashtable<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Hashtable<String, String> endpoints) {
        this.endpoints = endpoints;
    }

}
