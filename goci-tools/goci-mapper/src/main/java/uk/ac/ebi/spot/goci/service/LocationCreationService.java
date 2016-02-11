package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.repository.LocationRepository;
import uk.ac.ebi.spot.goci.repository.RegionRepository;

/**
 * Created by emma on 02/02/2016.
 *
 * @author emma
 *         <p>
 *         Service that creates a new location in the database
 */
@Service
public class LocationCreationService {

    // Repositories
    private LocationRepository locationRepository;
    private RegionRepository regionRepository;

    @Autowired
    public LocationCreationService(LocationRepository locationRepository,
                                   RegionRepository regionRepository) {
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
    }

    public Location createLocation(String chromosomeName,
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
}
