package uk.ac.ebi.spot.goci.model;

import com.mashape.unirest.http.JsonNode;

/**
 * Created by emma on 20/01/2016.
 *
 * @author emma
 *         <p>
 *         Object to handle Ensembl REST API response which includes potential errors.
 */
public class RestResponseResult {

    private String error;

    private JsonNode restResult;

    public RestResponseResult() {
    }

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
}
