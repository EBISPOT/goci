package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 21/01/2016.
 *
 * @author emma
 *         <p>
 *         Models response from http://rest.ensembl.org/info/assembly/homo_sapiens?content-type=application/json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblGenomeBuildVersionJson {
    private String assembly_name;

    public EnsemblGenomeBuildVersionJson() {
    }

    public String getAssembly_name() {
        return assembly_name;
    }

    public void setAssembly_name(String assembly_name) {
        this.assembly_name = assembly_name;
    }
}
