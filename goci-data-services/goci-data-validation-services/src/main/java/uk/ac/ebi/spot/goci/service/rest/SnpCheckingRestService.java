package uk.ac.ebi.spot.goci.service.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.SnpLookupJson;
import uk.ac.ebi.spot.goci.utils.RestUrlBuilder;

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
public class SnpCheckingRestService {

    @NotNull @Value("${mapping.snp_lookup_endpoint}")
    private String endpoint;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    private RestUrlBuilder restUrlBuilder;

    @Autowired
    public SnpCheckingRestService(RestUrlBuilder restUrlBuilder) {
        this.restUrlBuilder = restUrlBuilder;
    }

    /**
     * Check gene name returns a response
     *
     * @param snp Snp identifier to check
     * @return Error message
     */
    public String checkSnpIdentifierIsValid(String snp) {

        String error = null;
        String response = null;
        try {
            String url = restUrlBuilder.createUrl(getEndpoint(), snp);
            getLog().info("Querying: " + url);
            response = restUrlBuilder.getRestTemplate()
                    .getForObject(url, String.class);

            if (response.contains("error")) {
                error = "SNP identifier ".concat(snp).concat(" is not valid");
            }
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            getLog().error("Checking SNP identifier failed", e);
        }

        return error;
    }

    /**
     * Get the chromosome a SNP resides on
     *
     * @param snp Snp identifier to check
     * @return Set of all SNP chromosome names
     */
    public Set<String> getSnpLocations(String snp) {

        Set<String> snpChromosomeNames = new HashSet<>();
        SnpLookupJson snpLookupJson = new SnpLookupJson();
        try {
            String url = restUrlBuilder.createUrl(getEndpoint(), snp);
            getLog().info("Querying: " + url);
            snpLookupJson =
                    restUrlBuilder.getRestTemplate()
                            .getForObject(url, SnpLookupJson.class);
            snpLookupJson.getMappings().forEach(snpMappingsJson -> {
                snpChromosomeNames.add(snpMappingsJson.getSeq_region_name());
            });
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            getLog().error("Getting locations for SNP ".concat(snp).concat(" failed"), e);
        }
        return snpChromosomeNames;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}