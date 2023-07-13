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
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.model.deposition.util.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.PublicationService;
import uk.ac.ebi.spot.goci.service.StudyService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepositionSubmissionImportService {

    private final Logger log = LoggerFactory.getLogger(getClass());

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

    @Autowired
    private DepositionSubmissionService depositionSubmissionService;

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
        List<DepositionStudyDto> studyDtos = getStudies(depositionSubmission.getSubmissionId());
        //List<DepositionStudyDto> studies = depositionSubmission.getStudies();
        List<String> gcsts = studyDtos.stream().map(DepositionStudyDto::getAccession).collect(Collectors.toList());
        getLog().info("[{}] Found {} studies in the submission retrieved from the Deposition App: {}", submissionID, studyDtos.size(), gcsts);

        Boolean outcome =  true;


        if (submissionType == Submission.SubmissionType.SUM_STATS) { //if submission type is SUM_STATS only
            getLog().info("[{}] Found SUM_STATS submission.", submissionID);
            ImportLogStep importStep = importLog.addStep(new ImportLogStep("Publishing summary stats", submissionID));
            List<Pair<Boolean, List<String>>> results = updateSumstatsData(studyDtos, dbStudies);
                for(Pair<Boolean, List<String>> pair : results) {
                    if(pair.getLeft() != null && !pair.getLeft()) {
                        importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                        importLog.addErrors(pair.getRight(), "Publishing summary stats");
                        outcome = false;
                        break;
                    }
                    if(pair.getLeft() != null && pair.getLeft()) {
                        importLog.addErrors(pair.getRight(),"Publishing summary stats");
                    }
                }
            if(outcome) {
                importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                importStep = importLog.addStep(new ImportLogStep("Updating submission status: CURATION_COMPLETE", submissionID));
                String stepOutcome = ingestService.updateSubmissionStatus(depositionSubmission, "CURATION_COMPLETE", "PUBLISHED_WITH_SS");
                if (stepOutcome != null) {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS_WITH_WARNINGS);
                    importLog.addWarning(stepOutcome, "Updating submission status: CURATION_COMPLETE");
                } else {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                }
            }
        } else {
            ImportLogStep studiesStep = importLog.addStep(new ImportLogStep("Verifying studies", submissionID));
            if (studyDtos != null) { // && dbStudies.size() == 1) { //only do this for un-curated publications
                importLog.updateStatus(studiesStep.getId(), ImportLog.SUCCESS);
                getLog().info("[{}] Validating associations ...", submissionID);
                ImportLogStep importStep = importLog.addStep(new ImportLogStep("Validating associations", submissionID));
                List<DepositionAssociationDto> asscnDtos = getAssociations(depositionSubmission.getSubmissionId());
                List<DepositionSampleDto> sampleDtos = getSamples(depositionSubmission.getSubmissionId());
                for (DepositionStudyDto studyDto : studyDtos) {
                    if (asscnDtos != null) {
                        List<String> errors = associationValidationService.validateAssociations(studyDto.getStudyTag(), studyDto.getAccession(), asscnDtos);
                        importLog.addWarnings(errors, "Validating associations");
                    }
                }
                getLog().info("[{}] Associations validated. Found {} errors and {} warnings.", submissionID, importLog.getErrorList().size(),
                        importLog.getWarnings().size());
                if (!importLog.getErrorList().isEmpty()) {
                    importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                    outcome = false;
                } else {
                    importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                    getLog().info("[{}] Deleting proxy studies created when the publication was initially imported.", submissionID);
                    importStep = importLog.addStep(new ImportLogStep("Deleting proxy studies", submissionID));
                    String result = depositionStudyService.deleteStudies(dbStudies, curator, currentUser);
                    if (result != null) {
                        importLog.addError(result, "Deleting proxy studies");
                        importLog.updateStatus(importStep.getId(), ImportLog.FAIL);
                        outcome = false;
                    } else {
                        importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
                    }

                    if (outcome) {
                        outcome = studiesProcessingService.processStudies(depositionSubmission, currentUser, publication, curator, importLog, studyDtos, asscnDtos, sampleDtos);
                        if (outcome) {
                            getLog().info("[{}] Deleting unpublished studies and body of works.", submissionID);
                            importStep = importLog.addStep(new ImportLogStep("Deleting unpublished data", submissionID));
                            result = cleanupPrePublishedStudies(studyDtos);
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


    private List<DepositionStudyDto> getStudies(String submissionImportId) {
        DepositionStudyListWrapper depositionStudyListWrapper = null;
        List<DepositionStudyDto> studyDtos = new ArrayList<>();
        depositionStudyListWrapper = depositionSubmissionService.getSubmissionStudies("", submissionImportId);
        List<DepositionStudyDto> studies = buildStudiesList(depositionStudyListWrapper);
        if(studies != null) {
            studyDtos.addAll(studies);
        }
        Links links = depositionStudyListWrapper.getLinks();
        while(links != null && links.getNext() != null) {
            depositionStudyListWrapper = depositionSubmissionService.getSubmissionStudies(links.getNext().getHref(), submissionImportId);
            studies = buildStudiesList(depositionStudyListWrapper);
            if(studies != null) {
                studyDtos.addAll(studies);
            }
            links = depositionStudyListWrapper.getLinks();
        }
        return studyDtos;
    }

    private List<DepositionAssociationDto> getAssociations(String submissionImportId){
        DepositionAssociationListWrapper depositionAssociationListWrapper = null;
        List<DepositionAssociationDto> asscnDtos = new ArrayList<>();
        depositionAssociationListWrapper = depositionSubmissionService.getSubmissionAssociations("", submissionImportId);
        List<DepositionAssociationDto> asscns = buildAssociationList(depositionAssociationListWrapper);
        if(asscns != null) {
            asscnDtos.addAll(asscns);
        }
        Links asscnLinks = depositionAssociationListWrapper.getLinks();
        while(asscnLinks != null && asscnLinks.getNext() != null) {
            depositionAssociationListWrapper = depositionSubmissionService.getSubmissionAssociations(asscnLinks.getNext().getHref(), submissionImportId);
            asscns = buildAssociationList(depositionAssociationListWrapper);
            if(asscns != null) {
                asscnDtos.addAll(asscns);
            }
            asscnLinks = depositionAssociationListWrapper.getLinks();
        }
        return asscnDtos;
    }

    private List<DepositionSampleDto> getSamples(String submissionImportId) {
        DepositionSampleListWrapper depositionSampleListWrapper = null;
        List<DepositionSampleDto> sampleDtos = new ArrayList<>();
        depositionSampleListWrapper = depositionSubmissionService.getSubmissionSamples("", submissionImportId);
        List<DepositionSampleDto> samples = buildSamplesList(depositionSampleListWrapper);
        if(samples != null) {
            sampleDtos.addAll(samples);
        }
        Links sampleLinks = depositionSampleListWrapper.getLinks();
        while(sampleLinks != null && sampleLinks.getNext() != null) {
            depositionSampleListWrapper = depositionSubmissionService.getSubmissionSamples(sampleLinks.getNext().getHref(), submissionImportId);
            samples = buildSamplesList(depositionSampleListWrapper);
            if(samples != null) {
                sampleDtos.addAll(samples);
            }
            sampleLinks = depositionSampleListWrapper.getLinks();
        }
        return sampleDtos;
    }

    private List<Pair<Boolean, List<String>>>  updateSumstatsData(List<DepositionStudyDto> studyDtos, Collection<Study> dbStudies) {

        return studyDtos.stream().
                map(studyDTO -> depositionStudyService.publishSummaryStats(studyDTO, dbStudies)).collect(Collectors.toList());
    }

    private List<DepositionAssociationDto> buildAssociationList(DepositionAssociationListWrapper depositionAssociationListWrapper) {
        return Optional.ofNullable(depositionAssociationListWrapper.getAssociations())
                .map(DepositionAssociationList::getAssociations)
                .orElse(null);
    }

    private List<DepositionStudyDto> buildStudiesList(DepositionStudyListWrapper depositionStudyListWrapper) {
        return Optional.ofNullable(depositionStudyListWrapper.getStudies())
                .map(DepositionStudyList::getStudies)
                .orElse(null);
    }

    private List<DepositionSampleDto> buildSamplesList(DepositionSampleListWrapper depositionSampleListWrapper) {
        return Optional.ofNullable(depositionSampleListWrapper.getSamplesList())
                .map(DepositionSampleList::getSamples)
                .orElse(null);
    }


}
