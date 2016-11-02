package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.RegionRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by emma on 07/07/2015.
 * <p>
 * Service that processes data returned from mapping pipeline. This specifically focuses on location information and
 * saves it to the database.
 */
@Service
public class SnpLocationMappingService {

    // Repositories
    private LocationRepository locationRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GenomicContextRepository genomicContextRepository;

    // Services
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    private LocationCreationService locationCreationService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    //Constructor
    @Autowired
    public SnpLocationMappingService(LocationRepository locationRepository,
                                     SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                     GenomicContextRepository genomicContextRepository,
                                     SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService,
                                     LocationCreationService locationCreationService) {
        this.locationRepository = locationRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.singleNucleotidePolymorphismQueryService = singleNucleotidePolymorphismQueryService;
        this.locationCreationService = locationCreationService;
    }

    public void storeSnpLocation(Map<String, Set<Location>> snpToLocations) {

        // Go through each rs_id and its associated locations returned from the mapping pipeline
        for (String snpRsId : snpToLocations.keySet()) {

            Set<Location> snpLocationsFromMapping = snpToLocations.get(snpRsId);

            // Check if the SNP exists
            SingleNucleotidePolymorphism snpInDatabase =
                    singleNucleotidePolymorphismQueryService.findByRsIdIgnoreCase(snpRsId);

            if (snpInDatabase != null) {

                // Store all new location objects
                Collection<Location> newSnpLocations = new ArrayList<>();

                for (Location snpLocationFromMapping : snpLocationsFromMapping) {

                    String chromosomeNameFromMapping = snpLocationFromMapping.getChromosomeName();
                    if (chromosomeNameFromMapping != null) {
                        chromosomeNameFromMapping = chromosomeNameFromMapping.trim();
                    }

                    String chromosomePositionFromMapping = snpLocationFromMapping.getChromosomePosition();
                    if (chromosomePositionFromMapping != null) {
                        chromosomePositionFromMapping = chromosomePositionFromMapping.trim();
                    }

                    Region regionFromMapping = snpLocationFromMapping.getRegion();
                    String regionNameFromMapping = null;
                    if (regionFromMapping != null) {
                        if (regionFromMapping.getName() != null) {
                            regionNameFromMapping = regionFromMapping.getName().trim();
                        }
                    }

                    // Check if location already exists
                    Location existingLocation =
                            locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                    chromosomeNameFromMapping,
                                    chromosomePositionFromMapping,
                                    regionNameFromMapping);


                    if (existingLocation != null) {
                        newSnpLocations.add(existingLocation);
                    }
                    // Create new location
                    else {
                        Location newLocation = locationCreationService.createLocation(chromosomeNameFromMapping,
                                                                                      chromosomePositionFromMapping,
                                                                                      regionNameFromMapping);

                        newSnpLocations.add(newLocation);
                    }
                }

                // If we have new locations then link to snp and save
                if (newSnpLocations.size() > 0) {

                    // Set new location details
                    snpInDatabase.setLocations(newSnpLocations);
                    // Update the last update date
                    snpInDatabase.setLastUpdateDate(new Date());
                    singleNucleotidePolymorphismRepository.save(snpInDatabase);
                }
                else {getLog().warn("No new locations to add to " + snpRsId);}

            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                getLog().error("Adding location for SNP not found in database, RS_ID:" + snpRsId);
                throw new RuntimeException("Adding location for SNP not found in database, RS_ID: " + snpRsId);

            }

        }
    }

    /**
     * Method to remove the existing locations linked to a SNP
     *
     * @param snp SNP from which to remove the associated locations
     */
    public void removeExistingSnpLocations(SingleNucleotidePolymorphism snp) {

        // Get a list of locations currently linked to SNP
        Collection<Location> oldSnpLocations = snp.getLocations();

        if (oldSnpLocations != null && !oldSnpLocations.isEmpty()) {
            Set<Long> oldSnpLocationIds = new HashSet<>();
            for (Location oldSnpLocation : oldSnpLocations) {
                oldSnpLocationIds.add(oldSnpLocation.getId());
            }

            // Remove old locations
            snp.setLocations(new ArrayList<>());
            singleNucleotidePolymorphismRepository.save(snp);

            // Clean-up old locations that were linked to SNP
            if (oldSnpLocationIds.size() > 0) {
                for (Long oldSnpLocationId : oldSnpLocationIds) {
                    cleanUpLocations(oldSnpLocationId);
                }
            }
        }
    }

    /**
     * Method to remove any old locations that no longer have snps or genomic contexts linked to them
     *
     * @param id Id of location object
     */
    private void cleanUpLocations(Long id) {
        List<SingleNucleotidePolymorphism> snps =
                singleNucleotidePolymorphismRepository.findByLocationsId(id);
        List<GenomicContext> genomicContexts = genomicContextRepository.findByLocationId(id);

        if (snps.size() == 0 && genomicContexts.size() == 0) {
            locationRepository.delete(id);
        }
    }

}
