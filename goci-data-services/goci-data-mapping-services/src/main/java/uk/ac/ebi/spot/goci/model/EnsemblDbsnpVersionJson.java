package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 21/01/2016.
 *
 * @author emma
 *         <p>
 *         Models response from http://rest.ensembl.org/info/variation/homo_sapiens?content-type=application/json;filter=dbSNP
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblDbsnpVersionJson {

    private String version;

    public EnsemblDbsnpVersionJson() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
