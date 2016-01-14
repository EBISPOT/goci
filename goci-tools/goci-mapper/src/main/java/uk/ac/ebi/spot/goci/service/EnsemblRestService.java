package uk.ac.ebi.spot.goci.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 *
 * @author Laurent Class running the Ensembl REST API calls
 */
@Service
public class EnsemblRestService {

    private static String server = "http://rest.ensembl.org";

    private String rest_endpoint;
    private String rest_data;
    private String rest_parameters = "content-type=application/json";

    private JsonNode rest_results = new JsonNode(""); // Default empty result;
    private ArrayList<String> rest_errors = new ArrayList<String>();

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Default constructor
    public EnsemblRestService() {
    }


    /**
     * Simple constructor with endpoint and data
     *
     * @param rest_endpoint the endpoint part of the URL
     * @param rest_data     the data/id/symbol we want to query
     */
    public EnsemblRestService(String rest_endpoint, String rest_data) {
        this.rest_endpoint = rest_endpoint;
        this.rest_data = rest_data;
    }


    /**
     * More complex contructor with extra parameters
     *
     * @param rest_endpoint   the endpoint part of the URL
     * @param rest_data       the data/id/symbol we want to query
     * @param rest_parameters the extra parameters to add at the end of the REST call url
     */
    public EnsemblRestService(String rest_endpoint, String rest_data, String rest_parameters) {
        this.rest_endpoint = rest_endpoint;
        this.rest_data = rest_data;
        this.rest_parameters = rest_parameters;
    }


    /**
     * Run the Ensembl REST API call, using the parameters from the constructor
     *
     * @throws IOException
     * @throws UnirestException
     * @throws InterruptedException
     */
    public void getRestCall() throws IOException, UnirestException, InterruptedException {

        // Build URL
        URL url = null;

        if (this.rest_parameters != "") {
            Matcher matcher = Pattern.compile("^\\?").matcher(this.rest_parameters);
            if (!matcher.matches()) {
                this.rest_parameters = '?' + this.rest_parameters;
            }
        }
        url = new URL(server + this.rest_endpoint + this.rest_data + this.rest_parameters);
        getLog().info("Querying: " + url);

        // Call REST API
        if (url != null) {
            this.fetchJson(url.toString());
        }
    }


    /**
     * Return the results of the Ensembl REST API call
     *
     * @return JSONObject containing the returned JSON data
     */

    public JsonNode getRestResults() {
        return this.rest_results;
    }


    /**
     * Return the list of error messages from the Ensembl REST API call
     *
     * @return List of error messages
     */
    public ArrayList<String> getErrors() {
        return this.rest_errors;
    }


    /**
     * Add error messages to the array of REST error messages
     *
     * @param error_msg the error message
     */
    private void addErrors(String error_msg) {
        this.rest_errors.add(error_msg);
    }


    private void fetchJson(String url) throws UnirestException, InterruptedException {

        HttpResponse<JsonNode> response = Unirest.get(url)
                .header("Content-Type", "application/json")
                .asJson();
        String retryHeader = response.getHeaders().getFirst("Retry-After");

        getLog().info("Response: " + response.getStatus());
        if (response.getStatus() == 200) { // Success
            this.rest_results = response.getBody();
        }
        else if (response.getStatus() == 429 && retryHeader != null) { // Too Many Requests
            Long waitSeconds = Long.valueOf(retryHeader);
            Thread.sleep(waitSeconds * 1000);
            fetchJson(url);
        }
        else if (response.getStatus() == 503) { // Service unavailable
            this.addErrors("No server is available to handle this request (Error 503: service unavailable)");
            getLog().error("No server is available to handle this request (Error 503: service unavailable)");
        }
        else if (response.getStatus() == 400) { // Bad request (no result found)
            JSONObject json_obj = response.getBody().getObject();
            if (json_obj.has("error")) {
                this.addErrors(json_obj.getString("error"));
            }
            getLog().error(url + " is generating an invalid request. (Error 400: bad request)");
        }
        else { // Other issue
            this.addErrors("No data available");
            getLog().error("No data at " + url);
        }
    }
}