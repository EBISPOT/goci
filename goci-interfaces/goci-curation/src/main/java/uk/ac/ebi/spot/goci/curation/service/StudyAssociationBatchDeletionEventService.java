package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

/**
 * Created by emma on 26/08/2016.
 *
 * @author emma
 *         <p>
 *         Service that records a batch deletion event of associations
 */
@Service
public class StudyAssociationBatchDeletionEventService {

    private StudyRepository studyRepository;

    private TrackingOperationService trackingOperationService;

    @Autowired
    public StudyAssociationBatchDeletionEventService(StudyRepository studyRepository,
                                                     @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService) {
        this.studyRepository = studyRepository;
        this.trackingOperationService = trackingOperationService;
    }

    /**
     * Upload a file to the study specific dir
     *
     * @param studyId          Study to assign event to
     * @param associationCount Number of associations deleted
     * @param user             User that triggered event
     */
    public void createBatchUploadEvent(Long studyId, Integer associationCount, SecureUser user) {
        Study study = studyRepository.findOne(studyId);
        String description = associationCount.toString()
                .concat(" associations deleted");
        createEvent(study, user, description);
    }

    private void createEvent(Study study, SecureUser user, String description) {
        trackingOperationService.update(study, user, "ASSOCIATION_BATCH_DELETE", description);
        studyRepository.save(study);
    }
}
