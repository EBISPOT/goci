package uk.ac.ebi.spot.goci.curation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.exception.ResourceNotFoundException;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import java.util.Optional;

@Slf4j
@Service
public class StudyDataService {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private DiseaseTraitRepository diseaseTraitRepository;

    public Page<Study> getStudiesByHousekeepingStatus(Pageable pageable,boolean isPublished){
        return studyRepository.findByHousekeepingIsPublished(pageable, isPublished);
    }

    public Optional<Study> getStudyByAccessionId(String accessionId) {
        return studyRepository.findByAccessionId(accessionId);
    }

    public Study updateStudyDiseaseTraitByAccessionId(String trait, String accessionId){
        Study study = this.getStudyByAccessionId(accessionId)
                .orElseThrow(()-> new ResourceNotFoundException("Study", accessionId));

        DiseaseTrait diseaseTrait = Optional.ofNullable(diseaseTraitRepository.findByTraitIgnoreCase(trait))
                .orElseThrow(()->new ResourceNotFoundException("Disease Trait", trait));

        study.setDiseaseTrait(diseaseTrait);
        studyRepository.save(study);
        log.info("Study with accession Id: {} found and updated", accessionId);
        return study;
    }
}
