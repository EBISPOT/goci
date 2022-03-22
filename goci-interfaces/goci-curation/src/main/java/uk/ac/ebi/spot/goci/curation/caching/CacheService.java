package uk.ac.ebi.spot.goci.curation.caching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.PublicationOperationsService;
import uk.ac.ebi.spot.goci.curation.util.Sorting;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CacheService {

    @Autowired private CuratorRepository curatorRepository;
    @Autowired private PlatformRepository platformRepository;
    @Autowired private GenotypingTechnologyRepository genotypingTechnologyRepository;
    @Autowired private CurationStatusRepository curationStatusRepository;
    @Autowired private UnpublishReasonRepository unpublishReasonRepository;
    @Autowired private PublicationOperationsService publicationOperationsService;

    @Cacheable("curators")
    public List<Curator> getAllCurators() {
        log.info("Caching Curators ... ");
        List<Curator> curators = curatorRepository.findAll(Sorting.sortByLastNameAsc());
        log.info("{} curators found", curators.size());
        return curators;
    }

    @Cacheable("platforms")
    public List<Platform> getAllPlatforms() {
        log.info("Caching Platforms ... ");
        List<Platform> platforms = platformRepository.findAll();
        log.info("{} platforms found", platforms.size());
        return platforms;
    }

    @Cacheable("platformsHtml")
    public String getAllPlatformsHtml() {
        log.info("Caching Platforms ... ");
        List<Platform> platforms = this.getAllPlatforms();
        StringBuilder platformString = new StringBuilder();
        for (Platform efoTrait : platforms){
            platformString.append(String.format("<option value='%s'> %s </option>", efoTrait.getId(), efoTrait.getManufacturer()));
        }
        return platformString.toString();
    }

    @Cacheable("genotypingTechnologies")
    public List<GenotypingTechnology> getAllGenotypingTechnologies() {
        log.info("Caching Genotyping Technologies ... ");
        List<GenotypingTechnology> genotypingTechnologies = genotypingTechnologyRepository.findAll();
        log.info("{} Genotyping Technologies found", genotypingTechnologies.size());
        return genotypingTechnologies;
    }

    @Cacheable("curationStatuses")
    public List<CurationStatus> getAllCurationStatuses() {
        log.info("Caching Curation Statuses ... ");
        List<CurationStatus> curationStatuses = curationStatusRepository.findAll(Sorting.sortByStatusAsc());
        log.info("{} Curation Statuses found", curationStatuses.size());
        return curationStatuses;
    }

    @Cacheable("unpublishReasons")
    public List<UnpublishReason> getAllUnpublishReasons() {
        log.info("Caching Unpublish Reasons ... ");
        List<UnpublishReason> unpublishReasons = unpublishReasonRepository.findAll();
        log.info("{} Unpublish Reasons found", unpublishReasons.size());
        return unpublishReasons;
    }

    @Cacheable("studyTypes")
    public List<String> getAllStudyTypeOptions() {
        return Arrays.asList(
                "GXE",
                "GXG",
                "CNV",
                "Genome-wide genotyping array studies",
                "Targeted genotyping array studies",
                "Exome genotyping array studies",
                "Genome-wide sequencing studies",
                "Exome-wide sequencing studies",
                "Studies in curation queue",
                "Multi-SNP haplotype studies",
                "SNP Interaction studies",
                "p-Value Set",
                "User Requested",
                "Open Targets");
    }

    @Cacheable("qualifiers")
    public List<String> getAllQualifierOptions() {
        return Arrays.asList("up to", "at least", "~", ">");
    }

    @Cacheable("authors")
    public List<String> getAllAuthors() {
        return publicationOperationsService.listFirstAuthors();
    }
}
