package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Housekeeping;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyExtension;
import uk.ac.ebi.spot.goci.repository.StudyExtensionRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by emma on 03/08/2016.
 *
 * @author emma
 * <p>
 * Service to update a study object
 */
@Service
public class StudyUpdateService {

    private StudyExtensionRepository studyExtensionRepository;
    private StudyRepository studyRepository;
    private AttributeUpdateService attributeUpdateService;
    private TrackingOperationService trackingOperationService;

    @Autowired
    public StudyUpdateService(@Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService,
                              StudyRepository studyRepository,
                              AttributeUpdateService attributeUpdateService,
                              StudyExtensionRepository studyExtensionRepository) {
        this.trackingOperationService = trackingOperationService;
        this.studyRepository = studyRepository;
        this.attributeUpdateService = attributeUpdateService;
        this.studyExtensionRepository = studyExtensionRepository;
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
    public void updateStudy(Long existingStudyId, Study study, StudyExtension studyExtension, SecureUser user) {

        // Use id in URL to get study and then its associated housekeeping
        Study existingStudy = studyRepository.findOne(existingStudyId);
        Housekeeping existingHousekeeping = existingStudy.getHousekeeping();

        // Check changes and record update details
        String updateDescription = generateUpdateDescription(existingStudy, study);

        // Set the housekeeping of the study returned to one already linked to it in database
        // Need to do this as we don't return housekeeping in form
        study.setHousekeeping(existingHousekeeping);

        //study.getStudyExtension().setStudy(study);
        trackingOperationService.update(study, user, "STUDY_UPDATE", updateDescription);

        if (studyExtension != null) {
            // Use Custom query "getStudyExtensionId" to get the ID of the StudyExtension since "studyExtension" passed
            // by the study.html form returns "id" as the id of the Study object and not the StudyExtension
            // and the default "find" query syntax then returns this study.id
            StudyExtension existing = studyExtensionRepository.getStudyExtensionId(study.getId());

            if (existing != null) {
                existing.setStudyDescription(studyExtension.getStudyDescription());
                existing.setCohort(studyExtension.getCohort());
                existing.setCohortSpecificReference(studyExtension.getCohortSpecificReference());
                existing.setStatisticalModel(studyExtension.getStatisticalModel());
                existing.setSummaryStatisticsFile(studyExtension.getSummaryStatisticsFile());
                existing.setSummaryStatisticsAssembly(studyExtension.getSummaryStatisticsAssembly());
                studyExtensionRepository.save(existing);
            } else {
                studyExtensionRepository.save(studyExtension);
            }
        }
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

        // Check disease trait for changes
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

        // Check EFO trait for changes
        List<String> existingStudyEfoTraits = new ArrayList<>();
        List<String> updatedStudyEfoTraits = new ArrayList<>();

        existingStudyEfoTraits = existingStudy.getEfoTraits()
                .stream()
                .filter(efoTrait -> efoTrait.getTrait() != null)
                .filter(efoTrait -> !efoTrait.getTrait().isEmpty())
                .map(efoTrait -> efoTrait.getTrait())
                .collect(Collectors.toList());


        if (study.getEfoTraits() != null) {
            updatedStudyEfoTraits = study.getEfoTraits()
                    .stream()
                    .filter(efoTrait -> !efoTrait.getTrait().isEmpty())
                    .map(efoTrait -> efoTrait.getTrait())
                    .collect(Collectors.toList());
        }

        String efoTraitUpdateDescription = checkForEfoTraitUpdate(existingStudyEfoTraits, updatedStudyEfoTraits);
        if (efoTraitUpdateDescription != null) {
            updateDetails.add(efoTraitUpdateDescription);
        }


        StringJoiner updateDetailsJoiner = new StringJoiner(", ");
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

    private String checkForEfoTraitUpdate(List<String> existingStudyEfoTraits, List<String> updatedStudyEfoTraits) {

        // Sort traits
        List<String> existingStudyEfoTraitsSorted =
                existingStudyEfoTraits.stream().sorted().collect(Collectors.toList());
        List<String> updatedStudyEfoTraitsSorted = updatedStudyEfoTraits.stream().sorted().collect(Collectors.toList());

        // Create a string version of each list of traits
        String existingStudyEfoTraitsAsString = null;
        if (!existingStudyEfoTraitsSorted.isEmpty()) {
            StringJoiner existingJoiner = new StringJoiner(", ");
            existingStudyEfoTraitsSorted.forEach(s -> existingJoiner.add(s));
            existingStudyEfoTraitsAsString = existingJoiner.toString();
        }

        String updatedStudyEfoTraitsAsString = null;
        if (!updatedStudyEfoTraitsSorted.isEmpty()) {
            StringJoiner updateJoiner = new StringJoiner(", ");
            updatedStudyEfoTraitsSorted.stream().forEach(s -> updateJoiner.add(s));
            updatedStudyEfoTraitsAsString = updateJoiner.toString();
        }

        return attributeUpdateService.compareAttribute("EFO Trait",
                existingStudyEfoTraitsAsString,
                updatedStudyEfoTraitsAsString);
    }
}
