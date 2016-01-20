package uk.ac.ebi.spot.goci.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpHost;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 *
 * @author Laurent Class running the Ensembl REST API calls
 */
@Service
public class EnsemblRestService {

    @NotNull @Value("${ensembl.server}")
    private String server;

    // Request rate variables
    private final int requestPerSecond = 15;
    private int requestCount = 0;
    private long limitStartTime = System.currentTimeMillis();
    private final int maxSleepTime = 1000;

    private Hashtable<String, String> endpoints = new Hashtable<String, String>();


    //    private String rest_endpoint;
//    private String rest_data;
//    private String rest_parameters = "";
//
//    private JsonNode rest_results = new JsonNode(""); // Default empty result;
//    private ArrayList<String> rest_errors = new ArrayList<String>();

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Default constructor

//    public EnsemblRestService() {
//    }

    // Set the different Ensembl REST API endpoints used in the pipeline
    @Autowired
    public void setEndpoints() {
        String species = "homo_sapiens";
        this.endpoints.put("variation", "/variation/" + species + "/");
        this.endpoints.put("lookup_symbol", "/lookup/symbol/" + species + "/");
        this.endpoints.put("overlap_region", "/overlap/region/" + species + "/");
        this.endpoints.put("info_assembly", "/info/assembly/" + species + "/");
        this.endpoints.put("info_variation", "/info/variation/" + species + "/");
        this.endpoints.put("info_data", "/info/data/");
    }

//    /**
//     * Simple constructor with endpoint and data
//     *
//     * @param rest_endpoint the endpoint part of the URL
//     * @param rest_data     the data/id/symbol we want to query
//     */
//    public EnsemblRestService(String rest_endpoint, String rest_data) {
//        this.rest_endpoint = rest_endpoint;
//        this.rest_data = rest_data;
//    }
//
//
//    /**
//     * More complex contructor with extra parameters
//     *
//     * @param rest_endpoint   the endpoint part of the URL
//     * @param rest_data       the data/id/symbol we want to query
//     * @param rest_parameters the extra parameters to add at the end of the REST call url
//     */
//    public EnsemblRestService(String rest_endpoint, String rest_data, String rest_parameters) {
//        this.rest_endpoint = rest_endpoint;
//        this.rest_data = rest_data;
//        this.rest_parameters = rest_parameters;
//    }

    @PostConstruct
    public void init() {
                // Set proxy
        String host = System.getProperty("http.proxyHost");
         String port = System.getProperty("http.proxyPort");
        Integer portNum = 0;

                    // Get port number
        if (port != null) {
            portNum = Integer.valueOf(port);
        }

        if (host != null && port != null) {
            Unirest.setProxy(new HttpHost(host, portNum));
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            Unirest.shutdown();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to shutdown Unirest HTTP connection service", e);
        }
    }

    /**
     * Run the Ensembl REST API call, using the parameters from the constructor
     *
     * @throws IOException
     * @throws UnirestException
     * @throws InterruptedException
     */
    public void getRestCall(String rest_endpoint, String rest_data, String rest_parameters) throws IOException, UnirestException, InterruptedException {

        // Build URL
        URL url = null;

        if (rest_parameters != "") {
            Matcher matcher = Pattern.compile("^\\?").matcher(rest_parameters);
            if (!matcher.matches()) {
                rest_parameters = '?' + rest_parameters;
            }
        }
        url = new URL(server + rest_endpoint + rest_data + rest_parameters);

        // Call REST API
        if (url != null) {
            fetchJson(url.toString());
        }
    }


    /**
     * Return the results of the Ensembl REST API call
     *
     * @return JSONObject containing the returned JSON data
     */

//    public JsonNode getRestResults() {
//        return this.rest_results;
//    }


    /**
     * Return the list of error messages from the Ensembl REST API call
     *
     * @return List of error messages
     */
//    public ArrayList<String> getErrors() {
//        return this.rest_errors;
//    }


    /**
     * Add error messages to the array of REST error messages
     *
     * @param error_msg the error message
     */
//    private void addErrors(String error_msg) {
//        this.rest_errors.add(error_msg);
//    }


    private void fetchJson(String url) throws UnirestException, InterruptedException {

        HttpResponse<JsonNode> response = Unirest.get(url)
                .header("Content-Type", "application/json")
                .asJson();
        String retryHeader = response.getHeaders().getFirst("Retry-After");

        if (response.getStatus() == 200) { // Success
            this.rest_results = response.getBody();
        }
        else if (response.getStatus() == 429 && retryHeader != null) { // Too Many Requests
            Long waitSeconds = Long.valueOf(retryHeader);
            Thread.sleep(waitSeconds * 1000);
            fetchJson(url);
        }
        else if (response.getStatus() == 503) { // Service unavailable
            this.addErrors(
                    "No server is available to handle this request (Error 503: service unavailable) at url: " + url);
            getLog().error(
                    "No server is available to handle this request (Error 503: service unavailable) at url: " + url);
        }
        else if (response.getStatus() == 400) { // Bad request (no result found)
            JSONObject json_obj = response.getBody().getObject();
            if (json_obj.has("error")) {
                this.addErrors(json_obj.getString("error"));
            }
            getLog().error(url + " is generating an invalid request. (Error 400: bad request)");
        }
        else { // Other issue
            this.addErrors("No data available at url " + url);
            getLog().error("No data at " + url);
        }
    }

    /**
     * Simple generic Ensembl REST API call method.
     *
     * @param endpoint_type the endpoint name
     * @param data          the data/id/symbol we want to query
     * @return the corresponding JSONObject
     */
    private JSONObject getSimpleRestCall(String endpoint_type, String data) throws EnsemblMappingException {
        String endpoint = this.getEndpoint(endpoint_type);
//        EnsemblRestService ens_rest_call = new EnsemblRestService(endpoint, data);
        JSONObject json_result = new JSONObject();
        try {
            rateLimit();
            ens_rest_call.getRestCall();
            json_result = ens_rest_call.getRestResults().getObject();

            // Errors
            ArrayList rest_errors = ens_rest_call.getErrors();
            if (rest_errors.size() > 0) {
                for (int i = 0; i < rest_errors.size(); ++i) {
                    this.pipeline_errors.add(rest_errors.get(i).toString());
                }
            }
        }
        catch (IOException | InterruptedException | UnirestException e) {
            getLog().error("Encountered a " + e.getClass().getSimpleName() +
                                   " whilst trying to run mapping of SNP", e);
            throw new EnsemblMappingException();
        }
        return json_result;
    }

    /**
     * Return the Ensembl REST API endpoint URL corresponding the the endpoint name provided
     *
     * @param endpoint_name the name of the REST API endpoint
     * @return the URL part specific to the queried endpoint
     */
    private String getEndpoint(String endpoint_name) {
        return this.endpoints.get(endpoint_name);
    }

    /**
     * Check if the program reached the rate limit of calls per second
     *
     * @throws InterruptedException
     */
    private void rateLimit() throws InterruptedException {
        requestCount++;
        if (requestCount == requestPerSecond) {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - limitStartTime;
            //if less than a second has passed then sleep for the remainder of the second
            if (diff < maxSleepTime) {
                Thread.sleep(maxSleepTime - diff);
            }
            //reset
            limitStartTime = System.currentTimeMillis();
            requestCount = 0;
        }
    }

}