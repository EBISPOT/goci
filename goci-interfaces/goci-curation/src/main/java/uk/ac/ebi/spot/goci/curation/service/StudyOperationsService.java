package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.Assignee;
import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;
import uk.ac.ebi.spot.goci.curation.model.errors.ErrorNotification;
import uk.ac.ebi.spot.goci.curation.model.errors.NoteIsLockedError;
import uk.ac.ebi.spot.goci.curation.model.errors.StudyIsLockedError;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.CurationStatusRepository;
import uk.ac.ebi.spot.goci.repository.CuratorRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.*;
import uk.ac.ebi.spot.goci.utils.EuropePMCData;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

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
    private StudyNoteService studyNoteService;
    private StudyNoteOperationsService studyNoteOperationsService;
    private CuratorService curatorService;
    private PublicationService publicationService;

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
                                  HousekeepingOperationsService housekeepingOperationsService,
                                  StudyNoteService studyNoteService,
                                  StudyNoteOperationsService studyNoteOperationsService,
                                  CuratorService curatorService,
                                  PublicationService publicationService
    ) {
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
        this.studyNoteService = studyNoteService;
        this.studyNoteOperationsService=studyNoteOperationsService;
        this.curatorService=curatorService;
        this.publicationService = publicationService;
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

                        //the old note in house keeping comment out.
//                        String notes = housekeeping.getNotes();
//                        housekeeping.setNotes(notes.concat("****").concat(republish_message));

                        StudyNote note = studyNoteOperationsService.createAutomaticNote(republish_message,study,user);
                        // The note is properly created. We don't need to check any business logic. Just link to the study.
                        studyNoteService.saveStudyNote(note);
                        study.addNote(note);

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


    public ErrorNotification addStudyNote(Study study, StudyNote studyNote, SecureUser user)  {

        ErrorNotification notification = new ErrorNotification();

        //xintodo need to refactor after removing curator table
        Curator curator = curatorService.getCuratorIdByEmail(user.getEmail());
        studyNote.setCurator(curator);

        //user cannot touch system notes
        if (studyNoteOperationsService.isSystemNote(studyNote)){
            notification.addError(new NoteIsLockedError());
        }

        //published study can only have private note added to it
        //This is comment out because curators want to add public note to published studies.
//        if(isPublished(study) & studyNoteOperationsService.isPublicNote(studyNote)){
//            notification.addError(new PublicNoteIsNotAllowedForPublishedStudyError());
//        }

        //check if study is published
//        if(isPublished(study)){
//            notification.addError(new StudyIsLockedError());
//        }

        if(!notification.hasErrors()){
            studyNoteService.saveStudyNote(studyNote);
        }
        return notification;
    }

    public ErrorNotification deleteStudyNote(Study study, StudyNote studyNote) {

        ErrorNotification notification = new ErrorNotification();

        //user cannot touch system notes
        if (studyNoteOperationsService.isSystemNote(studyNote)){
            notification.addError(new NoteIsLockedError());
        }

        //check if study is published
        if(isPublished(study)){
            notification.addError(new StudyIsLockedError());
        }

        if(!notification.hasErrors()){
            studyNoteService.deleteStudyNote(studyNote);
        }

        return notification;
    }

    public ErrorNotification duplicateStudyNoteToSiblingStudies(Study sourceStudy,Long nodeId,SecureUser user){
        //find all studies with the same pubmed id
        // THOR
        Collection<Study> studies = publicationService.findStudiesByPubmedId(sourceStudy.getPublicationId().getPubmedId());
        //remove the source study
        studies = studies.stream().filter(targetStudy -> sourceStudy.getId() != targetStudy.getId()).collect(Collectors.toList());

        //find the note
        StudyNote noteToCopy = studyNoteService.findOne(nodeId);

        ErrorNotification notification = new ErrorNotification();


        //copy note to studies
        studies.stream().forEach(targetStudy -> {
            ErrorNotification en = addStudyNote(targetStudy, studyNoteOperationsService.duplicateNote(targetStudy, noteToCopy, user), user);
            if(en.hasErrors()){
                notification.addError(en.getErrors());
            }
        });

        studyNoteOperationsService.updateDuplicatedNote(noteToCopy, user);
        return notification;
    }




    //#xintodo refactor needed
    public Boolean isPublished(Study study){
        return study.getHousekeeping().getIsPublished();
    }
}