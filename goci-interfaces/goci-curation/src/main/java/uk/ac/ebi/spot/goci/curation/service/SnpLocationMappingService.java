package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpMappingForm;
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
import java.util.HashMap;
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

    private LocationRepository locationRepository;
    private RegionRepository regionRepository;
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GenomicContextRepository genomicContextRepository;

    //Constructor
    @Autowired
    public SnpLocationMappingService(LocationRepository locationRepository,
                                     RegionRepository regionRepository,
                                     SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                     GenomicContextRepository genomicContextRepository) {
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.genomicContextRepository = genomicContextRepository;
    }

    /**
     * Method used to format data returned from view via form so it can be stored in database
     *
     * @param snpMappingForms list of snp maaping forms which contain rs_id and associated location information
     */
    public void processMappingForms(List<SnpMappingForm> snpMappingForms) {

        // Need to read through each form and flatten down information
        // and create structure linking each RS_ID to its location(s)
        Map<String, Set<Location>> snpToLocationsMap = new HashMap<>();

        for (SnpMappingForm snpMappingForm : snpMappingForms) {

            String snpInForm = snpMappingForm.getSnp();
            Location locationInForm = snpMappingForm.getLocation();

            // Next time we see SNP, add location to set
            // This would only occur is SNP has multiple locations
            if (snpToLocationsMap.containsKey(snpInForm)) {
                snpToLocationsMap.get(snpInForm).add(locationInForm);
            }

            // First time we see a SNP store the location
            else {
                Set<Location> snpLocation = new HashSet<>();
                snpLocation.add(locationInForm);
                snpToLocationsMap.put(snpInForm, snpLocation);
            }
        }

        storeSnpLocation(snpToLocationsMap);
    }

    public void storeSnpLocation(Map<String, Set<Location>> snpToLocations) {

        // Go through each rs_id and its associated locations returning from the mapping pipeline
        for (String snpRsId : snpToLocations.keySet()) {

            Set<Location> snpLocationsFromMapping = snpToLocations.get(snpRsId);

            // Check if the SNP exists
            List<SingleNucleotidePolymorphism> snpsInDatabase =
                    singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(snpRsId);

            if (!snpsInDatabase.isEmpty()) {

                // For each snp with that rs_id link it to the new location(s)
                for (SingleNucleotidePolymorphism snpInDatabase : snpsInDatabase) {

                    // Store all new location objects
                    Collection<Location> newSnpLocations = new ArrayList<>();

                    // Get a list of locations currently linked to our SNP
                    Collection<Location> oldSnpLocations = snpInDatabase.getLocations();
                    Collection<Long> oldSnpLocationIds = new ArrayList<>();
                    for (Location oldSnpLocation : oldSnpLocations) {
                        oldSnpLocationIds.add(oldSnpLocation.getId());
                    }

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
                            Location newLocation = createLocation(chromosomeNameFromMapping,
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

                        // Clean-up old locations that were linked to SNP
                        if (oldSnpLocationIds != null && oldSnpLocationIds.size() > 0) {
                            for (Long oldSnpLocationId : oldSnpLocationIds) {
                                cleanUpLocations(oldSnpLocationId);
                            }
                        }
                    }
                }
            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                throw new RuntimeException("Adding location for SNP not found in database, RS_ID: " + snpRsId);
            }

        }
    }

    private Location createLocation(String chromosomeName,
                                    String chromosomePosition,
                                    String regionName) {


        Region region = null;
        region = regionRepository.findByName(regionName);

        // If the region doesn't exist, save it
        if (region == null) {
            Region newRegion = new Region();
            newRegion.setName(regionName);
            region = regionRepository.save(newRegion);
        }

        Location newLocation = new Location();
        newLocation.setChromosomeName(chromosomeName);
        newLocation.setChromosomePosition(chromosomePosition);
        newLocation.setRegion(region);

        // Save location
        locationRepository.save(newLocation);
        return newLocation;
    }

    // Method to remove any old locations that no longer have snps or genomic contexts linked to them
    private void cleanUpLocations(Long id) {
        List<SingleNucleotidePolymorphism> snps =
                singleNucleotidePolymorphismRepository.findByLocationsId(id);

        List<GenomicContext> genomicContexts= genomicContextRepository.findByLocationId(id);

        if (snps.size() == 0 && genomicContexts.size() == 0) {
            locationRepository.delete(id);
        }
    }

}
