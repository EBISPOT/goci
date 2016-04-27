package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.SnpLookupJson;

import javax.validation.constraints.NotNull;

/**
 * Created by Emma on 22/04/16.
 *
 * @author emma
 *         <p>
 *         Checks a SNP identifier is valid using standard Spring mechanism to consume a RESTful service
 */
@Service
public class SnpValidationChecks {

    @NotNull @Value("${mapping.snp_lookup_endpoint}")
    private String endpoint;

    @NotNull @Value("${ensembl.server}")
    private String server;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Check gene name returns a response
     *
     * @param snp Snp identifier to check
     * @return Error message
     */
    public String checkSnpIdentifierIsValid(String snp) {

        String error = null;
        String url = getServer().concat(getEndpoint()).concat(snp);
        getLog().info("Checking url: " + url);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            String response = restTemplate.getForObject(url, String.class);
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            error = "SNP identifier ".concat(snp).concat(" is not valid");
            getLog().error("Checking SNP identifier failed", e);
        }
        return error;
    }

    public void getSnpLocations(String snp) {

        String error = null;
        String url = getServer().concat(getEndpoint()).concat(snp);
        getLog().info("Checking url: " + url);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        SnpLookupJson snpLookupJson =  new SnpLookupJson();
        try {
            snpLookupJson = restTemplate.getForObject(url, SnpLookupJson.class);
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            getLog().error("Checking SNP identifier failed", e);
        }


    }


    public String getEndpoint() {
        return endpoint;
    }

    public String getServer() {
        return server;
    }
}