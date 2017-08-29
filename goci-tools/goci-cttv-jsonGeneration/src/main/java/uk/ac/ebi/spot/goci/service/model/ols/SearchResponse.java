package uk.ac.ebi.spot.goci.service.model.ols;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by olgavrou on 29/08/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    @JsonProperty("numFound")
    private int numFound;

    @JsonProperty("docs")
    private SearchResult[] searchResults;

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public SearchResult[] getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(SearchResult[] searchResults) {
        this.searchResults = searchResults;
    }
}
