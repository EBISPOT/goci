package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.curation.service.tracking.EventTypeService;
import uk.ac.ebi.spot.goci.curation.service.tracking.TrackingOperationService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Ethnicity;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.EthnicityRepository;
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
    private EthnicityRepository ethnicityRepository;
    private EventTypeService eventTypeService;

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository,
                                  PublishStudyCheckService publishStudyCheckService,
                                  StudyRepository studyRepository,
                                  CuratorRepository curatorRepository,
                                  CurationStatusRepository curationStatusRepository,
                                  @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                                  EthnicityRepository ethnicityRepository,
                                  EventTypeService eventTypeService) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
        this.publishStudyCheckService = publishStudyCheckService;
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.trackingOperationService = trackingOperationService;
        this.ethnicityRepository = ethnicityRepository;
        this.eventTypeService = eventTypeService;
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
        study.setHousekeeping(createHousekeeping());
        trackingOperationService.create(study, user);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" created"));
        return study;
    }

    /**
     * Update a study entry in the database
     *
     * @param existingStudyId ID of study being edited
     * @param study           Study to update
     * @param user            User preforming request
     */
    public void updateStudy(Long existingStudyId, Study study, SecureUser user) {

        // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(existingStudyId);
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we don't return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        trackingOperationService.update(study, user, EventType.STUDY_UPDATE);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" updated"));
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
        saveHousekeeping(study, housekeeping);

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
                    saveHousekeeping(study, housekeeping);
                }
            }
            else {
                updateStatus(study, housekeeping, user);
            }
        }
        else {
            // Save housekeeping returned from form
            saveHousekeeping(study, housekeeping);
        }
        return message;
    }

    /**
     * Update a study status
     *
     * @param study Study to delete
     */
    public void deleteStudy(Study study, SecureUser user) {

        // Before we delete the study get its associated housekeeping and ethnicity
        Long housekeepingId = study.getHousekeeping().getId();
        Housekeeping housekeepingAttachedToStudy = housekeepingRepository.findOne(housekeepingId);
        Collection<Ethnicity> ethnicitiesAttachedToStudy = ethnicityRepository.findByStudyId(study.getId());

        // Delete ethnicity information linked to this study
        for (Ethnicity ethnicity : ethnicitiesAttachedToStudy) {
            ethnicityRepository.delete(ethnicity);
        }

        // Delete study
        studyRepository.delete(study);

        // Delete housekeeping
        housekeepingRepository.delete(housekeepingAttachedToStudy);
        trackingOperationService.delete(study, user);
    }

    /**
     * Save housekeepoing
     *
     * @param housekeeping
     * @param study
     */
    private void saveHousekeeping(Study study, Housekeeping housekeeping) {
        // Save housekeeping returned from form
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
        EventType eventType = eventTypeService.determineEventTypeFromStatus(newStatus);
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
        EventType eventType = eventTypeService.determineEventTypeFromCurator(curator);
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

                // Save and create event
                saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
            // Send notification email to curators
            case "Level 1 curation done":
                mailService.sendEmailNotification(study, "Level 1 curation done");

                // Save and create event
                saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
            default:

                // Save and create event
                saveHousekeeping(study, housekeeping);
                recordStudyStatusChange(study, user, housekeeping.getCurationStatus());
                break;
        }
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
}