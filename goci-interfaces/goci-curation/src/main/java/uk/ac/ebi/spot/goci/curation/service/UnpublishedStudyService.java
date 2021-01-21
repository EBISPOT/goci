package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.UnpublishedStudy;
import uk.ac.ebi.spot.goci.repository.UnpublishedStudyRepository;

import java.util.Optional;

@Service
public class UnpublishedStudyService {

    @Autowired
    private UnpublishedStudyRepository unpublishedStudyRepository;

    public Page<UnpublishedStudy> getUnpublishedStudiesBySumStatsFile(Pageable pageable){
        return unpublishedStudyRepository.findBySummaryStatsFileIsNotRequired(pageable);
    }

    public Optional<UnpublishedStudy> getUnpublishedStudy(Long unpublishedStudyId) {
        return Optional.ofNullable(unpublishedStudyRepository.findOne(unpublishedStudyId));
    }
}
