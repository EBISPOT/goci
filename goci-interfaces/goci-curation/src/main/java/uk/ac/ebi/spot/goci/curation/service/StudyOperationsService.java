package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.UnpublishReason;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.EventTypeService;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

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
    private EventTypeService eventTypeService;
    private HousekeepingOperationsService housekeepingOperationsService;

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository,
                                  PublishStudyCheckService publishStudyCheckService,
                                  StudyRepository studyRepository,
                                  CuratorRepository curatorRepository,
                                  CurationStatusRepository curationStatusRepository,
                                  @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                  EventTypeService eventTypeService,
                                  HousekeepingOperationsService housekeepingOperationsService) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
        this.publishStudyCheckService = publishStudyCheckService;
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.trackingOperationService = trackingOperationService;
        this.eventTypeService = eventTypeService;
        this.housekeepingOperationsService = housekeepingOperationsService;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Create a study entry in the database
     *
     * @param study Study to save
     * @param user  User preforming request
     * @return study
     */
    public Study createStudy(Study study, SecureUser user) {
        study.setHousekeeping(housekeepingOperationsService.createHousekeeping());
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

        // If the current and new status are different
        if (newStatus != null && newStatus != currentStudyStatus) {

            // Get housekeeping object and assign new status
            Housekeeping housekeeping = study.getHousekeeping();

            if (newStatus.getStatus().equals("Publish study")) {
                // Run pre-publish checks first
                Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                message = publishStudyCheckService.runChecks(study, associations);

                // if checks pass then update the status and save objects
                if (message == null) {
                    housekeeping.setCurationStatus(newStatus);
                    updateStatus(study, housekeeping, userFromRequest);
                }
            }
            else {
                housekeeping.setCurationStatus(newStatus);
                updateStatus(study, housekeeping, userFromRequest);
            }
        }
        else {
            message = "Current status and new status are the same, no change required";
        }
        return message;
    }

    /**
     * Assign curator to a study
     */
    public void assignStudyCurator(Study study, Assignee assignee, SecureUser user) {

        Long curatorId = assignee.getCuratorId();
        Curator curator = curatorRepository.findOne(curatorId);

        // Set new curator on the study housekeeping
        Housekeeping housekeeping = study.getHousekeeping();
        housekeeping.setCurator(curator);
        housekeepingOperationsService.saveHousekeeping(study, housekeeping);

        // Add event
        recordStudyCuratorChange(study, user, curator);
    }

    /**
     * Update housekeeping
     */
    public String updateHousekeeping(Housekeeping housekeeping, Study study, SecureUser user) {

        CurationStatus newStatus = housekeeping.getCurationStatus();
        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();

        Curator newCurator = housekeeping.getCurator();
        Curator currentCurator = study.getHousekeeping().getCurator();

        // If curator has changed, record the curator change event
        if (newCurator != null && newCurator != currentCurator) {
            recordStudyCuratorChange(study, user, newCurator);
        }

        // If the current and new status are different
        String message = null;
        if (newStatus != null && newStatus != currentStudyStatus) {
            if (newStatus.getStatus().equals("Publish study")) {

                // Run pre-publish checks first
                Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                message = publishStudyCheckService.runChecks(study, associations);

                // if checks pass then update the status
                if (message == null) {
                    updateStatus(study, housekeeping, user);
                }
                // restore old status
                else {
                    housekeeping.setCurationStatus(currentStudyStatus);
                    housekeepingOperationsService.saveHousekeeping(study, housekeeping);
                }
            }
            else {
                updateStatus(study, housekeeping, user);
            }
        }
        else {
            // Save housekeeping returned from form
            housekeepingOperationsService.saveHousekeeping(study, housekeeping);
        }
        return message;
    }

    /**
     * Unpublish a study entry in the database
     *
     * @param studyId         ID of study to unpublish
     * @param unpublishReason Reason the study is being unpublished
     * @param user            User performing request
     */
    public void unpublishStudy(Long studyId, UnpublishReason unpublishReason, SecureUser user) {

        Study study = studyRepository.findOne(studyId);

        // Before we unpublish the study get its associated housekeeping
        Long housekeepingId = study.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);

        //Set the unpublishDate and a new lastUpdateDate in houskeeping
        java.util.Date unpublishDate = new java.util.Date();
        housekeepingAttachedToStudy.setCatalogUnpublishDate(unpublishDate);
        housekeepingAttachedToStudy.setLastUpdateDate(unpublishDate);

        //Set the reason for unpublishing
        housekeepingAttachedToStudy.setUnpublishReason(unpublishReason);

        housekeepingAttachedToStudy.setIsPublished(false);

        //Set the unpublised status in housekeeping
        CurationStatus status = curationStatusRepository.findByStatus("Unpublished from catalog");
        housekeepingAttachedToStudy.setCurationStatus(status);
        updateStatus(study, housekeepingAttachedToStudy, user);
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
        String eventType = eventTypeService.determineEventTypeFromStatus(newStatus);
        trackingOperationService.update(study, user, eventType);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" status updated"));
    }

    /**
     * Record a study curator change
     *
     * @param curator New curator to apply to study
     * @param study   Study to update
     * @param user    User preforming request
     */
    private void recordStudyCuratorChange(Study study, SecureUser user, Curator curator) {

        // Create study event
        String eventType = eventTypeService.determineEventTypeFromCurator(curator);
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
                if(!housekeeping.getIsPublished()){
                    housekeeping.setIsPublished(true);

                    if (housekeeping.getCatalogPublishDate() == null) {
                        Date publishDate = new Date();
                        housekeeping.setCatalogPublishDate(publishDate);
                    }

                    if (housekeeping.getCatalogUnpublishDate() != null && housekeeping.getUnpublishReason() != null){
                        String republish_message = "Study unpublished on "
                                + housekeeping.getCatalogUnpublishDate().toString()
                                + " for reason "
                                + housekeeping.getUnpublishReason().getReason()
                                + " and republished on "
                                + new Date().toString();

                        String notes = housekeeping.getNotes();

                        housekeeping.setNotes(notes.concat("****").concat(republish_message));
                        housekeeping.setCatalogUnpublishDate(null);
                        housekeeping.setUnpublishReason(null);
                    }
                }


                // Save and create event
                housekeepingOperationsService.saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());

                // Send notification email to curators
                getLog().info("Sending email...");
                mailService.sendEmailNotification(study, "Publish study");
                break;

            case "Level 1 curation done":
                // Send notification email to curators
                getLog().info("Sending email...");
                mailService.sendEmailNotification(study, "Level 1 curation done");

                // Save and create event
                housekeepingOperationsService.saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
            case "Level 2 curation done":
                // Send notification email to curators
                getLog().info("Sending email... Level 2 curation Done");
                mailService.sendEmailNotification(study, "Level 2 curation done");

                // Save and create event
                housekeepingOperationsService.saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
            default:
                // Save and create event
                housekeepingOperationsService.saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
        }
    }
}