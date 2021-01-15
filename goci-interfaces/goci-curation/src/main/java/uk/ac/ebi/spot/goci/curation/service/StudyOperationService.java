package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

@Service
public class StudyOperationService {

    @Autowired
    private StudyRepository studyRepository;

    public Page<Study> getStudiesByHousekeepingStatus(Pageable pageable,boolean isPublished){
        return studyRepository.findByHousekeepingIsPublished(pageable, isPublished);
    }
}
