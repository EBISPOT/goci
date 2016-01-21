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

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private ArrayList<String> pipelineErrors = new ArrayList<>();

    private String functionalClass;


    public EnsemblMappingResult() {
    }


    public ArrayList<String> getPipelineErrors() {
        return pipelineErrors;
    }

    public void setPipelineErrors(ArrayList<String> pipelineErrors) {
        this.pipelineErrors = pipelineErrors;
    }

    public Collection<GenomicContext> getGenomicContexts() {
        return genomicContexts;
    }

    public void setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        this.genomicContexts = genomicContexts;
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
        ArrayList<String> pipelineErrors = getPipelineErrors();
        pipelineErrors.add(error);
        setPipelineErrors(pipelineErrors);
    }

    public void addGenomicContext(GenomicContext genomicContext) {
        Collection<GenomicContext> genomicContexts = getGenomicContexts();
        genomicContexts.add(genomicContext);
        setGenomicContexts(genomicContexts);
    }

}
