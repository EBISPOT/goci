package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.GeneLookupJson;

/**
 * Created by Laurent on 28/09/15.
 *
 * @author Laurent
 *         <p>
 *         Class getting the dbSNP version from the Ensembl REST API
 *         <p>
 *         Refactored by Emma to use standard Spring mechanism to consume a RESTful service
 */
@Service
public class GeneValidationChecks {

/*    @NotNull @Value("${mapping.gene_lookup_endpoint}")
    private String endpoint;

    @NotNull @Value("${ensembl.server}")
    private String server;*/

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Check gene name returns a response
     *
     * @return the dbSNP version
     */
    public String checkGeneSymbolIsValid(String gene) {

        String error = null;

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://rest.ensembl.org/lookup/symbol/homo_sapiens/SFRP1?content-type=application/json";
        GeneLookupJson geneLookupJson = new GeneLookupJson();

        try {
            geneLookupJson = restTemplate.getForObject(url, GeneLookupJson.class);
            String objectType = geneLookupJson.getObject_type();
            getLog().info("Checking gene: " + url);

            if (!objectType.equalsIgnoreCase("gene")) {
                error = "Gene symbol not valid";
            }
        }

        catch (Exception e) {
            getLog().error("Checking gene symbol failed", e);
        }

        return error;
    }

//    public String getEndpoint() {
//        return endpoint;
//    }
//
//    public String getServer() {
//        return server;
//    }

}
