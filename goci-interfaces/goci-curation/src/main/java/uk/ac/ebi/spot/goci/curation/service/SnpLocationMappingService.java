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

import java.util.List;

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

    public void storeSnpLocation(SnpMappingForm snpMappingForm) {

        String snp = snpMappingForm.getSnp();
        Location location = snpMappingForm.getLocation();
        Long locationId;

        // Check if location already exists
        // TODO THIS WILL CRASH , WE HAVE DUPLICATE LOCATIONS
        Location existingLocation =
                locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(location.getChromosomeName(),
                                                                                          location.getChromosomePosition(),
                                                                                          location.getRegion()
                                                                                                  .getName());

        if (existingLocation != null) {
            locationId = existingLocation.getId();
        }

        // Otherwise create location
        else {
            Location newLocation = createLocation(location);
            locationId = newLocation.getId();
        }

        // Find ID in database for SNP
        Long snpId;
        List<SingleNucleotidePolymorphism> snps = singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(snp);
        if (snps.size() > 0) {
            // TODO HOW DO WE HANDLE DUPLICATES
            // LINK ALL SNPS WITH THAT RS_ID TO THAT LOCATION
        }

        // If SNP doesn't exist
        else {
            //   TODO WHAT SHOULD WE DO IN THIS CASE
            // TODO THIS IS CASE WHERE SNP DOESN'T EXIST
            // COULD THE RS_ID BE FROM THE MERGED SNP
        }


    }

    private Location createLocation(Location location) {

        // First save the region
        Region region = null;
        String regionName = location.getRegion().getName();

        region = regionRepository.findByName(regionName);

        // If the region doesn't exist, save it
        if (region == null) {
            region = regionRepository.save(location.getRegion());
        }

        // Set region on location
        location.setRegion(region);

        // Save location
        Location newLocation = locationRepository.save(location);
        return newLocation;
    }


}
