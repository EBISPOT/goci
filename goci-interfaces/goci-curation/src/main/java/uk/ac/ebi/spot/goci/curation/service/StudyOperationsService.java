package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.MappingDetails;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.CurationStatus;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.HousekeepingRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    /**
     * An additional date should be added to the "Curator information" tab called "Last automated mapping date". This
     * should record the last date all SNPs were mapped using the automated mapping pipeline i.e. when there has been an
     * Ensembl release. This should be left blank for studies where SNPs have different mapping dates or were mapped by
     * a curator, indicating that the curator should check the "SNP associations page" for last mapping info.
     *
     * @param study Study with mapping details
     */
    public MappingDetails createMappingSummary(Study study) {

        MappingDetails mappingSummary = new MappingDetails();
        Collection<Association> studyAssociations = associationRepository.findByStudyId(study.getId());

        // Determine if we have more than one performer
        Set<String> allAssociationMappingPerformers = new HashSet<String>();
        for (Association association : studyAssociations) {
            allAssociationMappingPerformers.add(association.getLastMappingPerformedBy());
        }

        Map<String, String> mappingDateToPerformerMap = new HashMap<>();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

        // If only one performer we need to check dates to see mapping didn't happen at different times
        if (allAssociationMappingPerformers.size() == 1) {
            String performer = allAssociationMappingPerformers.iterator().next();

            // Only care about automated mapping
            if (performer != null) {
                if (performer.equals("automatic_mapping_process") || performer.contains("Release")) {

                    // Go through all associations and store mapping performer and date
                    for (Association association : studyAssociations) {
                        String date = dt.format(association.getLastMappingDate());
                        mappingDateToPerformerMap.put(date, performer);
                    }
                }
            }
        }

        // If its only been mapped by an automated process, all with same date
        if (mappingDateToPerformerMap.size() == 1) {
            for (String date : mappingDateToPerformerMap.keySet()) {
                Date newDate = null;
                try {
                    newDate = dt.parse(date);

                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                mappingSummary.setMappingDate(newDate);
                mappingSummary.setPerformer(mappingDateToPerformerMap.get(date));
            }
        }
        return mappingSummary;
    }
}
