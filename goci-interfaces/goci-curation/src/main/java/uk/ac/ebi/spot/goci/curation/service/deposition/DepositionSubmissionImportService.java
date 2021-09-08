package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.curation.service.mail.MailService;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionAssociationDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionStudyDto;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSubmission;
import uk.ac.ebi.spot.goci.model.deposition.Submission;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepositionSubmissionImportService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    private CuratorRepository curatorRepository;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private DepositionStudyService depositionStudyService;

    @Autowired
    private IngestService ingestService;

    @Autowired
    private AssociationValidationService associationValidationService;

    @Autowired
    private PublicationExtensionRepository publicationExtensionRepository;

    @Autowired
    private BodyOfWorkRepository bodyOfWorkRepository;

    @Autowired
    private UnpublishedStudyRepository unpublishedRepository;

    @Autowired
    private UnpublishedAncestryRepository unpublishedAncestryRepo;

    @Autowired
    private StudiesProcessingService studiesProcessingService;

    @Autowired
    private MailService mailService;

    @Autowired
    private SubmissionImportProgressService submissionImportProgressService;

    @Autowired
    private StudyService studyService;

    @Async
    @Transactional
    public void importSubmission(DepositionSubmission depositionSubmission, SecureUser currentUser, Long submissionImportId) {
        ImportLog importLog = new ImportLog();
        getLog().info("Evaluating submission type for: {}", depositionSubmission.getSubmissionId());
        Submission.SubmissionType submissionType = DepositionUtil.getSubmissionType(depositionSubmission);
        getLog().info("Found submission type for: {}", submissionType.name());

        Curator curator = curatorRepository.findByEmail(currentUser.getEmail());
        String submissionID = depositionSubmission.getSubmissionId();

        getLog().info("[{}] Retrieving publication: {}", submissionID, depositionSubmission.getPublication().getPmid());
        Publication publication = publicationService.findByPumedId(depositionSubmission.getPublication().getPmid());
        getLog().info("[{}] Found publication: {}", submissionID, publication.getPubmedId());

        getLog().info("[{}] Looking for studies in the local DB ...", submissionID);
        Collection<Study> dbStudies = studyService.findByPublication(depositionSubmission.getPublication().getPmid());
        List<Long> dbStudyIds = dbStudies.stream().map(Study::getId).collect(Collectors.toList());
        getLog().info("[{}] Found {} studies: {}", submissionID, dbStudies.size(), dbStudyIds);

        List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<String> gcsts = studies.stream().map(DepositionStudyDto::getAccession).collect(Collectors.toList());
        getLog().info("[{}] Found {} studies in the submission retrieved from the Deposition App: {}", submissionID, studies.size(), gcsts);

        boolean outcome = true;

        if (submissionType == Submission.SubmissionType.SUM_STATS) { //if submission type is SUM_STATS only
            getLog().info("[{}] Found SUM_STATS submission.", submissionID, studies.size());
            ImportLogStep importStep = importLog.addStep(new ImportLogStep("Publishing summary stats", submissionID));
            Pair<Boolean, List<String>> result = depositionStudyService.publishSummaryStats(studies, dbStudies);
            if (result.getLeft()) {
                importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                importLog.addErrors(result.getRight(), "Publishing summary stats");

                importStep = importLog.addStep(new ImportLogStep("Updating submission status: CURATION_COMPLETE", submissionID));
                String stepOutcome = ingestService.updateSubmissionStatus(depositionSubmission, "CURATION_COMPLETE", "PUBLISHED_WITH_SS");
                if (stepOutcome != null) {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS_WITH_WARNINGS);
                    importLog.addWarning(stepOutcome, "Updating submission status: CURATION_COMPLETE");
                } else {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                }
            } else {
                importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                importLog.addErrors(result.getRight(), "Publishing summary stats");
                outcome = false;
            }
        } else {
            ImportLogStep studiesStep = importLog.addStep(new ImportLogStep("Verifying studies", submissionID));
            if (studies != null) {// && dbStudies.size() == 1) { //only do this for un-curated publications
                importLog.updateStatus(studiesStep.getId(), ImportLog.SUCCESS);
                getLog().info("[{}] Deleting proxy studies created when the publication was initially imported.", submissionID);
                ImportLogStep importStep = importLog.addStep(new ImportLogStep("Deleting proxy studies", submissionID));
                String result = depositionStudyService.deleteStudies(dbStudies, curator, currentUser);
                if (result != null) {
                    importLog.addError(result, "Deleting proxy studies");
                    importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                    outcome = false;
                } else {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                }

                if (outcome) {
                    outcome = studiesProcessingService.processStudies(depositionSubmission, currentUser, publication, curator, importLog);

                    if (outcome) {
                        getLog().info("[{}] Deleting unpublished studies and body of works.", submissionID);
                        importStep = importLog.addStep(new ImportLogStep("Deleting unpublished data", submissionID));
                        result = cleanupPrePublishedStudies(studies);
                        if (result != null) {
                            importLog.addWarning(result, "Deleting unpublished data");
                            importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS_WITH_WARNINGS);
                        } else {
                            importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                        }

                        importStep = importLog.addStep(new ImportLogStep("Updating submission status: CURATION_COMPLETE", submissionID));
                        result = ingestService.updateSubmissionStatus(depositionSubmission, "CURATION_COMPLETE", "CURATION_STARTED");
                        if (result != null) {
                            importLog.addWarning(result, "Updating submission status: CURATION_COMPLETE");
                            importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS_WITH_WARNINGS);
                        } else {
                            importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                        }
                    }
                }
            } else {
                importLog.updateStatus(studiesStep.getId(), ImportLog.FAIL);
                importLog.addError("Submission [" + submissionID + "] has no studies", "Verifying studies");
                outcome = false;
            }
        }

        if (outcome) {
            if (depositionSubmission.getPublication().getCorrespondingAuthor() != null) {
                getLog().info("Creating Publication extensions for corresponding authors ...");
                PublicationExtension author = new PublicationExtension();
                author.setCorrespondingAuthorEmail(depositionSubmission.getPublication().getCorrespondingAuthor().getEmail());
                if (depositionSubmission.getPublication().getCorrespondingAuthor().getGroup() != null) {
                    author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getGroup());
                } else {
                    author.setCorrespondingAuthorName(depositionSubmission.getPublication().getCorrespondingAuthor().getFirstName() + ' ' + depositionSubmission.getPublication().getCorrespondingAuthor().getLastName());
                }
                publicationExtensionRepository.save(author);
                getLog().info("Publication extension created: {}", author.getId());
                List<PublicationExtension> authorList = new ArrayList<>();
                authorList.add(author);
                publication.setCorrespondingAuthors(authorList);
                publicationService.save(publication);
                getLog().info("Publication [{}] saved.", publication.getPubmedId());
            }
        } else {
            ImportLogStep importStep = importLog.addStep(new ImportLogStep("Updating submission status: IMPORT_FAILED", submissionID));
            String stepOutcome = ingestService.updateSubmissionStatus(depositionSubmission, "IMPORT_FAILED", "");
            if (stepOutcome != null) {
                importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                importLog.addError(stepOutcome, "Updating submission status: IMPORT_FAILED");
            } else {
                importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
            }
        }

        mailService.sendSubmissionImportNotification(outcome, depositionSubmission.getPublication().getPmid(),
                submissionID, importLog, currentUser.getEmail());

        getLog().info("Import process finalized: {}", depositionSubmission.getSubmissionId());
        getLog().info(importLog.pretty(false));

        submissionImportProgressService.deleteImport(submissionImportId);
    }

    private String cleanupPrePublishedStudies(List<DepositionStudyDto> studyDtoList) {
        try {
            studyDtoList.forEach(studyDto -> {
                UnpublishedStudy unpublishedStudy = unpublishedRepository.findByAccession(studyDto.getAccession());
                if (unpublishedStudy != null) {
                    Collection<BodyOfWork> bodyOfWorks = unpublishedStudy.getBodiesOfWork();
                    Collection<UnpublishedAncestry> ancestries = unpublishedStudy.getAncestries();
                    unpublishedAncestryRepo.delete(ancestries);
                    Set<UnpublishedStudy> referencedStudies = new HashSet<>();
                    bodyOfWorks.forEach(bodyOfWork -> {
                        bodyOfWork.getStudies().forEach(study -> {
                            referencedStudies.add(study);
                        });
                    });
                    if (referencedStudies.size() <= 1) {
                        bodyOfWorkRepository.delete(bodyOfWorks);
                    }
                    unpublishedRepository.delete(unpublishedStudy);
                }
            });
        } catch (Exception e) {
            getLog().error("Encountered error: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }

        return null;
    }

}
