package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.InitialSampleDescription;
import uk.ac.ebi.spot.goci.curation.model.ReplicationSampleDescription;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 *         <p>
 *         Service to update initialSampleSize and replicateSampleSize attributes of study object. The tracking
 *         information is added to the study object, as these are attributes of a study.
 */
@Service
public class StudySampleDescriptionService {

    private StudyRepository studyRepository;
    private AttributeUpdateService attributeUpdateService;
    private TrackingOperationService trackingOperationService;

    @Autowired
    public StudySampleDescriptionService(StudyRepository studyRepository,
                                         AttributeUpdateService attributeUpdateService,
                                         @Qualifier("studyTrackingOperationServiceImpl") TrackingOperationService trackingOperationService) {
        this.studyRepository = studyRepository;
        this.attributeUpdateService = attributeUpdateService;
        this.trackingOperationService = trackingOperationService;
    }

    public void addStudyInitialReplcationSampleDescription(Long studyId,
                                                           InitialSampleDescription initialSampleDescription,
                                                           ReplicationSampleDescription replicationSampleDescription,
                                                           SecureUser user) {


        // Find existing study to add details to
        Study study = studyRepository.findOne(studyId);

        // Check changes and record update details
        String updateDescription =
                generateUpdateDescription(initialSampleDescription.getInitialSampleDescription(),
                                          replicationSampleDescription.getReplicationSampleDescription(),
                                          study);

        // Set our descriptions which are attributes of the study
        study.setInitialSampleSize(initialSampleDescription.getInitialSampleDescription());
        study.setReplicateSampleSize(replicationSampleDescription.getReplicationSampleDescription());

        // Save study
        trackingOperationService.update(study, user, "STUDY_SAMPLE_DESCRIPTION_UPDATE", updateDescription);
        studyRepository.save(study);
    }

    /**
     * Generate a description of update made
     *
     * @param initialSampleDescription     New initial sample description
     * @param replicationSampleDescription New replicate sample description
     * @param study                        Updated study
     */
    private String generateUpdateDescription(String initialSampleDescription,
                                             String replicationSampleDescription,
                                             Study study) {

        String updateDescription = null;
        List<String> updateDetails = new ArrayList<>();

        String initialSampleDescriptionUpdateDescription =
                checkForSampleDescriptionUpdate("Initial Sample Description", study.getInitialSampleSize(),
                                                initialSampleDescription);

        if (initialSampleDescriptionUpdateDescription != null) {
            updateDetails.add(initialSampleDescriptionUpdateDescription);
        }

        String replicationSampleDescriptionUpdateDescription =
                checkForSampleDescriptionUpdate("Replication Sample Description",
                                                study.getReplicateSampleSize(),
                                                replicationSampleDescription);

        if (replicationSampleDescriptionUpdateDescription != null) {
            updateDetails.add(replicationSampleDescriptionUpdateDescription);
        }


        StringJoiner updateDetailsJoiner = new StringJoiner(",");
        if (!updateDetails.isEmpty()) {
            updateDetails.forEach(s -> updateDetailsJoiner.add(s));
            updateDescription = updateDetailsJoiner.toString();
        }

        return updateDescription;
    }

    private String checkForSampleDescriptionUpdate(String descriptionType, String existingSampleSize,
                                                   String newSampleDescription) {

        return attributeUpdateService.compareAttribute(descriptionType,
                                                       existingSampleSize,
                                                       newSampleDescription);
    }
}
