package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 22/10/2015.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on study
 */
@Service
public class StudyOperationsService {

    private AssociationRepository associationRepository;
    private MailService mailService;
    private HousekeepingRepository housekeepingRepository;
    private PublishStudyCheckService publishStudyCheckService;
    private StudyRepository studyRepository;
    private CuratorRepository curatorRepository;
    private CurationStatusRepository curationStatusRepository;
    private TrackingOperationService trackingOperationService;

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository,
                                  PublishStudyCheckService publishStudyCheckService,
                                  StudyRepository studyRepository,
                                  CuratorRepository curatorRepository,
                                  CurationStatusRepository curationStatusRepository,
                                  @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
        this.publishStudyCheckService = publishStudyCheckService;
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.trackingOperationService = trackingOperationService;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Update a study status
     *
     * @param study Study to save
     * @param user  User preforming request
     * @return ID of study to save
     */
    public Study saveStudy(Study study, SecureUser user) {

        // Update and save study
        study.setHousekeeping(createHousekeeping());
        trackingOperationService.create(study, user);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" created"));
        return study;
    }

    /**
     * Update a study status
     *
     * @param newStatus          New status to apply to study
     * @param study              Study to update
     * @param currentStudyStatus Current status of the study to update
     * @param user               User preforming request
     */
    public String updateStatus(CurationStatus newStatus,
                               Study study,
                               CurationStatus currentStudyStatus,
                               SecureUser user) {

        Housekeeping housekeeping = study.getHousekeeping();
        String message = null;
        // If the status has changed
        if (newStatus != null && newStatus != currentStudyStatus) {
            switch (newStatus.getStatus()) {
                case "Publish study":

                    // Run pre-publish checks
                    Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                    message = publishStudyCheckService.runChecks(study, associations);

                    if (message == null) {
                        // If there is no existing publish date then update
                        if (study.getHousekeeping().getCatalogPublishDate() == null) {
                            Date publishDate = new Date();
                            housekeeping.setCatalogPublishDate(publishDate);
                        }
                        mailService.sendEmailNotification(study, newStatus.getStatus());
                        housekeeping.setCurationStatus(newStatus);
                    }
                    // restore previous status
                    else {
                        housekeeping.setCurationStatus(currentStudyStatus);
                    }
                    break;

                // Send notification email to curators
                case "Level 1 curation done":
                    mailService.sendEmailNotification(study, newStatus.getStatus());
                    housekeeping.setCurationStatus(newStatus);
                    break;
                default:
                    housekeeping.setCurationStatus(newStatus);
                    break;
            }
            // Save changes
            housekeepingRepository.save(housekeeping);
            housekeeping.setLastUpdateDate(new Date());

            // Create event
            EventType eventType = determineEventTypeFromStatus(newStatus);
            trackingOperationService.update(study, user, eventType);
            studyRepository.save(study);
            getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" status updated"));
        }
        return message;
    }

    /**
     * Create study housekeeping
     */
    private Housekeeping createHousekeeping() {
        // Create housekeeping object and create the study added date
        Housekeeping housekeeping = new Housekeeping();
        java.util.Date studyAddedDate = new java.util.Date();
        housekeeping.setStudyAddedDate(studyAddedDate);

        // Set status
        CurationStatus status = curationStatusRepository.findByStatus("Awaiting Curation");
        housekeeping.setCurationStatus(status);

        // Set curator
        Curator curator = curatorRepository.findByLastName("Level 1 Curator");
        housekeeping.setCurator(curator);

        // Save housekeeping
        housekeepingRepository.save(housekeeping);

        // Save housekeeping
        return housekeeping;
    }

    /**
     * Assign status to a study
     */
    public String assignStudyStatus(Study study, StatusAssignment statusAssignment, SecureUser userFromRequest) {
        Long statusId = statusAssignment.getStatusId();
        CurationStatus status = curationStatusRepository.findOne(statusId);
        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();
        return updateStatus(status, study, currentStudyStatus, userFromRequest);
    }

    /**
     * Update housekeeping
     */
    public String updateHousekeeping(Housekeeping housekeeping, Study study, SecureUser userFromRequest) {

        String message = null;

        // Before we save housekeeping get the status in database so we can check for a change
        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();

        // Save housekeeping returned from form straight away to save any curator entered details like notes etc
        housekeeping.setLastUpdateDate(new Date());
        housekeepingRepository.save(housekeeping);

        // Update status
        CurationStatus newStatus = housekeeping.getCurationStatus();
        if (newStatus != null && newStatus != currentStudyStatus) {
            message = updateStatus(newStatus, study, currentStudyStatus,
                                   userFromRequest);
        }
        return message;
    }

    /**
     * Determine event type based on status
     *
     * @param status curation status to determine event type from
     * @return eventType
     */
    private EventType determineEventTypeFromStatus(CurationStatus status) {
        EventType eventType = null;
        switch (status.getStatus()) {
            case "Level 1 ancestry done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_1_ANCESTRY_DONE;
                break;
            case "Level 2 ancestry done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_2_ANCESTRY_DONE;
                break;
            case "Level 1 curation done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_1_CURATION_DONE;
                break;
            case "Level 2 curation done":
                eventType = EventType.STUDY_STATUS_CHANGE_LEVEL_2_CURATION_DONE;
                break;
            case "Publish study":
                eventType = EventType.STUDY_STATUS_CHANGE_PUBLISH_STUDY;
                break;
            case "Awaiting Curation":
                eventType = EventType.STUDY_STATUS_CHANGE_AWAITING_CURATION;
                break;
            case "Outstanding Query":
                eventType = EventType.STUDY_STATUS_CHANGE_OUTSTANDING_QUERY;
                break;
            case "CNV Paper":
                eventType = EventType.STUDY_STATUS_CHANGE_CNV_PAPER;
                break;
            case "Curation Abandoned":
                eventType = EventType.STUDY_STATUS_CHANGE_CURATION_ABANDONED;
                break;
            case "Conversion Problem":
                eventType = EventType.STUDY_STATUS_CHANGE_CONVERSION_PROBLEM;
                break;
            case "Unpublished from catalog":
                eventType = EventType.STUDY_STATUS_CHANGE_UNPUBLISHED_FROM_CATALOG;
                break;
            case "Pending author query":
                eventType = EventType.STUDY_STATUS_CHANGE_PENDING_AUTHOR_QUERY;
                break;
            case "Awaiting EFO assignment":
                eventType = EventType.STUDY_STATUS_CHANGE_AWAITING_EFO_ASSIGNMENT;
                break;
            default:
                eventType = EventType.STUDY_STATUS_CHANGE_UNKNOWN;
                break;
        }
        return eventType;
    }
}