package uk.ac.ebi.spot.goci.curation.service;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisCacheDto;
import uk.ac.ebi.spot.goci.curation.dto.AnalysisDTO;
import uk.ac.ebi.spot.goci.model.deposition.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.curation.exception.DataIntegrityException;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DiseaseTraitService {

    private static final Logger log = LoggerFactory.getLogger(DiseaseTraitService.class);
    private DiseaseTraitRepository diseaseTraitRepository;
    private StudyRepository studyRepository;

    public DiseaseTraitService(DiseaseTraitRepository diseaseTraitRepository,
                               StudyRepository studyRepository) {
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.studyRepository = studyRepository;
    }

    public Optional<DiseaseTrait> getDiseaseTrait(Long traitId) {
        return Optional.ofNullable(diseaseTraitRepository.findOne(traitId));
    }

    public void syncDiseaseTraitMongoSeqId(DiseaseTrait diseaseTrait) {
        diseaseTraitRepository.save(diseaseTrait);
    }

    public List<DiseaseTrait> removeExistingTraits(List<DiseaseTrait> diseaseTraits) {
        return diseaseTraits.stream().filter((diseaseTrait) -> !diseaseTraitRepository.findByTraitIgnoreCase(diseaseTrait.getTrait()).isPresent())
                .collect(Collectors.toList());
    }

    public Page<DiseaseTrait> getDiseaseTraits(Pageable pageable) {
        return diseaseTraitRepository.findAll(pageable);
    }

    public DiseaseTrait createDiseaseTrait(DiseaseTrait diseaseTrait) {
        diseaseTraitRepository.findByTraitIgnoreCase(diseaseTrait.getTrait())
                .ifPresent(trait -> {
                    throw new DataIntegrityException(String.format("Trait %s already exist", trait.getTrait()));
                });
        diseaseTrait = diseaseTraitRepository.save(diseaseTrait);
        return diseaseTrait;
    }

    public Page<DiseaseTrait> searchByParameter(String search, Pageable pageable) {
        return diseaseTraitRepository.findBySearchParameter(search, pageable);
    }

    public Optional<DiseaseTrait> updateDiseaseTrait(DiseaseTraitDto diseaseTraitDTO, Long traitId) {
        return this.getDiseaseTrait(traitId)
                .map(diseaseTrait -> {
                    diseaseTrait.setTrait(diseaseTraitDTO.getTrait());
                    log.info("Updating {}: {}", EntityType.DISEASE_TRAIT, diseaseTrait.getTrait());
                    return Optional.ofNullable(this.createDiseaseTrait(diseaseTrait));
                })
                .orElseGet(Optional::empty);
    }

    public Map<String, Object> deleteDiseaseTrait(Long diseaseTraitId) {
        log.info("Attempting to delete {}: {}", EntityType.DISEASE_TRAIT, diseaseTraitId);
        Map<String, Object> response = new HashMap<>();

        return this.getDiseaseTrait(diseaseTraitId)
                .map(diseaseTrait -> {
                    Collection<Study> diseaseTraitStudies = studyRepository.findByDiseaseTraitId(diseaseTraitId);
                    if (diseaseTraitStudies.isEmpty()) {
                        diseaseTraitRepository.delete(diseaseTraitId);
                        log.info("Delete Trait: {} was successful", diseaseTraitId);
                        response.put("status", "deleted");
                    } else {
                        log.error("Trait: {} is in use, has {} studies, so cannot be deleted", diseaseTraitId, diseaseTraitStudies.size());
                        response.put("status", "DATA_IN_USE");
                        response.put("studyCount", diseaseTraitStudies.size());
                    }
                    return response;
                })
                .orElseGet(() -> {
                    log.error("Unable to find Disease Trait: {}", diseaseTraitId);
                    return response;
                });
    }

    public List<DiseaseTrait> createDiseaseTraits(List<DiseaseTrait> diseaseTraits) {
        log.info("Creating {}: {}", EntityType.DISEASE_TRAIT,
                 diseaseTraits.stream()
                         .map(DiseaseTrait::getTrait)
                         .collect(Collectors.toList()));
        diseaseTraits = diseaseTraitRepository.save(diseaseTraits);
        log.info("Bulk {} created", EntityType.DISEASE_TRAIT);
        return diseaseTraits;
    }


    @Cacheable(value = "diseaseTraitAnalysis", key = "#analysisId")
    public AnalysisCacheDto similaritySearch(List<AnalysisDTO> diseaseTraitAnalysisDTOS, String analysisId, double threshold) {
        LevenshteinDistance lv = new LevenshteinDistance();
        CosineDistance cd = new CosineDistance();

        List<DiseaseTrait> diseaseTraits = diseaseTraitRepository.findAll();
        List<AnalysisDTO> analysisReport = new ArrayList<>();
        diseaseTraitAnalysisDTOS
                .forEach(diseaseTraitAnalysisDTO ->
                                 diseaseTraits.forEach(diseaseTrait -> {
                                                           String trait = diseaseTrait.getTrait();
                                                           String userTerm = diseaseTraitAnalysisDTO.getUserTerm();

                                                           double cosineDistance = cd.apply(userTerm, trait);
                                                           double levenshteinDistance = ((double) lv.apply(userTerm, trait)) / Math.max(userTerm.length(), trait.length());
                                                           double cosineSimilarityPercent = Math.round((1 - cosineDistance) * 100);
                                                           double levenshteinSimilarityPercent = Math.round((1 - levenshteinDistance) * 100);
                                                           double chosen = Math.max(cosineSimilarityPercent, levenshteinSimilarityPercent);
                                                           if (chosen >= threshold) {
                                                               AnalysisDTO report = AnalysisDTO.builder()
                                                                       .userTerm(userTerm)
                                                                       .similarTerm(trait)
                                                                       .degree(chosen).build();
                                                               analysisReport.add(report);
                                                           }
                                                       }
                                 ));

        return AnalysisCacheDto.builder()
                .uniqueId(analysisId)
                .analysisResult(analysisReport).build();
    }


}
