package uk.ac.ebi.spot.goci.curation.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheRunner implements CommandLineRunner {

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(String... strings) throws Exception {
        cacheService.getAllDiseaseTraits();
        cacheService.getAllDiseaseTraitsHtml();
        cacheService.getAllEFOTraits();
        cacheService.getAllEFOTraitsHtml();
        cacheService.getAllCurators();
        cacheService.getAllPlatforms();
        cacheService.getAllGenotypingTechnologies();
        cacheService.getAllCurationStatuses();
        cacheService.getAllUnpublishReasons();
        cacheService.getAllPlatformsHtml();
    }
}
