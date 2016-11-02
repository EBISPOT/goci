package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.EnsemblReleaseJson;

import javax.validation.constraints.NotNull;

/**
 * Created by Laurent on 22/09/15.
 *
 * @author Laurent
 *         <p>
 *         Class getting the Ensembl Release version from the Ensembl REST API
 *         <p>
 *         Refactored by Emma to use standard Spring mechanism to consume a RESTful service
 */
@Service
public class EnsemblRelease {

    @NotNull @Value("${mapping.release_endpoint}")
    private String endpoint;

    @NotNull @Value("${ensembl.server}")
    private String server;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Getter for the release version
     *
     * @return the numeric release version
     */
    public int getReleaseVersion() throws EnsemblRestIOException {

        RestTemplate restTemplate = new RestTemplate();
        String url = getServer() + getEndpoint();
        int currentEnsemblRelease = 0;

        try {
            EnsemblReleaseJson ensemblReleaseJson = restTemplate.getForObject(url, EnsemblReleaseJson.class);
            getLog().info("Querying " + url);
            int[] releases = ensemblReleaseJson.getReleases();

            if (releases != null) {
                if (releases.length == 1) {
                    currentEnsemblRelease = releases[0];
                }
                else {
                    throw new EnsemblRestIOException("Unable to determine Ensembl release");
                }
            }
            else {
                throw new EnsemblRestIOException("No Ensembl release information returned from API");
            }
        }

        catch (Exception e) {
            throw new EnsemblRestIOException("Problem querying Ensembl API for release");
        }

        return currentEnsemblRelease;
    }

    public String getServer() {
        return server;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
