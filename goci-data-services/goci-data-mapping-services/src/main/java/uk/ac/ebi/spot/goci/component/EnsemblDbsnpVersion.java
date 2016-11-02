package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.EnsemblDbsnpVersionJson;

import javax.validation.constraints.NotNull;

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
public class EnsemblDbsnpVersion {

    @NotNull @Value("${mapping.dbsnp_endpoint}")
    private String endpoint;

    @NotNull @Value("${ensembl.server}")
    private String server;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Getter for the dbSNP version
     *
     * @return the dbSNP version
     */
    public int getDbsnpVersion() throws EnsemblRestIOException {

        RestTemplate restTemplate = new RestTemplate();
        String url = getServer() + getEndpoint();
        String version = "";

        try {
            getLog().info("Querying " + url);

            //Ensembl returns an array data structure for this call
            // Have to do some wrangling to get dbSNP version
            EnsemblDbsnpVersionJson[] response = restTemplate.getForObject(url, EnsemblDbsnpVersionJson[].class);
            version = response[0].getVersion();

            if (version.isEmpty()) {
                throw new EnsemblRestIOException("Unable to determine Ensembl dbSNP version");
            }
        }

        catch (Exception e) {
            throw new EnsemblRestIOException("Problem querying Ensembl API for dbSNP version");
        }

        return Integer.parseInt(version);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getServer() {
        return server;
    }

}
