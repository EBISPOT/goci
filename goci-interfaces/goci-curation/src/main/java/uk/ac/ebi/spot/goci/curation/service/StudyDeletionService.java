package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.service.*;
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
    private StudyService studyService;
    private PublicationOperationsService publicationOperationsService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public StudyDeletionService(AncestryRepository ancestryRepository,
                                @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                StudyRepository studyRepository,
                                DeletedStudyRepository deletedStudyRepository,
                                StudyService studyService,
                                PublicationOperationsService publicationOperationsService) {
        this.ancestryRepository = ancestryRepository;
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
        this.deletedStudyRepository = deletedStudyRepository;
        this.studyService = studyService;
        this.publicationOperationsService = publicationOperationsService;
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

        // WeeklyTracking, CuratorTracking and Note. Please use this method!
        // Shared with === DataDeletionService ===
        studyService.deleteRelatedInfoByStudy(study);

        // Add deletion event
        trackingOperationService.delete(study, user);
        DeletedStudy deletedStudy = createDeletedStudy(study);

        // THOR - Don't delete the publication and Author - OR check if there is just a publication.
        Publication publication = study.getPublicationId();
        study.setPublicationId(null);
        studyRepository.save(study);
        // Delete study
        studyRepository.delete(study);


        publicationOperationsService.deletePublicationWithNoStudies(publication);

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
        // THOR
        String pubmed = study.getPublicationId().getPubmedId();
        String title = study.getPublicationId().getTitle();
        return new DeletedStudy(id, title, pubmed, events);
    }
}
