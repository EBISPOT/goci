package uk.ac.ebi.spot.goci.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
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
    private String rest_parameters = "";

    private final int requestPerSecond = 15;
    private int requestCount = 0;
    private long limitStartTime = System.currentTimeMillis();

    private JsonNode rest_results;
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
        try {
            if (this.rest_parameters != "") {
                Matcher matcher = Pattern.compile("^\\?").matcher(this.rest_parameters);
                if (!matcher.matches()) {
                    this.rest_parameters = '?' + this.rest_parameters;
                }
            }
            url = new URL(server + this.rest_endpoint + this.rest_data + this.rest_parameters);
            //System.out.println(server + this.rest_endpoint + this.rest_data + this.rest_parameters+"\n");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Call REST API
        try {
            this.fetchJson(url.toString());
        }
        catch (UnirestException e) {
            e.printStackTrace();
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
        try {
            rateLimit();
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
                this.addErrors("No server is available to handle this request (Error 503: service unavailable)");
                getLog().error("No server is available to handle this request (Error 503: service unavailable)");
            }
            else if (response.getStatus() == 400) { // Bad request
                this.addErrors(url + " is generating an invalid request. (Error 400: bad request)");
                getLog().error(url + " is generating an invalid request. (Error 400: bad request)");
                throw new IllegalArgumentException(url + " is generating an invalid request. (Error 400: bad request)");
            }
            else { // Other issue
                this.addErrors("No data available");
                getLog().error("No data at " + url);
                throw new IllegalArgumentException("No data at " + url);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }
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
            if (diff < 1000) {
                Thread.sleep(1000 - diff);
            }
            //reset
            limitStartTime = System.currentTimeMillis();
            requestCount = 0;
        }
    }
}