package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
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
public class StudyService {

    private StudyRepository studyRepository;
    private AssociationRepository associationRepository;
    private MailService mailService;

    @Autowired
    public StudyService(StudyRepository studyRepository, AssociationRepository associationRepository, MailService mailService) {
        this.studyRepository = studyRepository;
        this.associationRepository = associationRepository;
        this.mailService = mailService;
    }

    public String updateStatus(CurationStatus status, Study study) {

        String message = null;

        // For the study check all SNPs have been checked
        Collection<Association> associations = associationRepository.findByStudyId(study.getId());
        int snpsNotChecked = 0;
        for (Association association : associations) {
            // If we have one that is not checked set value
            if (!association.getSnpChecked()) {
                snpsNotChecked = 1;
            }
        }

        CurationStatus currentStudyStatus = study.getHousekeeping().getCurationStatus();

        // If the status has changed
        if (status != currentStudyStatus) {
            if (status != null && status.getStatus().equals("Publish study")) {

                // If not checked redirect back to page and make no changes
                if (snpsNotChecked == 1) {
                    message = "Some SNP associations have not been checked, please review before publishing";
                } else {
                    // If there is no existing publish date then update
                    if (study.getHousekeeping().getCatalogPublishDate() == null) {
                        Date publishDate = new Date();
                        study.getHousekeeping().setCatalogPublishDate(publishDate);
                        study.getHousekeeping().setCurationStatus(status);
                    }
                }
            }

            //Set date and send email notification
            if (status != null && status.getStatus().equals("Send to NCBI")) {
                // If not checked redirect back to page and make no changes
                if (snpsNotChecked == 1) {
                    message = "Some SNP associations have not been checked, please review before sending to NCBI";
                } else {
                    Date sendToNCBIDate = new Date();
                    study.getHousekeeping().setSendToNCBIDate(sendToNCBIDate);
                    mailService.sendEmailNotification(study, status.getStatus());
                    study.getHousekeeping().setCurationStatus(status);
                }
            }

            // Send notification email to curators
            if (status != null && status.getStatus().equals("Level 1 curation done")) {
                mailService.sendEmailNotification(study, status.getStatus());
                study.getHousekeeping().setCurationStatus(status);
            }
        }

        // Save our study if no errors
        if (message != null) {
            studyRepository.save(study);
        }

        return message;
    }
}
