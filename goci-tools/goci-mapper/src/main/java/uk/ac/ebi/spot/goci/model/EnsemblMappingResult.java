package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/01/2016.
 *
 * @author emma
 *         <p>
 *         DTO that holds results of Ensembl REST API call
 */


public class EnsemblMappingResult {

    private String rsId;

    private Integer merged;

    private Collection<Location> locations = new ArrayList<>();

    private Collection<GenomicContext> genomic_contexts = new ArrayList<>();

    private ArrayList<String> pipeline_errors = new ArrayList<>();

    private String functionalClass;


    public EnsemblMappingResult() {
    }


    public ArrayList<String> getPipeline_errors() {
        return pipeline_errors;
    }

    public void setPipeline_errors(ArrayList<String> pipeline_errors) {
        this.pipeline_errors = pipeline_errors;
    }

    public Collection<GenomicContext> getGenomic_contexts() {
        return genomic_contexts;
    }

    public void setGenomic_contexts(Collection<GenomicContext> genomic_contexts) {
        this.genomic_contexts = genomic_contexts;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    public void setLocations(Collection<Location> locations) {
        this.locations = locations;
    }

    public Integer getMerged() {
        return merged;
    }

    public void setMerged(Integer merged) {
        this.merged = merged;
    }

    public String getFunctionalClass() {
        return functionalClass;
    }

    public void setFunctionalClass(String functionalClass) {
        this.functionalClass = functionalClass;
    }

    public String getRsId() {
        return rsId;
    }

    public void setRsId(String rsId) {
        this.rsId = rsId;
    }

    public void addPipelineErrors(String error) {
        ArrayList<String> pipelineErrors = getPipeline_errors();
        pipelineErrors.add(error);
        setPipeline_errors(pipelineErrors);
    }

}
