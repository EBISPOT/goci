package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.curation.dto.DiseaseTraitDto;
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

    public Page<DiseaseTrait> getDiseaseTraits(Pageable pageable) {
        return diseaseTraitRepository.findAll(pageable);
    }

    public DiseaseTrait createDiseaseTrait(DiseaseTrait diseaseTrait) {
        diseaseTrait = diseaseTraitRepository.save(diseaseTrait);
        return diseaseTrait;
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




}
