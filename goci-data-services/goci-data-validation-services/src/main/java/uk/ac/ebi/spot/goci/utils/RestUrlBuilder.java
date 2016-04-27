package uk.ac.ebi.spot.goci.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Created by emma on 27/04/2016.
 *
 * @author emma
 *         <p>
 *         Simple util class to build a url
 */
@Service
public class RestUrlBuilder {

    @NotNull @Value("${ensembl.server}")
    private String server;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public String createUrl(String endpoint, String query) {
        String url = getServer().concat(endpoint).concat(query);
        getLog().info("Checking url: " + url);
        return url;
    }

    public String getServer() {
        return server;
    }
}
