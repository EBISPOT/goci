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

    @Autowired
    public StudyOperationsService(AssociationRepository associationRepository,
                                  MailService mailService,
                                  HousekeepingRepository housekeepingRepository) {
        this.associationRepository = associationRepository;
        this.mailService = mailService;
        this.housekeepingRepository = housekeepingRepository;
    }

    /**
     * Update a studies status
     *
     * @param newStatus          New status to apply to study
     * @param study              Study to update
     * @param currentStudyStatus Current status of the study to update
     */
    public String updateStatus(CurationStatus newStatus, Study study, CurationStatus currentStudyStatus) {

        String message = null;
        Housekeeping housekeeping = study.getHousekeeping();

        // If the status has changed
        if (newStatus != null && newStatus != currentStudyStatus) {
            switch (newStatus.getStatus()) {
                case "Publish study":
                    // For the study check all SNPs have been checked
                    Collection<Association> associations = associationRepository.findByStudyId(study.getId());
                    int snpsNotApproved = studyAssociationCheck(associations);

                    if (snpsNotApproved == 1) {
                        message = "Some SNP associations have not been checked for study: "
                                + study.getAuthor() + ", "
                                + " pubmed = " + study.getPubmedId()
                                + ", please review before changing the status to "
                                + newStatus.getStatus();
                    }
                    else {
                        // If there is no existing publish date then update
                        if (study.getHousekeeping().getCatalogPublishDate() == null) {
                            Date publishDate = new Date();
                            housekeeping.setCatalogPublishDate(publishDate);
                        }
                        mailService.sendEmailNotification(study, newStatus.getStatus());
                        housekeeping.setCurationStatus(newStatus);
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
        }

        // If error was identified restore previous status
        if (message != null) {
            housekeeping.setCurationStatus(currentStudyStatus);
        }

        // Save changes
        housekeepingRepository.save(housekeeping);

        return message;
    }

    /**
     * Check SNPs have been approved
     *
     * @param associations All associations found for a study
     */
    public int studyAssociationCheck(Collection<Association> associations) {
        int snpsNotApproved = 0;
        for (Association association : associations) {
            // If we have one that is not checked set value
            if (!association.getSnpApproved()) {
                snpsNotApproved = 1;
            }
        }

        return snpsNotApproved;
    }
}
