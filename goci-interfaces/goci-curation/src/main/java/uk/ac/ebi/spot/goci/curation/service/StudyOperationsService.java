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
     * Assign status to a study
     */
    public String assignStudyStatus(Study study, StatusAssignment statusAssignment, SecureUser userFromRequest) {

        CurationStatus newStatus = curationStatusRepository.findOne(statusAssignment.getStatusId());
        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();

        String message = null;

        // Create new housekeeping object
        Housekeeping newHousekeeping = study.getHousekeeping();
        newHousekeeping.setCurationStatus(newStatus);

        // If the current and new status are different
        if (newStatus != null && newStatus != currentStudyStatus) {
            if (newStatus.getStatus().equals("Publish study")) {
                // Run pre-publish checks first
                Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                message = publishStudyCheckService.runChecks(study, associations);
                // if checks pass then update the status
                if (message == null) {
                    updateStatus(study, newHousekeeping, userFromRequest);
                }
            }
            else {
                updateStatus(study, newHousekeeping, userFromRequest);
            }
        }
        else {
            message = "Current status and new status are the same, no change required";
        }
        return message;
    }

    /**
     * Update housekeeping
     */
    public String updateHousekeeping(Housekeeping housekeeping, Study study, SecureUser userFromRequest) {

        CurationStatus newStatus = housekeeping.getCurationStatus();
        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();

        String message = null;

        // If the current and new status are different
        if (newStatus != null && newStatus != currentStudyStatus) {
            if (newStatus.getStatus().equals("Publish study")) {
                // Run pre-publish checks first
                Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                message = publishStudyCheckService.runChecks(study, associations);
                // if checks pass then update the status
                if (message == null) {
                    updateStatus(study, housekeeping, userFromRequest);
                }
                // restore old status
                else {
                    housekeeping.setCurationStatus(currentStudyStatus);
                    saveHousekeeping(study, housekeeping);
                }
            }
            else {
                updateStatus(study, housekeeping, userFromRequest);
            }
        }
        // TODO WRITE TEST
        else {
            // Save housekeeping returned from form
            saveHousekeeping(study, housekeeping);
        }
        return message;
    }

    /**
     * Save housekeepoing
     *
     * @param housekeeping
     * @param study
     */
    private void saveHousekeeping(Study study, Housekeeping housekeeping) {
        // Save housekeeping returned from form
        // todo TEST SETTING STUDY ATTRIBUTE
        housekeeping.setLastUpdateDate(new Date());
        housekeepingRepository.save(housekeeping);
        study.setHousekeeping(housekeeping);
        studyRepository.save(study);
    }

    /**
     * Record a study status change
     *
     * @param newStatus New status to apply to study
     * @param study     Study to update
     * @param user      User preforming request
     */
    private void recordStudyStatusChange(Study study, SecureUser user, CurationStatus newStatus) {
        // Create syudy event
        EventType eventType = determineEventTypeFromStatus(newStatus);
        trackingOperationService.update(study, user, eventType);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" status updated"));
    }

    /**
     * Update a study status
     *
     * @param study        Study to update
     * @param housekeeping Study housekeeping object to apply status change to
     * @param user         User preforming request
     */
    private void updateStatus(Study study, Housekeeping housekeeping,
                              SecureUser user) {

        switch (housekeeping.getCurationStatus().getStatus()) {
            case "Publish study":
                // If there is no existing publish date then update
                if (housekeeping.getCatalogPublishDate() == null) {
                    Date publishDate = new Date();
                    housekeeping.setCatalogPublishDate(publishDate);
                }
                mailService.sendEmailNotification(study, "Publish study");
                break;
            // Send notification email to curators
            case "Level 1 curation done":
                mailService.sendEmailNotification(study, "Level 1 curation done");
                break;
            default:
                break;
        }
        // Save and create event
        saveHousekeeping(study, housekeeping);
        recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
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