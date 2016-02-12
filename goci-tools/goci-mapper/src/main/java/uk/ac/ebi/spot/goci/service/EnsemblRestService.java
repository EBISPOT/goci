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
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.RestResponseResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laurent on 15/07/15.
 *
 * @author Laurent
 *         <p>
 *         Class running the Ensembl REST API calls
 */
@Service
public class EnsemblRestService {

    @NotNull @Value("${ensembl.server}")
    private String server;

    @NotNull @Value("${mapping.requestPerSecond}")
    private Integer requestPerSecond;

    @NotNull @Value("${mapping.requestCount}")
    private Integer requestCount;

    @NotNull @Value("${mapping.maxSleepTime}")
    private Integer maxSleepTime;

    private long limitStartTime = System.currentTimeMillis();

    private Hashtable<String, String> endpoints = new Hashtable<String, String>();

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

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


    // Set the different Ensembl REST API endpoints used in the pipeline
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


    /**
     * Simple generic Ensembl REST API call method.
     *
     * @param endpoint_type   the endpoint name
     * @param data            the data/id/symbol we want to query
     * @param rest_parameters rest parameters
     * @return the corresponding result
     */
    public RestResponseResult getRestCall(String endpoint_type, String data, String rest_parameters)
            throws EnsemblRestIOException {

        String endpoint = getEndpoints().get(endpoint_type);
        URL url = null;
        RestResponseResult restResponseResult = new RestResponseResult();

        try {
            rateLimit();

            // Build URL
            if (!Objects.equals(rest_parameters, "")) {
                Matcher matcher = Pattern.compile("^\\?").matcher(rest_parameters);
                if (!matcher.matches()) {
                    rest_parameters = '?' + rest_parameters;
                }
            }

            url = new URL(getServer() + endpoint + data + rest_parameters);
            restResponseResult = fetchJson(url.toString());
        }

        catch (InterruptedException | MalformedURLException | UnirestException e) {
            getLog().error("Encountered a " + e.getClass().getSimpleName() +
                                   " whilst trying to run mapping of SNP", e);
            throw new EnsemblRestIOException("Encountered a " + e.getClass().getSimpleName() +
                                                     " whilst trying to run mapping of SNP", e);
        }


        return restResponseResult;
    }

    /**
     * Fetch response from API
     *
     * @param url URL to query
     * @return the corresponding result
     */
    private RestResponseResult fetchJson(String url)
            throws UnirestException, InterruptedException, EnsemblRestIOException {

        RestResponseResult restResponseResult = new RestResponseResult();

        // Parameters to monitor API call success
        int maxTries = 5;
        int tries = 0;
        int wait = 1;
        boolean success = false;

        while (!success && tries < maxTries) {

            tries++;

            try {
                getLog().trace("Querying URL: " + url);
                HttpResponse<JsonNode> response = Unirest.get(url)
                        .header("Content-Type", "application/json")
                        .asJson();
                String retryHeader = response.getHeaders().getFirst("Retry-After");
                getLog().trace("URL response: " + response.getStatus());


                if (response.getStatus() == 200) { // Success
                    success = true;
                    restResponseResult.setRestResult(response.getBody());
                }
                else if (response.getStatus() == 429 && retryHeader != null) { // Too Many Requests
                    Long waitSeconds = Long.valueOf(retryHeader);
                    Thread.sleep(waitSeconds * 1000);
                }
                else {

                    if (response.getStatus() == 503) { // Service unavailable
                        restResponseResult.setError(
                                "No server is available to handle this request (Error 503: service unavailable) at url: " +
                                        url);
                        getLog().error(
                                "No server is available to handle this request (Error 503: service unavailable) at url: " +
                                        url);
                        throw new EnsemblRestIOException(
                                "No server is available to handle this request (Error 503: service unavailable) at url: " +
                                        url);
                    }
                    else if (response.getStatus() == 400) { // Bad request (no result found)
                        JSONObject json_obj = response.getBody().getObject();
                        if (json_obj.has("error")) {
                            restResponseResult.setError(json_obj.getString("error"));
                        }
                        getLog().error(url + " is generating an invalid request. (Error 400: bad request)");

                        // Success is set to true here as the API call was successful
                        // but the gene or snp does not exist in Ensembl
                        success = true;
                    }

                    else { // Other issue
                        restResponseResult.setError("No data available at url " + url);
                        getLog().error("No data at " + url);
                        throw new EnsemblRestIOException("No data available at url " + url);
                    }
                }
            }
            catch (UnirestException e) {
                getLog().error(
                        "Caught exception from Ensembl Rest call, this call will be retried after " + wait + "s.", e);
                Thread.sleep(wait * 1000);
            }
        }

        if (success) {
            return restResponseResult;
        }
        else {
            getLog().error("Failed to obtain a result from from '" + url + "' after after " + maxTries + " attempts");
            throw new EnsemblRestIOException(
                    "Failed to obtain a result from '" + url + "' after " + maxTries + " attempts");
        }
    }


    /**
     * Check if the program reached the rate limit of calls per second
     *
     * @throws InterruptedException
     */
    private void rateLimit() throws InterruptedException {

        requestCount = getRequestCount();
        requestCount++;

        if (requestCount == getRequestPerSecond()) {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - getLimitStartTime();
            //if less than a second has passed then sleep for the remainder of the second
            if (diff < getMaxSleepTime()) {
                Thread.sleep(getMaxSleepTime() - diff);
            }

            //reset
            setLimitStartTime(System.currentTimeMillis());
            setRequestCount(0);
        }
    }

    public Hashtable<String, String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Hashtable<String, String> endpoints) {
        this.endpoints = endpoints;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getRequestPerSecond() {
        return requestPerSecond;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public long getLimitStartTime() {
        return limitStartTime;
    }

    public void setLimitStartTime(long limitStartTime) {
        this.limitStartTime = limitStartTime;
    }

    public int getMaxSleepTime() {
        return maxSleepTime;
    }

}