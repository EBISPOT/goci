package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.component.EnsemblMappingPipeline;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 13/08/2015.
 *
 * @author emma
 *         <p>
 *         Service that runs mapping pipeline over all associations in database.
 */
@Service
public class MappingService {

    // Repositories
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    // Services
    private SnpLocationMappingService snpLocationMappingService;
    private SnpGenomicContextMappingService snpGenomicContextMappingService;
    private AssociationReportService associationReportService;
    private MappingRecordService mappingRecordService;
    private AssociationService associationService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public MappingService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                          SnpLocationMappingService snpLocationMappingService,
                          SnpGenomicContextMappingService snpGenomicContextMappingService,
                          AssociationReportService associationReportService,
                          MappingRecordService mappingRecordService,
                          AssociationService associationService) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.snpLocationMappingService = snpLocationMappingService;
        this.snpGenomicContextMappingService = snpGenomicContextMappingService;
        this.associationReportService = associationReportService;
        this.mappingRecordService = mappingRecordService;
        this.associationService = associationService;
    }
    
    /**
     * Get all associations in database
     */
    public void mapCatalogContents(String performer) {

        // Get all associations via service
        Collection<Association> associations = associationService.findAllAssociations();
        getLog().info("Total number of associations to map: " + associations.size());
        validateAndMapSnps(associations, performer);
    }

    /**
     * Perform validation and mapping of association
     *
     * @param associations Collection of associations to map
     */
    public void validateAndMapSnps(Collection<Association> associations, String performer) {

        // Map to store returned location data, this is used as
        // snpLocationMappingService process all locations linked
        // to a single snp in one go
        Map<String, Set<Location>> snpToLocationsMap = new HashMap<>();

        // Collection to store all genomic contexts
        Collection<GenomicContext> allGenomicContexts = new ArrayList<>();

        // For each association get the loci
        for (Association association : associations) {

            getLog().debug("Mapping association: " + association.getId());

            // Collection to store all errors for one association
            Collection<String> associationPipelineErrors = new ArrayList<>();

            Collection<Locus> studyAssociationLoci = association.getLoci();

            // For each loci get the SNP and author reported genes
            for (Locus associationLocus : studyAssociationLoci) {
                Long locusId = associationLocus.getId();

                Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus =
                        singleNucleotidePolymorphismRepository.findByRiskAllelesLociId(locusId);

                Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

                // Get gene names
                Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
                for (Gene authorReportedGeneLinkedToSnp : authorReportedGenesLinkedToSnp) {
                    authorReportedGeneNamesLinkedToSnp.add(authorReportedGeneLinkedToSnp.getGeneName());
                }

                // Pass rs_id and author reported genes to mapping component
                for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {
                    String snpRsId = snpLinkedToLocus.getRsId();
                    EnsemblMappingPipeline ensemblMappingPipeline =
                            new EnsemblMappingPipeline(snpRsId, authorReportedGeneNamesLinkedToSnp);
                    ensemblMappingPipeline.run_pipeline();

                    Collection<Location> locations = ensemblMappingPipeline.getLocations();
                    Collection<GenomicContext> snpGenomicContexts = ensemblMappingPipeline.getGenomicContexts();
                    ArrayList<String> pipelineErrors = ensemblMappingPipeline.getPipelineErrors();

                    // Store location information for SNP
                    if (!locations.isEmpty()) {
                        for (Location location : locations) {

                            // Next time we see SNP, add location to set
                            // This would only occur is SNP has multiple locations
                            if (snpToLocationsMap.containsKey(snpRsId)) {
                                snpToLocationsMap.get(snpRsId).add(location);
                            }

                            // First time we see a SNP store the location
                            else {
                                Set<Location> snpLocation = new HashSet<>();
                                snpLocation.add(location);
                                snpToLocationsMap.put(snpRsId, snpLocation);
                            }
                        }
                    }
                    else {
                        getLog().warn("Attempt to map SNP: " + snpRsId + " returned no location details");
                    }

                    // Store genomic context data for snp
                    if (!snpGenomicContexts.isEmpty()) {
                        allGenomicContexts.addAll(snpGenomicContexts);
                    }
                    else {
                        getLog().warn("Attempt to map SNP: " + snpRsId + " returned no mapped genes");
                    }

                    if (!pipelineErrors.isEmpty()) {
                        associationPipelineErrors.addAll(pipelineErrors);
                    }
                }
            }

            // Create association report based on whether there is errors or not
            if (!associationPipelineErrors.isEmpty()) {
                associationReportService.processAssociationErrors(association, associationPipelineErrors);
            }
            else {
                associationReportService.updateAssociationReportDetails(association);
            }
        }

        // Save data
        if (!snpToLocationsMap.isEmpty()) {
            getLog().info("Adding/updating location details for SNPs" + snpToLocationsMap.keySet().toString());
            snpLocationMappingService.storeSnpLocation(snpToLocationsMap);
        }
        if (!allGenomicContexts.isEmpty()) {
            getLog().info(
                    "Adding/updating genomic context for SNPs" + snpToLocationsMap.keySet().toString());
            snpGenomicContextMappingService.processGenomicContext(allGenomicContexts);
        }

        // Once mapping is complete, update mapping record
        for (Association association : associations) {

            // Get current curator of study
            Study associationStudy = association.getStudy();
            mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);
        }

    }

}



