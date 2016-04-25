package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.GeneLookupJson;

import javax.validation.constraints.NotNull;

/**
 * Created by Emma on 22/04/16.
 *
 * @author emma
 *         <p>
 *         Checks a gene symbol is valid using standard Spring mechanism to consume a RESTful service
 */
@Service
public class GeneValidationChecks {

    @NotNull @Value("${mapping.gene_lookup_endpoint}")
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
     * @param gene Gene name to check
     * @return Error message
     */
    public String checkGeneSymbolIsValid(String gene) {

        String error = null;
        String url = getServer().concat(getEndpoint()).concat(gene);
        getLog().info("Checking gene: " + url);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        GeneLookupJson geneLookupJson = new GeneLookupJson();

        try {
            geneLookupJson = restTemplate.getForObject(url, GeneLookupJson.class);

            if (!geneLookupJson.getObject_type().equalsIgnoreCase("gene")) {
                error = "Gene symbol is not valid";
            }
        }
        // The query returns a 400 error if response returns an error
        catch (Exception e) {
            error = "Gene symbol is not valid";
            getLog().error("Checking gene symbol failed", e);
        }
        return error;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getServer() {
        return server;
    }
}