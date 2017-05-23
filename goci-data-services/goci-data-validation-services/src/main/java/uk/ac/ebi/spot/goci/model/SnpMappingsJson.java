package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 27/04/2016.
 *
 * @author emma
 *         <p>
 *         Model to hold mapping information returned from: http://rest.ensembl.org/variation/human/rs7329174?content-type=application/json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnpMappingsJson {

    private String seq_region_name;

    public String getSeq_region_name() {
        return seq_region_name;
    }
}
