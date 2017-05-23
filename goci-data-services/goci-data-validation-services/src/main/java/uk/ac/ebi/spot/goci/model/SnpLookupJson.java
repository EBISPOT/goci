package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by emma on 22/04/2016.
 *
 * @author emma
 *         <p>
 *         Model to process response from: http://rest.ensembl.org/variation/human/rs7329174?content-type=application/json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnpLookupJson {

    private List<SnpMappingsJson> mappings;

    public List<SnpMappingsJson> getMappings() {
        return mappings;
    }
}
