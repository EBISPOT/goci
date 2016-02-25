package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;

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

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository,
                                  PublishStudyCheckService publishStudyCheckService) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
        this.publishStudyCheckService = publishStudyCheckService;
    }

    /**
     * Update a studies status
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
}
