package uk.ac.ebi.spot.goci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by emma on 21/01/2016.
 * @author emma
 *
 * Models response from http://rest.ensembl.org/info/assembly/homo_sapiens?content-type=application/json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblGenomeBuildVersionJson {
    private String assemblyName;

    public EnsemblGenomeBuildVersionJson() {
    }

    public String getAssemblyName() {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }
}
