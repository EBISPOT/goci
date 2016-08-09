package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by emma on 03/08/2016.
 *
 * @author emma
 *         <p>
 *         Service to update a study object
 */
@Service
public class StudyUpdateService {

    private StudyRepository studyRepository;
    private AttributeUpdateService attributeUpdateService;
    private TrackingOperationService trackingOperationService;

    @Autowired
    public StudyUpdateService(@Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                              StudyRepository studyRepository,
                              AttributeUpdateService attributeUpdateService) {
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
        this.attributeUpdateService = attributeUpdateService;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    /**
     * Update a study entry in the database
     *
     * @param existingStudyId ID of study being edited
     * @param study           Study to update
     * @param user            User performing request
     */
    public void updateStudy(Long existingStudyId, Study study, SecureUser user) {

        // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(existingStudyId);
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Check changes and record update details
        String updateDescription = generateUpdateDescription(existingStudy, study);

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we don't return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        trackingOperationService.update(study, user, EventType.STUDY_UPDATE, updateDescription);
        studyRepository.save(study);
        getLog().info("Study ".concat(String.valueOf(study.getId())).concat(" updated"));
    }

    /**
     * Generate a description of update made
     *
     * @param existingStudy Study based on whats stored in database
     * @param study         Updated study
     */
    private String generateUpdateDescription(Study existingStudy, Study study) {

        String updateDescription = null;
        List<String> updateDetails = new ArrayList<>();

        // Check disease trait reported by curator
        String existingDiseaseTraitName = null;
        if (existingStudy.getDiseaseTrait() != null && existingStudy.getDiseaseTrait().getTrait() != null) {
            existingDiseaseTraitName = existingStudy.getDiseaseTrait().getTrait();
        }

        String updatedDiseaseTraitName = null;
        if (study.getDiseaseTrait() != null && study.getDiseaseTrait().getTrait() != null) {
            updatedDiseaseTraitName = study.getDiseaseTrait().getTrait();
        }

        String diseaseTraitUpdateDescription =
                checkForDiseaseTraitUpdate(existingDiseaseTraitName, updatedDiseaseTraitName);

        if (diseaseTraitUpdateDescription != null) {
            updateDetails.add(diseaseTraitUpdateDescription);
        }

        StringJoiner updateDetailsJoiner = new StringJoiner(",");
        if (!updateDetails.isEmpty()) {
            updateDetails.forEach(s -> updateDetailsJoiner.add(s));
            updateDescription = updateDetailsJoiner.toString();
        }

        return updateDescription;
    }

    private String checkForDiseaseTraitUpdate(String existingDiseaseTraitName, String updatedDiseaseTraitName) {
        return attributeUpdateService.compareAttribute("Disease Trait",
                                                       existingDiseaseTraitName,
                                                       updatedDiseaseTraitName);
    }
}
