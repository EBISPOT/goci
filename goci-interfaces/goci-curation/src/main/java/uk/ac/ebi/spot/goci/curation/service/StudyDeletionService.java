package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collection;

/**
 * Created by emma on 31/05/2016.
 *
 * @author emma
 *         <p>
 *         Service to delete a study from curation interface
 */

@Service
public class StudyDeletionService {

    /**
     * Delete a study
     *
     * @param study Study to delete
     * @param user  User
     */

    private HousekeepingRepository housekeepingRepository;
    private EthnicityRepository ethnicityRepository;
    private TrackingOperationService trackingOperationService;
    private StudyRepository studyRepository;

    public void deleteStudy(Study study, SecureUser user) {

        // Before we delete the study get its associated housekeeping and ethnicity
        Long housekeepingId = study.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);
        Collection<Ethnicity> ethnicitiesAttachedToStudy = ethnicityRepository.findByStudyId(study.getId());

        // Delete ethnicity information linked to this study
        for (Ethnicity ethnicity : ethnicitiesAttachedToStudy) {
            ethnicityRepository.delete(ethnicity);
        }

        // Delete housekeeping
        housekeepingRepository.delete(housekeepingAttachedToStudy);

        // Add deletion event
        trackingOperationService.delete(study, user);
        createDeletedStudy(study);

        // Delete study
        studyRepository.delete(study);


    }

    private void createDeletedStudy(Study study) {
        Collection<Event> events = study.getEvents();
        Long id = study.getId();
        String pubmed = study.getPubmedId();
        String title = study.getTitle();

        // TODO CREATE DELETED_STUDY
        // TODO WILL NEED REPO AND MODEL OBJECT

    }
}
