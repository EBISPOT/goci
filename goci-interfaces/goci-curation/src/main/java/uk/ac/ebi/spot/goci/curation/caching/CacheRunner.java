package uk.ac.ebi.spot.goci.curation.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.cron.SyncReportedTraitsTask;
import uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService;
import uk.ac.ebi.spot.goci.curation.service.EfoTraitService;

@Component
public class CacheRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CacheRunner.class);

    @Autowired private CacheService cacheService;
    @Autowired private DiseaseTraitService diseaseTraitService;
    @Autowired private EfoTraitService efoTraitService;

    @Override
    public void run(String... strings) throws Exception {
        this.runCache();
    }

    public void runCache(){
        log.info("Running runCache() inside CacheRunner");
        diseaseTraitService.getAllDiseaseTraits();
        diseaseTraitService.getAllDiseaseTraitsHtml();
        efoTraitService.getAllEFOTraits();
        efoTraitService.getAllEFOTraitsHtml();
        cacheService.getAllCurators();
        cacheService.getAllPlatforms();
        cacheService.getAllGenotypingTechnologies();
        cacheService.getAllCurationStatuses();
        cacheService.getAllUnpublishReasons();
        cacheService.getAllPlatformsHtml();
    }
}
