package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.component.EnsemblMappingPipeline;
import uk.ac.ebi.spot.goci.exception.EnsemblMappingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EnsemblMappingResult;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.SecureUserRepository;
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
 *         Service that runs mapping pipeline over all or a selection of associations.
 */
@Service
public class MappingService {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;

    // Services
    private SnpLocationMappingService snpLocationMappingService;
    private SnpGenomicContextMappingService snpGenomicContextMappingService;
    private AssociationReportService associationReportService;
    private MappingRecordService mappingRecordService;
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    private EnsemblMappingPipeline ensemblMappingPipeline;
    private TrackingOperationService trackingOperationService;
    private SecureUserRepository secureUserRepository;


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
                          SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService,
                          EnsemblMappingPipeline ensemblMappingPipeline,
                          @Qualifier("associationTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                          SecureUserRepository secureUserRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.snpLocationMappingService = snpLocationMappingService;
        this.snpGenomicContextMappingService = snpGenomicContextMappingService;
        this.associationReportService = associationReportService;
        this.mappingRecordService = mappingRecordService;
        this.singleNucleotidePolymorphismQueryService = singleNucleotidePolymorphismQueryService;
        this.ensemblMappingPipeline = ensemblMappingPipeline;
        this.trackingOperationService = trackingOperationService;
        this.secureUserRepository = secureUserRepository;
    }


    /**
     * Perform validation and mapping of association
     *
     * @param association Association to map
     * @param performer   name of curator/job carrying out the mapping
     * @param user
     */
    @Transactional(rollbackFor = EnsemblMappingException.class)
    public void validateAndMapAssociation(Association association, String performer, SecureUser user)
            throws EnsemblMappingException {

        try {
            doMapping(association);

            // Update mapping event
            trackingOperationService.update(association, user, "ASSOCIATION_MAPPING");

            // Once mapping is complete, update mapping record
            getLog().debug("Update mapping record");
            mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);

        }
        catch (EnsemblMappingException e) {
            throw new EnsemblMappingException("Attempt to map supplied association failed", e);
        }
    }

    /**
     * Perform validation and mapping of all database associations
     *
     * @param associations Collection of associations to map
     * @param performer    name of curator/job carrying out the mapping
     */
    public void validateAndMapAllAssociations(Collection<Association> associations, String performer)
            throws EnsemblMappingException {

        // Default mapping user
        SecureUser user = secureUserRepository.findByEmail("automatic_mapping_process");

        try {
            for (Association association : associations) {
                doMapping(association);

                // Update mapping event
                trackingOperationService.update(association, user, "ASSOCIATION_MAPPING");

                // Once mapping is complete, update mapping record
                getLog().debug("Update mapping record");
                mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);
            }
        }
        catch (EnsemblMappingException e) {
            throw new EnsemblMappingException("Attempt to map all associations failed", e);
        }
    }

    private void doMapping(Association association) throws EnsemblMappingException {

        getLog().info("Mapping association: " + association.getId());

        // Map to store returned location data, this is used in
        // snpLocationMappingService to process all locations linked
        // to a single snp in one go
        Map<String, Set<Location>> snpToLocationsMap = new HashMap<>();

        // Collection to store all genomic contexts
        Collection<GenomicContext> allGenomicContexts = new ArrayList<>();

        // Collection to store all errors for one association
        Collection<String> associationPipelineErrors = new ArrayList<>();

        // For each loci get the SNP and author reported genes
        Collection<Locus> studyAssociationLoci = association.getLoci();
        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();

            Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus =
                    singleNucleotidePolymorphismQueryService.findByRiskAllelesLociId(locusId);

            Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

            // Get gene names
            Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
            for (Gene authorReportedGeneLinkedToSnp : authorReportedGenesLinkedToSnp) {
                authorReportedGeneNamesLinkedToSnp.add(authorReportedGeneLinkedToSnp.getGeneName().trim());
            }

            // Pass rs_id and author reported genes to mapping component
            for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {

                String snpRsId = snpLinkedToLocus.getRsId();
                EnsemblMappingResult ensemblMappingResult = new EnsemblMappingResult();

                // Try to map supplied data
                try {
                    getLog().debug("Running mapping....");
                    ensemblMappingResult =
                            ensemblMappingPipeline.run_pipeline(snpRsId, authorReportedGeneNamesLinkedToSnp);
                }
                catch (Exception e) {
                    getLog().error("Encountered a " + e.getClass().getSimpleName() +
                                           " whilst trying to run mapping of SNP " + snpRsId +
                                           ", found in association: " + association.getId(), e);
                    throw new EnsemblMappingException();
                }

                getLog().debug("Mapping complete");
                // First remove old locations and genomic contexts
                snpLocationMappingService.removeExistingSnpLocations(snpLinkedToLocus);
                snpGenomicContextMappingService.removeExistingGenomicContexts(snpLinkedToLocus);

                Collection<Location> locations = ensemblMappingResult.getLocations();
                Collection<GenomicContext> snpGenomicContexts = ensemblMappingResult.getGenomicContexts();
                ArrayList<String> pipelineErrors = ensemblMappingResult.getPipelineErrors();

                // Update functional class
                snpLinkedToLocus.setFunctionalClass(ensemblMappingResult.getFunctionalClass());
                snpLinkedToLocus.setLastUpdateDate(new Date());

                snpLinkedToLocus.setMerged(Long.valueOf(ensemblMappingResult.getMerged()));

                // Update the merge table
                if (ensemblMappingResult.getMerged() == 1) {
                    String currentSnpId = ensemblMappingResult.getCurrentSnpId();
                    SingleNucleotidePolymorphism currentSnp =
                            singleNucleotidePolymorphismRepository.findByRsId(currentSnpId);
                    // Create a new entry in the SingleNucleotidePolymorphism SQL table for the current rsID
                    // Add the current SingleNucleotidePolymorphism to the "merged" rsID
                    if (currentSnp == null) {
                        currentSnp = new SingleNucleotidePolymorphism();
                        currentSnp.setRsId(currentSnpId);
                        currentSnp.setFunctionalClass(snpLinkedToLocus.getFunctionalClass());
                        singleNucleotidePolymorphismRepository.save(currentSnp);
                        currentSnp = singleNucleotidePolymorphismRepository.findByRsId(currentSnpId);
                    }
                    snpLinkedToLocus.setCurrentSnp(currentSnp);
                }
                singleNucleotidePolymorphismRepository.save(snpLinkedToLocus);

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
                    pipelineErrors.add("Attempt to map SNP: " + snpRsId + " returned no location details");
                }

                // Store genomic context data for snp
                if (!snpGenomicContexts.isEmpty()) {
                    allGenomicContexts.addAll(snpGenomicContexts);
                }
                else {
                    getLog().warn("Attempt to map SNP: " + snpRsId + " returned no mapped genes");
                    pipelineErrors.add("Attempt to map SNP: " + snpRsId + " returned no mapped genes");
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

        // Save data
        if (!snpToLocationsMap.isEmpty()) {
            getLog().debug("Updating location details ...");
            snpLocationMappingService.storeSnpLocation(snpToLocationsMap);
            getLog().debug("Updating location details complete");
        }
        if (!allGenomicContexts.isEmpty()) {
            getLog().debug("Updating genomic context details ...");
            snpGenomicContextMappingService.processGenomicContext(allGenomicContexts);
            getLog().debug("Updating genomic context details complete");
        }
    }
}
