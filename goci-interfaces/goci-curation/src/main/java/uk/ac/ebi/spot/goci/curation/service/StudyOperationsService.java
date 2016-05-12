package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.exception.NoStudyDirectoryException;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Curator;
import uk.ac.ebi.spot.goci.model.Event;
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
    private EventOperationsService eventOperationsService;

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository,
                                  PublishStudyCheckService publishStudyCheckService,
                                  StudyRepository studyRepository,
                                  CuratorRepository curatorRepository,
                                  CurationStatusRepository curationStatusRepository,
                                  EventOperationsService eventOperationsService) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
        this.publishStudyCheckService = publishStudyCheckService;
        this.studyRepository = studyRepository;
        this.curatorRepository = curatorRepository;
        this.curationStatusRepository = curationStatusRepository;
        this.eventOperationsService = eventOperationsService;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Update a study status
     *
     * @param study Study to save
     * @return ID of study to save
     */
    public Study saveStudy(Study study, SecureUser user) {

        // Update and save study
        study.setHousekeeping(createHousekeeping());
        Event studyCreationEvent = eventOperationsService.createEvent(EventType.STUDY_CREATION, user);
        study.addEvent(studyCreationEvent);
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
     */
    public String updateStatus(CurationStatus newStatus, Study study, CurationStatus currentStudyStatus) {

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
        }
        return message;
    }

    /**
     * Create study housekeeping
     *
     * @return Housekeeping object
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
