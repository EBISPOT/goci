package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpMappingForm;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.repository.LocationRepository;

/**
 * Created by emma on 07/07/2015.
 */
@Service
public class SnpLocationMappingService {

    private LocationRepository locationRepository;

    //Constructor
    public SnpLocationMappingService() {
    }

    public void storeSnpLocation(SnpMappingForm snpMappingForm) {

        String snp = snpMappingForm.getSnp();
        Location location = snpMappingForm.getLocation();
        Long locationId;

        // Check if location already exists
        Location existingLocation =
                locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(location.getChromosomeName(),
                                                                                          location.getChromosomePosition(),
                                                                                          location.getRegion()
                                                                                                  .getName());

        if (existingLocation != null) {
            locationId = existingLocation.getId();
        }

        // Create location
        else {
            Location newLocation = createLocation(location);
            locationId = newLocation.getId();
        }

        // Link SNP to location



    }

    private Location createLocation(Location location) {
        Location newLocation = locationRepository.save(location);
        return newLocation;
    }


}
