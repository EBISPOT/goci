package uk.ac.ebi.spot.goci.curation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 08/01/2016.
 *
 * @author emma
 *         <p>
 *         Model class used to process Ensembl ping response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ping {

    private Integer ping;

    public Ping() {

    }

    public Integer getPing() {
        return ping;
    }

    public void setPing(Integer ping) {
        this.ping = ping;
    }
}
