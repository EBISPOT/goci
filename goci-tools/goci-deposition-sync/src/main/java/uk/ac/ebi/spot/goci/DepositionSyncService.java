package uk.ac.ebi.spot.goci;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.*;
import uk.ac.ebi.spot.goci.repository.*;
import uk.ac.ebi.spot.goci.service.DepositionPublicationService;
import uk.ac.ebi.spot.goci.service.DepositionSubmissionService;
import uk.ac.ebi.spot.goci.service.PublicationService;

import java.util.*;

@Service
@Transactional
public class DepositionSyncService {
    private final CurationStatus curationComplete;
    private final Curator levelOneCurator;
    private final CurationStatus awaitingCuration;
    private final CurationStatus awaitingLiterature;
    private final DepositionSubmissionService submissionService;
    private final BodyOfWorkRepository bodyOfWorkRepository;
    private final UnpublishedStudyRepository unpublishedRepository;
    private final UnpublishedAncestryRepository unpublishedAncestryRepo;
    private PublicationService publicationService;

    private DepositionPublicationService depositionPublicationService;

    private CurationStatusRepository statusRepository;

    private CuratorRepository curatorRepository;

    public DepositionSyncService(@Autowired PublicationService publicationService,
                                 @Autowired DepositionPublicationService depositionPublicationService,
                                 @Autowired CurationStatusRepository statusRepository,
                                 @Autowired CuratorRepository curatorRepository,
                                 @Autowired DepositionSubmissionService submissionService,
                                 @Autowired BodyOfWorkRepository bodyOfWorkRepository,
                                 @Autowired UnpublishedStudyRepository unpublishedRepository,
                                 @Autowired UnpublishedAncestryRepository unpublishedAncestryRepo) {
        this.curatorRepository = curatorRepository;
        this.statusRepository = statusRepository;
        this.depositionPublicationService = depositionPublicationService;
        this.publicationService = publicationService;
        this.submissionService = submissionService;
        this.bodyOfWorkRepository = bodyOfWorkRepository;
        this.unpublishedRepository = unpublishedRepository;
        this.unpublishedAncestryRepo = unpublishedAncestryRepo;

        curationComplete = statusRepository.findByStatus("Publish study");
        levelOneCurator = curatorRepository.findByLastName("Level 1 Curator");
        awaitingCuration = statusRepository.findByStatus("Awaiting Curation");
        awaitingLiterature = statusRepository.findByStatus("Awaiting literature");
    }

    private boolean isPublished(Publication publication) {

        for (Study study : publication.getStudies()) {
            Housekeeping housekeeping = study.getHousekeeping();
            if (!housekeeping.getIsPublished()) {
                return false;
            }
        }
        return true;
    }

//    private boolean isAvailable(Publication publication) {
//
//        for (Study study : publication.getStudies()) {
//            Housekeeping housekeeping = study.getHousekeeping();
//            CurationStatus curationStatus = housekeeping.getCurationStatus();
//            Curator curator = housekeeping.getCurator();
//            if (curationStatus == null) {
//                return false;
//            } else if (!curationStatus.getId().equals(awaitingCuration.getId()) &&
//                    !curationStatus.getId().equals(curationComplete.getId()) &&
//                    !curationStatus.getId().equals(awaitingLiterature.getId())) {
//                return false;
//            } else if (!curator.getId().equals(levelOneCurator.getId())) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean isWaiting(Publication publication) {
//
//        for (Study study : publication.getStudies()) {
//            Housekeeping housekeeping = study.getHousekeeping();
//            CurationStatus curationStatus = housekeeping.getCurationStatus();
//            Curator curator = housekeeping.getCurator();
//            if (curationStatus == null) {
//                return false;
//            } else if (!curationStatus.getId().equals(awaitingLiterature.getId())) {
//                return false;
//            } else if (!curator.getId().equals(levelOneCurator.getId())) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * syncPublications intends to keep publications syned between GOCI and Deposition. It checks the GOCI catalog
     * against Deposition. If a publication is in GOCI but not in Deposition, it checks the state of the studies in
     * GOCI.
     * If the study is awaiting curation (assigned to Level 1 Curator + Awating Curation status), the publication is
     * sent
     * to Deposition as AVAILABLE. If the study is published (Publish Study status) the publication is sent to
     * Deposition
     * as EXPORTED and a submission is created containing the study data from GOCI.
     * This is the reverse of the Import endpoint, where a submission is received from Curation and added into GOCI.
     */
    public void syncPublications(boolean initialSync) {
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        //if publication not in Deposition, insert
        Map<String, DepositionPublication> depositionPublications = depositionPublicationService.getAllPublications();
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            //System.out.println("checking pmid " + pubmedId);
            boolean isPublished = isPublished(p);
//            boolean isAvailable = isAvailable(p);
            DepositionPublication newPublication = createPublication(p);
            DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
            if(initialSync) { // add all publications to mongo
                if (newPublication != null && depositionPublication == null) {
                    if(isPublished) {
                        System.out.println("adding published publication " + pubmedId + " to mongo");
                        if(addSummaryStatsData(newPublication, p)){
                            newPublication.setStatus("PUBLISHED_WITH_SS");
                        }
                    }else {
                        newPublication.setStatus("ELIGIBLE");
                    }
                    depositionPublicationService.addPublication(newPublication);
                }
            }else {
                if(depositionPublication == null && newPublication != null) { // add new publication
                    if(isPublished) {
                        System.out.println("adding published publication " + pubmedId + " to mongo");
                    }else {
                        newPublication.setStatus("ELIGIBLE");
                        System.out.println("adding eligible publication " + pubmedId + " to mongo");
                    }
                    depositionPublicationService.addPublication(newPublication);
                }else if(newPublication != null){//check publication status, update if needed
                    if (isPublished && (depositionPublication.getStatus().equals("ELIGIBLE") || depositionPublication.getStatus().equals("CURATION_STARTED"))) { //sync newly
                        // published publications
                        newPublication.setStatus("PUBLISHED");
                        addSummaryStatsData(newPublication, p);
                        System.out.println("setting publication status to " + newPublication.getStatus() + " for " +
                                " " + pubmedId);
                        newPublication.setFirstAuthor(p.getFirstAuthor().getFullnameStandard());
                        depositionPublicationService.updatePublication(newPublication);
                    }else if (isPublished && depositionPublication.getStatus().equals("PUBLISHED")) { //sync newly
                        //published summary stats
                        if(addSummaryStatsData(newPublication, p)) {
                            System.out.println("setting publication status to PUBLISHED_WITH_SS for " + pubmedId);
                            newPublication.setStatus("PUBLISHED_WITH_SS");
                            newPublication.setFirstAuthor(p.getFirstAuthor().getFullnameStandard());
                            depositionPublicationService.updatePublication(newPublication);
                        }
                    }
                }
            }

        }
    }

    /**
     * method to import new studies from deposition that do not have a PubMed ID. We want to keep them separate from
     * existing studies to indicate the different level of review. But we do want them stored in GOCI so they can be
     * searched and displayed.
     */
    public void syncUnpublishedStudies(){
        Map<String, DepositionSubmission> submissions = submissionService.getSubmissions();
        //if unpublished_studies does not have accession, add
        //check body of work, if not found, add
        //else if accession exists, check publication for change, update
        //curation import will need to prune unpublished_studies, ancestry and body_of_work
        submissions.forEach((s, submission) -> {
            if(submission.getStatus().equals("SUBMITTED") && submission.getPublication() == null){
                submission.getStudies().forEach(studyDto -> {
                    BodyOfWorkDto bodyOfWorkDto = submission.getBodyOfWork();
                    if(bodyOfWorkDto.getEmbargoUntilPublished() == true || (bodyOfWorkDto.getEmbargoDate() != null && bodyOfWorkDto.getEmbargoDate().isBefore(new LocalDate()))){
                    }else {
                        String studyTag = studyDto.getStudyTag();
                        UnpublishedStudy unpublishedStudy = unpublishedRepository.findByAccession(studyDto.getAccession());
                        if (unpublishedStudy == null) {
                            unpublishedStudy =
                                    unpublishedRepository.save(UnpublishedStudy.createFromStudy(studyDto, submission));
                            Set<UnpublishedStudy> studies = new HashSet<>();
                            studies.add(unpublishedStudy);
                            //add bodies of work
                            BodyOfWork bodyOfWork = BodyOfWork.create(bodyOfWorkDto);
                            bodyOfWork.setStudies(studies);
                            bodyOfWorkRepository.save(bodyOfWork);
                            //add ancestries
                            List<DepositionSampleDto> sampleDtoList = getSamples(submission, studyTag);
                            List<UnpublishedAncestry> ancestryList = new ArrayList<>();
                            for (DepositionSampleDto sampleDto : sampleDtoList) {
                                UnpublishedAncestry ancestry = UnpublishedAncestry.create(sampleDto, unpublishedStudy);
                                ancestryList.add(ancestry);
                            }
                            unpublishedAncestryRepo.save(ancestryList);
                            unpublishedStudy.setAncestries(ancestryList);
                            unpublishedRepository.save(unpublishedStudy);
                        } else {//check for changes to body of work
                            Collection<BodyOfWork> bodyOfWorks = unpublishedStudy.getBodiesOfWork();
                            bodyOfWorks.forEach(bodyOfWork -> {
                                if (bodyOfWork.getPublicationId().equals(bodyOfWorkDto.getBodyOfWorkId())) {
                                    //create/update body of work
//                                    BodyOfWork updateBodyOfWork = BodyOfWork.create(bodyOfWorkDto);
//                                    updateBodyOfWork.setId(bodyOfWork.getId());
//                                    if (!updateBodyOfWork.equals(bodyOfWork)) {
//                                        bodyOfWorkRepository.save(updateBodyOfWork);
//                                    }
                                }
                            });
                        }
//                        submission.setStatus("IMPORTED");
//                        submissionService.updateSubmission(submission);
                    }
                });
            }
        });
    }

    private List<DepositionSampleDto> getSamples(DepositionSubmission submission, String studyTag){
        List<DepositionSampleDto> sampleDtoList = new ArrayList<>();
        submission.getSamples().forEach(depositionSampleDto -> {
            if(depositionSampleDto.getStudyTag().equals(studyTag)){
                sampleDtoList.add(depositionSampleDto);
            }
        });
        return sampleDtoList;
    }

    private boolean addSummaryStatsData(DepositionPublication depositionPublication, Publication publication){
        boolean hasFiles = false;
        List<DepositionSummaryStatsDto> summaryStatsDtoList = new ArrayList<>();
        Collection<Study> studies = publication.getStudies();
        for(Study study: studies){
            DepositionSummaryStatsDto summaryStatsDto = new DepositionSummaryStatsDto();
            summaryStatsDto.setStudyAccession(study.getAccessionId());
            summaryStatsDto.setSampleDescription(study.getInitialSampleSize());
            summaryStatsDto.setTrait(study.getDiseaseTrait().getTrait());
            summaryStatsDto.setStudyTag(study.getStudyTag());
            if(study.getFullPvalueSet()){
                hasFiles = true;
            }
            summaryStatsDto.setHasSummaryStats(study.getFullPvalueSet());
            summaryStatsDtoList.add(summaryStatsDto);
        }
        depositionPublication.setSummaryStatsDtoList(summaryStatsDtoList);
        if(hasFiles){
            depositionPublication.setStatus("PUBLISHED_WITH_SS");
        }
        return hasFiles;
    }

    /**
     * fix publications is intended as a one-off execution to correct errors with loaded data, not as part of the
     * daily sync
     */
    public void fixPublications() {
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        //check status, set to PUBLISHED_SS if hasSummaryStats
        Map<String, DepositionPublication> depositionPublications =
                depositionPublicationService.getAllBackendPublications();
        for (Publication p : gociPublications) {
            boolean isPublished = isPublished(p);
            String pubmedId = p.getPubmedId();
            //System.out.println("checking pmid " + pubmedId);
            DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
            if(isPublished && depositionPublication.getStatus().equals("PUBLISHED") && addSummaryStatsData(depositionPublication, p)){
                depositionPublication.setStatus("PUBLISHED_WITH_SS");
                System.out.println("updating " + depositionPublication.getPmid());
//                depositionPublicationService.updatePublication(depositionPublication);
            }
        }
    }

    private DepositionPublication createPublication(Publication p) {
        Author author = p.getFirstAuthor();
        DepositionPublication newPublication = null;
        if (author != null) { //error check for invalid publications
            newPublication = new DepositionPublication();
            newPublication.setPmid(p.getPubmedId());
            String authorName = null;
            Author firstAuthor = p.getFirstAuthor();
            if(firstAuthor.getLastName() == null || firstAuthor.getInitials() == null){
                authorName = firstAuthor.getFullname();
            }else {
                authorName = firstAuthor.getLastName() + " " + firstAuthor.getInitials();
            }
            newPublication.setFirstAuthor(authorName);
            newPublication.setPublicationDate(new LocalDate(p.getPublicationDate()));
            newPublication.setPublicationId(p.getId().toString());
            newPublication.setTitle(p.getTitle());
            newPublication.setJournal(p.getPublication());
            newPublication.setStatus("PUBLISHED");
        } else {
            System.out.println("error: publication " + p.getPubmedId() + " has no authors");
        }
        return newPublication;
    }
}
