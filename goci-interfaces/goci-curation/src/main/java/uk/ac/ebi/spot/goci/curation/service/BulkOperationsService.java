package uk.ac.ebi.spot.goci.curation.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Publication;
import uk.ac.ebi.spot.goci.model.SecureUser;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.AssociationService;
import uk.ac.ebi.spot.goci.service.TrackingOperationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BulkOperationsService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CheckEfoTermAssignmentService checkEfoTermAssignmentService;

    @Autowired
    private AssociationOperationsService associationOperationsService;

    @Autowired
    private CheckMappingService checkMappingService;

    @Autowired
    private AssociationService associationService;

    @Autowired
    @Qualifier("studyTrackingOperationServiceImpl")
    private TrackingOperationService trackingOperationService;

    public void flipUserRequested(Publication publication, SecureUser user) {
        Pair<Boolean, Boolean> existingStatus = getFlagStatus(publication);
        boolean existingUserRequested = existingStatus.getRight();
        boolean newUserRequested = !existingUserRequested;

        getLog().info("[{}] Changing 'User Requested' flag for {} studies", publication.getPubmedId(), publication.getStudies().size());
        List<Long> ids = new ArrayList<>();
        for (Study study : publication.getStudies()) {
            ids.add(study.getId());
        }

        for (Long studyId : ids) {
            Study existingStudy = studyRepository.findOne(studyId);
            existingStudy.setUserRequested(newUserRequested);
            String updateDescription = "Changed 'User Requested' flag to: " + Boolean.toString(newUserRequested);
            trackingOperationService.update(existingStudy, user, "STUDY_UPDATE", updateDescription);
            studyRepository.save(existingStudy);
            getLog().info("Study ".concat(String.valueOf(studyId)).concat(" updated"));
        }
    }

    public void flipOpenTargets(Publication publication, SecureUser user) {
        Pair<Boolean, Boolean> existingStatus = getFlagStatus(publication);
        boolean existingOpenTargets = existingStatus.getLeft();
        boolean newOpenTargets = !existingOpenTargets;

        getLog().info("[{}] Changing 'Open Targets' flag for {} studies", publication.getPubmedId(), publication.getStudies().size());
        List<Long> ids = new ArrayList<>();
        for (Study study : publication.getStudies()) {
            ids.add(study.getId());
        }
        for (Long studyId : ids) {
            Study existingStudy = studyRepository.findOne(studyId);
            existingStudy.setOpenTargets(newOpenTargets);
            String updateDescription = "Changed 'Open Targets' flag to: " + Boolean.toString(newOpenTargets);
            trackingOperationService.update(existingStudy, user, "STUDY_UPDATE", updateDescription);
            studyRepository.save(existingStudy);
            getLog().info("Study ".concat(String.valueOf(studyId)).concat(" updated"));
        }
    }

    public List<String> approveAssociations(Study study, SecureUser user) {
        getLog().info("Approving {} associations for study: {}", study.getAssociations().size(), study.getId());
        List<String> messages = new ArrayList<>();
        for (Association association : study.getAssociations()) {
            // Check if association has an EFO trait
            Boolean associationEfoTermsAssigned =
                    checkEfoTermAssignmentService.checkAssociationEfoAssignment(association);
            if (!associationEfoTermsAssigned) {
                messages.add("[" + study.getId() + "] Association: " + association.getId() + "has no EFO trait assigned.");
            } else {
                Boolean associationMappingAssigned = checkMappingService.checkAssociationMappingAssignment(association);
                if (!associationMappingAssigned) {
                    messages.add("[" + study.getId() + "] Association: " + association.getId() + "has no Mapping assigned.");
                } else {
                    associationOperationsService.approveAssociation(association, user);
                }
            }
        }

        return messages;
    }

    public Pair<Boolean, Boolean> getFlagStatus(Publication publication) {
        boolean openTargets = true;
        boolean userRequested = true;

        boolean otFound = false;
        boolean urFound = false;

        for (Study study : publication.getStudies()) {
            if (study.getOpenTargets() != null) {
                otFound = true;
                if (!study.getOpenTargets()) {
                    openTargets = false;
                }
            }
            if (study.getUserRequested() != null) {
                urFound = true;
                if (!study.getUserRequested()) {
                    userRequested = false;
                }
            }
        }

        if (!otFound) {
            openTargets = false;
        }
        if (!urFound) {
            userRequested = false;
        }

        return Pair.of(openTargets, userRequested);
    }
}
