package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/01/2016.
 *
 * @author emma
 *         <p>
 *         DTO that holds results of Ensembl REST API call and other values need by the mapping code.
 */

public class EnsemblMappingResult {

    private String rsId;

    private Integer merged;

    private Collection<Location> locations = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private ArrayList<String> pipelineErrors = new ArrayList<>();

    private String functionalClass;

    private ArrayList<String> overlappingGenes = new ArrayList<>();


    public EnsemblMappingResult() {
    }

    /**
     * Add error to list of pipeline errors
     *
     * @param error
     */
    public void addPipelineErrors(String error) {
        ArrayList<String> pipelineErrors = getPipelineErrors();
        pipelineErrors.add(error);
        setPipelineErrors(pipelineErrors);
    }

    /**
     * Add genomic context to list of existing genomic contexts
     *
     * @param genomicContext
     */
    public void addGenomicContext(GenomicContext genomicContext) {
        Collection<GenomicContext> genomicContexts = getGenomicContexts();
        genomicContexts.add(genomicContext);
        setGenomicContexts(genomicContexts);
    }

    /**
     * Add gene to list of overlapping genes.
     *
     * @param gene
     */
    public void addOverlappingGene(String gene) {
        ArrayList<String> overlappingGenes = getOverlappingGenes();
        overlappingGenes.add(gene);
        setOverlappingGenes(overlappingGenes);
    }


    // Getters/setters
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

    public ArrayList<String> getOverlappingGenes() {
        return overlappingGenes;
    }

    public void setOverlappingGenes(ArrayList<String> overlappingGenes) {
        this.overlappingGenes = overlappingGenes;
    }

}
