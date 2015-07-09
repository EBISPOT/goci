package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpMappingForm;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.RegionRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Collection;
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

    //Constructor
    @Autowired
    public SnpLocationMappingService(LocationRepository locationRepository,
                                     RegionRepository regionRepository,
                                     SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository) {
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
    }


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

        // Go through each rs_id and its associated locations returning from teh mapping pipeline
        for (String snpRsId : snpToLocations.keySet()) {

            Set<Location> snpLocationsInForm = snpToLocations.get(snpRsId);

            // Check if the SNP exists
            List<SingleNucleotidePolymorphism> snpsInDatabase =
                    singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(snpRsId);

            if (!snpsInDatabase.isEmpty()) {

                // For each snp with that rs_id link it to the new location(s)
                for (SingleNucleotidePolymorphism snpInDatabase : snpsInDatabase) {

                    // Store all new location objects
                    Collection<Location> newSnpLocations = new ArrayList<>();

                    for (Location snpLocationInForm : snpLocationsInForm) {

                        String chromosomeNameInForm = snpLocationInForm.getChromosomeName();
                        if (chromosomeNameInForm != null) {
                            chromosomeNameInForm = chromosomeNameInForm.trim();
                        }

                        String chromosomePositionInForm = snpLocationInForm.getChromosomePosition();
                        if (chromosomePositionInForm != null) {
                            chromosomePositionInForm = chromosomePositionInForm.trim();
                        }

                        Region regionInForm = snpLocationInForm.getRegion();
                        String regionNameInForm = null;
                        if (regionInForm != null) {
                            if (regionInForm.getName() != null) {
                                regionNameInForm = regionInForm.getName().trim();
                            }
                        }

                        // Check if location already exists
                        Location existingLocation =
                                locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                        chromosomeNameInForm,
                                        chromosomePositionInForm,
                                        regionNameInForm);


                        if (existingLocation != null) {
                            newSnpLocations.add(existingLocation);
                        }
                        // Create location
                        else {
                            Location newLocation = createLocation(chromosomeNameInForm,
                                                                  chromosomePositionInForm,
                                                                  regionNameInForm);

                            newSnpLocations.add(newLocation);
                        }
                    }

                    // Save new locations
                    snpInDatabase.setLocations(newSnpLocations);
                    singleNucleotidePolymorphismRepository.save(snpInDatabase);
                }
            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                throw new RuntimeException("Mapping SNP not found in database, RS_ID: " + snpRsId);
            }

        }
    }

    private Location createLocation(String chromosomeNameInForm,
                                    String chromosomePositionInForm,
                                    String regionNameInForm) {


        Region region = null;
        region = regionRepository.findByName(regionNameInForm);

        // If the region doesn't exist, save it
        if (region == null) {
            Region newRegion = new Region();
            newRegion.setName(regionNameInForm);
            region = regionRepository.save(newRegion);
        }

        Location newLocation = new Location();
        newLocation.setChromosomeName(chromosomeNameInForm);
        newLocation.setChromosomePosition(chromosomePositionInForm);
        newLocation.setRegion(region);

        // Save location
        locationRepository.save(newLocation);
        return newLocation;
    }

}
