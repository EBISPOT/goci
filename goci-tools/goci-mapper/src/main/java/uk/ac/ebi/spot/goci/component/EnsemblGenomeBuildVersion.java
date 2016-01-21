package uk.ac.ebi.spot.goci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.exception.EnsemblRestIOException;
import uk.ac.ebi.spot.goci.model.EnsemblGenomeBuildVersionJson;

import javax.validation.constraints.NotNull;

/**
 * Created by Laurent on 10/11/2015.
 *
 * @author Laurent
 *         <p>
 *         Class getting the Ensembl Genome build version from the Ensembl REST API
 *
 *         Refactored by Emma to use standard Spring mechanism to consume a RESTful service
 */
@Service
public class EnsemblGenomeBuildVersion {

    @NotNull @Value("${mapping.genome_build_endpoint}")
    private String endpoint;

    @NotNull @Value("${ensembl.server}")
    private String server;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Getter for the genome build version
     *
     * @return the genome build version
     */
    public String getGenomeBuildVersion() throws EnsemblRestIOException {

        RestTemplate restTemplate = new RestTemplate();
        String url = getServer() + getEndpoint();
        EnsemblGenomeBuildVersionJson ensemblGenomeBuildVersionJson = new EnsemblGenomeBuildVersionJson();
        String assemblyName = "";

        try {
            ensemblGenomeBuildVersionJson = restTemplate.getForObject(url, EnsemblGenomeBuildVersionJson.class);
            getLog().info("Querying " + url);
            assemblyName = ensemblGenomeBuildVersionJson.getAssemblyName();

            if (assemblyName.isEmpty()) {
                throw new EnsemblRestIOException("Unable to determine Ensembl genome build version");
            }
        }

        catch (Exception e) {
            throw new EnsemblRestIOException("Problem querying Ensembl API for genome build version");
        }

        return assemblyName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
