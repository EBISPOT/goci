package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.HousekeepingOperationsService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.model.deposition.EFOTraitDTO;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SingleStudyProcessingService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private DiseaseTraitRepository diseaseTraitRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private GenotypingTechnologyRepository genotypingTechnologyRepository;

    @Autowired
    private HousekeepingOperationsService housekeepingRepository;

    @Autowired
    private CuratorRepository curatorRepository;

    @Autowired
    private CurationStatusRepository statusRepository;

    @Autowired
    private StudyService studyService;

    @Autowired
    private EfoTraitRepository efoTraitRepository;

    @Autowired
    private StudyExtensionRepository studyExtensionRepository;

    public Pair<Study, List<EfoTrait>> processStudy(DepositionStudyDto studyDto, Publication publication, Boolean openTargets, Boolean userRequested) {
        Curator levelTwoCurator = curatorRepository.findByLastName("Level 2 Curator");
        CurationStatus levelOneCurationComplete = statusRepository.findByStatus("Level 1 curation done");

        getLog().info("Initializing study: {} | {}", publication.getPubmedId(), studyDto.getAccession());
        Study study = studyDto.buildStudy();
        DiseaseTrait diseaseTrait = Optional.ofNullable(studyDto.getDiseaseTraitDto())
                .map(DiseaseTraitDto::getTrait)
                .map(diseaseTraitRepository::findByTraitIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);
        //DiseaseTrait diseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getTrait()).get();
        study.setDiseaseTrait(diseaseTrait);


        String manufacturerString = studyDto.getArrayManufacturer();
        if (manufacturerString != null) {
            List<Platform> platformList = new ArrayList<>();
            String[] manufacturers = manufacturerString.split("\\||,");
            getLog().info("Manufacturers provided: {}", manufacturerString);
            for (String manufacturer : manufacturers) {
                Platform platform = platformRepository.findByManufacturer(manufacturer.trim());
                platformList.add(platform);
            }
            getLog().info("Manufacturers mapped: {}", platformList);
            study.setPlatforms(platformList);
        }
        List<GenotypingTechnology> gtList = new ArrayList<>();
        String genotypingTech = studyDto.getGenotypingTechnology();
        if (genotypingTech != null) {
            String[] technologies = genotypingTech.split("\\||,");
            getLog().info("Genotyping technology provided: {}", genotypingTech);
            for (String technology : technologies) {
                GenotypingTechnology gtt = genotypingTechnologyRepository.findByGenotypingTechnology(
                        DepositionTransform.transformGenotypingTechnology(technology.trim()));
                gtList.add(gtt);
            }
        }
        getLog().info("Genotyping technology mapped: {}", gtList);
        study.setGenotypingTechnologies(gtList);

        study.setPublicationId(publication);
        getLog().info("Creating house keeping ...");
        Housekeeping housekeeping = housekeepingRepository.createHousekeeping();
        getLog().info("House keeping created: {}", housekeeping.getId());
        study.setHousekeeping(housekeeping);
        housekeeping.setCurator(levelTwoCurator);
        housekeeping.setCurationStatus(levelOneCurationComplete);
        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
            getLog().info("Full p-value set to TRUE.");
        }
        Integer variantCount = studyDto.getVariantCount();
        if (variantCount != -1) {
            study.setSnpCount(variantCount);
        }
       // List<EfoTrait> efoTraitList = new ArrayList<>();
        //String efoTrait = studyDto.getEfoTrait();

        List<EfoTrait> efoTraitList =  Optional.ofNullable(studyDto.getEfoTraitDtos())
                .map(efoTraitDTOList -> efoTraitDTOList.stream()
                        .map(EFOTraitDTO::getShortForm)
                        .map(efoTraitRepository::findByShortForm)
                        .collect(Collectors.toList()))
                .orElse(null);

        List<EfoTrait> backgroundEfoTraitList =  Optional.ofNullable(studyDto.getBackgroundEfoTraitDtos())
                .map(efoTraitDTOList -> efoTraitDTOList.stream()
                        .map(EFOTraitDTO::getShortForm)
                        .map(efoTraitRepository::findByShortForm)
                        .collect(Collectors.toList()))
                .orElse(null);


        /*if (efoTrait != null) {
            String[] efoTraits = efoTrait.split("\\||,");
            getLog().info("EFO traits provided: {}", efoTraits);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait.trim());
                efoTraitList.add(dbTrait);
            }
        }*/
        List<EfoTrait> mappedTraitList = new ArrayList<>();
        getLog().info("EFO traits mapped: {}", efoTraitList);
        study.setEfoTraits(efoTraitList);
        /*String mappedBackgroundTrait = studyDto.getBackgroundEfoTrait();
        if (mappedBackgroundTrait != null) {
            String[] efoTraits = mappedBackgroundTrait.split("\\||,");
            getLog().info("Background EFO traits provided: {}", mappedBackgroundTrait);
            for (String trait : efoTraits) {
                EfoTrait dbTrait = efoTraitRepository.findByShortForm(trait);
                mappedTraitList.add(dbTrait);
            }
        }*/
        getLog().info("Background EFO traits mapped: {}", mappedTraitList);
        study.setMappedBackgroundTraits(backgroundEfoTraitList);
        Optional<DiseaseTrait> backgroundTraitOpt = diseaseTraitRepository.findByTraitIgnoreCase(studyDto.getBackgroundTrait());
        backgroundTraitOpt.ifPresent(study::setBackgroundTrait);

        if (studyDto.getSummaryStatisticsFile() != null && !studyDto.getSummaryStatisticsFile().equals("") && !studyDto.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
        }
        study.setStudyDesignComment(studyDto.getArrayInformation());
        study.setOpenTargets(openTargets);
        study.setUserRequested(userRequested);
        study.setPooled(studyDto.getPooledFlag());
        study.setGxe(studyDto.getGxeFlag());
        study.setInitialSampleSize(studyDto.getInitialSampleDescription());
        study.setReplicateSampleSize(studyDto.getReplicateSampleDescription());
        getLog().info("Saving study ...");
        studyService.save(study);
        getLog().info("Study saved: {}", study.getId());

        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setStudyDescription(studyDto.getStudyDescription());
        studyExtension.setCohort(studyDto.getCohort());
        studyExtension.setCohortSpecificReference(studyDto.getCohortId());
        studyExtension.setStatisticalModel(studyDto.getStatisticalModel());
        studyExtension.setSummaryStatisticsFile(studyDto.getSummaryStatisticsFile());
        studyExtension.setSummaryStatisticsAssembly(studyDto.getSummaryStatisticsAssembly());
        studyExtension.setStudy(study);

        getLog().info("Saving study extension...");
        studyExtensionRepository.save(studyExtension);
        getLog().info("Study extension saved: {}", studyExtension.getId());
        study.setStudyExtension(studyExtension);
        studyService.save(study);

        return Pair.of(study, efoTraitList);
    }

}
