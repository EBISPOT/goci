package uk.ac.ebi.fgpt.goci.web.view;

import uk.ac.ebi.fgpt.goci.model.GociUser;

/**
 * A simple response bean that encapsulates information about a REST API key request operation.  This bean should be
 * returned in response to a request for a new REST API key, and contains the REST API key itself, the user this key is
 * for, and the number of REST API keys this user now holds.
 *
 * @author Tony Burdett
 * @date 01-Nov-2010
 */
public class RestApiKeyResponseBean {
    private GociUser gociUser;

    public RestApiKeyResponseBean(GociUser gociUser) {
        this.gociUser = gociUser;
    }

    /**
     * Gets the actual value of the REST API key requested
     *
     * @return the rest api key
     */
    public String getRestApiKey() {
        return gociUser.getRestApiKey();
    }

    /**
     * Gets the user this REST API key belongs to
     *
     * @return the user who holds the key contained in this bean
     */
    public GociUser getGociUser() {
        return gociUser;
    }
}
