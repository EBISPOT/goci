package uk.ac.ebi.spot.goci.service.model.ols;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by olgavrou on 29/08/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQuery {

    @JsonProperty("response")
    private SearchResponse response;

    public SearchResponse getResponse() {
        return response;
    }

    public void setResponse(SearchResponse response) {
        this.response = response;
    }
}
