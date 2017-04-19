package uk.ac.ebi.spot.goci.model;

import com.mashape.unirest.http.JsonNode;

/**
 * Created by emma/cinzia on 20/01/2016-2017
 *
 * @author emma
 *         <p>
 *         Object to handle Ensembl REST API response which includes potential errors.
 */

public class RestResponseResult {

    private String url;

    private String error;

    private JsonNode restResult;

    private long waitSeconds = 0;

    private int status;

    public RestResponseResult() {
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public JsonNode getRestResult() {
        return restResult;
    }

    public void setRestResult(JsonNode restResult) {
        this.restResult = restResult;
    }

    public long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean hasErorr() {
        return (this.getError() != null);
    }
}