package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.SnpLookupJson;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

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

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            String response = restTemplate.getForObject(createUrl(snp), String.class);
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            error = "SNP identifier ".concat(snp).concat(" is not valid");
            getLog().error("Checking SNP identifier failed", e);
        }
        return error;
    }

    /**
     * Get the chromosome a SNP resides on
     *
     * @param snp Snp identifier to check
     * @return Error message
     */
    public Set<String> getSnpLocations(String snp) {

        Set<String> snpChromosomeNames = new HashSet<>();

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        SnpLookupJson snpLookupJson = new SnpLookupJson();
        try {
            snpLookupJson = restTemplate.getForObject(createUrl(createUrl(snp)), SnpLookupJson.class);
            snpLookupJson.getMappings().forEach(snpMappingsJson -> {
                snpChromosomeNames.add(snpMappingsJson.getSeq_region_name());
            });
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            getLog().error("Getting locations for SNP ".concat(snp).concat("failed"), e);
        }

        return snpChromosomeNames;
    }

    private String createUrl(String query) {
        String url = getServer().concat(getEndpoint()).concat(query);
        getLog().info("Checking url: " + url);
        return url;
    }


    public String getEndpoint() {
        return endpoint;
    }

    public String getServer() {
        return server;
    }
}