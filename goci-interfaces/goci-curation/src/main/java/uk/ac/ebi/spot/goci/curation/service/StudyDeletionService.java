package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.DeletedStudy;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DeletedStudyRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
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

    private AncestryRepository ancestryRepository;
    private TrackingOperationService trackingOperationService;
    private StudyRepository studyRepository;
    private DeletedStudyRepository deletedStudyRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyDeletionService(AncestryRepository ancestryRepository,
                                @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                StudyRepository studyRepository,
                                DeletedStudyRepository deletedStudyRepository) {
        this.ancestryRepository = ancestryRepository;
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
        this.deletedStudyRepository = deletedStudyRepository;
    }
    /**
     * Delete a study
     *
     * @param study Study to delete
     * @param user  User
     */
    public void deleteStudy(Study study, SecureUser user) {

        getLog().warn("Deleting study: ".concat(String.valueOf(study.getId())));

        // Before we delete the study get its associated ancestry
        Collection<Ancestry> ancestriesAttachedToStudy = ancestryRepository.findByStudyId(study.getId());

        // Delete ancestry information linked to this study
        for (Ancestry ancestry : ancestriesAttachedToStudy) {
            ancestryRepository.delete(ancestry);
        }

        // Add deletion event
        trackingOperationService.delete(study, user);
        DeletedStudy deletedStudy = createDeletedStudy(study);

        // Delete study
        studyRepository.delete(study);

        // Save deleted study details
        getLog().info("Saving details of deleted study: ".concat(String.valueOf(deletedStudy.getId())));
        deletedStudyRepository.save(deletedStudy);
    }

    /**
     * Create a deleted study entry in the database
     *
     * @param study Study that will be deleted
     * @return DeletedStudy object
     */
    private DeletedStudy createDeletedStudy(Study study) {
        Collection<Event> events = study.getEvents();
        Long id = study.getId();
        String pubmed = study.getPubmedId();
        String title = study.getTitle();
        return new DeletedStudy(id, title, pubmed, events);
    }
}
