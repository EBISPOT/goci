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
            boolean studyPublished = false;
            Housekeeping housekeeping = study.getHousekeeping();
            if (housekeeping.getIsPublished()) {
                studyPublished = true;
            }else if (!housekeeping.getIsPublished() && housekeeping.getCatalogUnpublishDate() != null) {
                studyPublished = true;
            }else if(housekeeping.getCurationStatus().getStatus().equals("Curation Abandoned")
                    || housekeeping.getCurationStatus().getStatus().equals("Unpublished from catalog")){
                studyPublished = true;
            }
            if(!studyPublished){
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
        List<String> newPubs = new ArrayList<>();
        List<String> updatePubs = new ArrayList<>();
        List<String> sumStatsPubs = new ArrayList<>();
        List<Publication> gociPublications = publicationService.findAll();
        //if publication not in Deposition, insert
        Map<String, DepositionPublication> depositionPublications = depositionPublicationService.getAllPublications();
        Map<String, BodyOfWorkDto> bomMap = depositionPublicationService.getAllBodyOfWork();
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            //System.out.println("checking pmid " + pubmedId);
//            boolean isAvailable = isAvailable(p);
            DepositionPublication newPublication = createPublication(p);
            boolean isPublished = isPublished(p);
            if(isPublished){
                newPublication.setStatus("PUBLISHED");
            }
            DepositionPublication depositionPublication = depositionPublications.get(pubmedId);
            if(initialSync) { // add all publications to mongo
                if (newPublication != null && depositionPublication == null) {
                    if(isPublished) {
                        System.out.println("adding published publication " + pubmedId + " to mongo");
                        if(addSummaryStatsData(newPublication, p)){
                            newPublication.setStatus("PUBLISHED_WITH_SS");
                        }
                    }else if(isUnpublished(p, bomMap.values())){//check if this pubmed id is associated with a
                        // prepublished submission
                        newPublication.setStatus("UNDER_SUBMISSION");
                    }else {
                        newPublication.setStatus("ELIGIBLE");
                    }
                    depositionPublicationService.addPublication(newPublication);
                }
            }else {
                if(depositionPublication == null && newPublication != null) { // add new publication
                    if(isPublished) {
                        if(addSummaryStatsData(newPublication, p)) {
                            System.out.println("adding published publication w/ sumstats " + pubmedId);
                            newPublication.setStatus("PUBLISHED_WITH_SS");
                            newPublication.setFirstAuthor(p.getFirstAuthor().getFullnameStandard());
                        }else {
                            System.out.println("adding published publication " + pubmedId + " to mongo");
                        }
                    }else if(isUnpublished(p, bomMap.values())){//check if this pubmed id is associated with a
                        // prepublished submission
                        newPublication.setStatus("UNDER_SUBMISSION");
                    }else {
                        newPublication.setStatus("ELIGIBLE");
                        System.out.println("adding eligible publication " + pubmedId + " to mongo");
                    }
                    depositionPublicationService.addPublication(newPublication);
                    newPubs.add(newPublication.getPmid());
                }else if(newPublication != null){//check publication status, update if needed
                    if (isPublished && (depositionPublication.getStatus().equals("ELIGIBLE") || depositionPublication.getStatus().equals("CURATION_STARTED"))) { //sync newly
                        // published publications
                        newPublication.setStatus("PUBLISHED");
                        addSummaryStatsData(newPublication, p);
                        System.out.println("setting publication status to " + newPublication.getStatus() + " for " +
                                " " + pubmedId);
                        newPublication.setFirstAuthor(p.getFirstAuthor().getFullnameStandard());
                        depositionPublicationService.updatePublication(newPublication);
                        updatePubs.add(newPublication.getPmid());
                    }else if (isPublished && depositionPublication.getStatus().equals("PUBLISHED")) { //sync newly
                        //published summary stats
                        if(addSummaryStatsData(newPublication, p)) {
                            System.out.println("setting publication status to PUBLISHED_WITH_SS for " + pubmedId);
                            newPublication.setStatus("PUBLISHED_WITH_SS");
                            newPublication.setFirstAuthor(p.getFirstAuthor().getFullnameStandard());
                            depositionPublicationService.updatePublication(newPublication);
                            sumStatsPubs.add(newPublication.getPmid());
                        }
                    }
                }
            }
        }
        System.out.println("created " + newPubs.size());
        System.out.println(Arrays.toString(newPubs.toArray()));
        System.out.println("published " + updatePubs.size());
        System.out.println(Arrays.toString(updatePubs.toArray()));
        System.out.println("added sum stats " + sumStatsPubs.size());
        System.out.println(Arrays.toString(sumStatsPubs.toArray()));
    }

    /**
     * method to import new studies from deposition that do not have a PubMed ID. We want to keep them separate from
     * existing studies to indicate the different level of review. But we do want them stored in GOCI so they can be
     * searched and displayed.
     */
    public void syncUnpublishedStudies(){
        List<String> newPubs = new ArrayList<>();
        List<String> updatePubs = new ArrayList<>();
        Map<String, DepositionSubmission> submissions = submissionService.getSubmissions();
        //if unpublished_studies does not have accession, add
        //check body of work, if not found, add
        //else if accession exists, check publication for change, update
        //curation import will need to prune unpublished_studies, ancestry and body_of_work
        submissions.forEach((s, submission) -> {
            if(submission.getStatus().equals("SUBMITTED") && submission.getPublication() == null){
                BodyOfWorkDto bodyOfWorkDto = submission.getBodyOfWork();
                if(isEligible(bodyOfWorkDto)) {
                    Set<UnpublishedStudy> studies = new HashSet<>();
                    submission.getStudies().forEach(studyDto -> {
                        String studyTag = studyDto.getStudyTag();
                        UnpublishedStudy unpublishedStudy = unpublishedRepository.findByAccession(studyDto.getAccession());
                        if (unpublishedStudy == null) {
                            unpublishedStudy =
                                    unpublishedRepository.save(UnpublishedStudy.createFromStudy(studyDto, submission));
                            studies.add(unpublishedStudy);
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
                        }
                    });
                    BodyOfWork bom = bodyOfWorkRepository.findByPublicationId(bodyOfWorkDto.getBodyOfWorkId());
                    if(bom == null) {
                        //add bodies of work
                        BodyOfWork bodyOfWork = BodyOfWork.create(bodyOfWorkDto);
                        bodyOfWork.setStudies(studies);
                        bodyOfWorkRepository.save(bodyOfWork);
                        newPubs.add(bodyOfWorkDto.getBodyOfWorkId());
                    }
                }
            }
        });
        Map<String, BodyOfWorkDto> bomMap = depositionPublicationService.getAllBodyOfWork();
        bomMap.entrySet().stream().forEach(e->{
            String bomId = e.getKey();
            BodyOfWorkDto dto = e.getValue();
            BodyOfWork bom = bodyOfWorkRepository.findByPublicationId(bomId);
            BodyOfWork newBom = BodyOfWork.create(dto);
            if(bom != null && isEligible(dto) && !bom.equals(newBom)){
                bom.update(newBom);
                bodyOfWorkRepository.save(bom);
                updatePubs.add(dto.getBodyOfWorkId());
            }
        });
        System.out.println("created " + newPubs.size());
        System.out.println(Arrays.toString(newPubs.toArray()));
        System.out.println("updated " + updatePubs.size());
        System.out.println(Arrays.toString(updatePubs.toArray()));

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
            if(study.getAccessionId() != null) {
                DepositionSummaryStatsDto summaryStatsDto = new DepositionSummaryStatsDto();
                summaryStatsDto.setStudyAccession(study.getAccessionId());
                summaryStatsDto.setSampleDescription(study.getInitialSampleSize());
                if (study.getDiseaseTrait() != null) {
                    summaryStatsDto.setTrait(study.getDiseaseTrait().getTrait());
                }
                summaryStatsDto.setStudyTag(study.getStudyTag());
                if (study.getFullPvalueSet()) {
                    hasFiles = true;
                }
                summaryStatsDto.setHasSummaryStats(study.getFullPvalueSet());
                summaryStatsDtoList.add(summaryStatsDto);
            }
        }
        if(summaryStatsDtoList.size() != 0) {
            depositionPublication.setSummaryStatsDtoList(summaryStatsDtoList);
        }
        return hasFiles;
    }

    /**
     * fix publications is intended as a one-off execution to correct errors with loaded data, not as part of the
     * daily sync
     */
    public void fixPublications() {
        List<String> fixedPubs = new ArrayList<>();
        //read all publications from GOCI
        List<Publication> gociPublications = publicationService.findAll();
        //check status, set to PUBLISHED_SS if hasSummaryStats
        Map<String, DepositionSubmission> submissions = submissionService.getSubmissions();
        Map<String, DepositionPublication> publicationMap = buildPublicationMap(submissions);
        Map<String, List<String>> sumStatsMap = buildSumStatsMap(submissions);
        System.out.println("pmid\tis published\tdepo sum stats size\tgoci sum stats size");
        for (Publication p : gociPublications) {
            String pubmedId = p.getPubmedId();
            List<String> accessionList = sumStatsMap.get(pubmedId);
            DepositionPublication depositionPublication = publicationMap.get(pubmedId);
            boolean isPublished = isPublished(p);
            if(depositionPublication != null) {
                addSummaryStatsData(depositionPublication, p);
                int newSumStatsSize = depositionPublication.getSummaryStatsDtoList() != null ?
                        depositionPublication.getSummaryStatsDtoList().size() : 0;
                System.out.println(pubmedId + "\t" + isPublished + "\t" + accessionList.size() + "\t" + newSumStatsSize);
                if (accessionList.size() != newSumStatsSize) {
                    System.out.println("adding summary stats to " + depositionPublication.getPmid());
                    depositionPublicationService.updatePublication(depositionPublication);
                    fixedPubs.add(pubmedId);
                }
            }
        }
        System.out.println("fixed " + fixedPubs.size());
        System.out.println(Arrays.toString(fixedPubs.toArray()));

    }

    private Map<String, DepositionPublication> buildPublicationMap(Map<String, DepositionSubmission> submissions) {
        Map<String, DepositionPublication> publicationMap = new HashMap<>();
        submissions.forEach((s, submission) ->{
            if(submission.getPublication() != null){
            publicationMap.put(submission.getPublication().getPmid(),
                submission.getPublication());
            }
        });
        return publicationMap;
    }

    private Map<String, List<String>> buildSumStatsMap(Map<String, DepositionSubmission> submissions) {
        Map<String, List<String>> sumStatsMap = new HashMap<>();
        submissions.forEach((s, submission) ->{
            if(submission.getPublication() != null){
                List<String> sumStatsList = new ArrayList<>();
                submission.getStudies().forEach(studyDto->{
                    if(studyDto.getAccession() != null) {
                        sumStatsList.add(studyDto.getAccession());
                    }
                });
                sumStatsMap.put(submission.getPublication().getPmid(), sumStatsList);
            }
        });
        return sumStatsMap;
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
            newPublication.setStatus("ELIGIBLE");
        } else {
            System.out.println("error: publication " + p.getPubmedId() + " has no authors");
        }
        return newPublication;
    }

    private boolean isEligible(BodyOfWorkDto bodyOfWorkDto) {
        if(!bodyOfWorkDto.getStatus().equals("UNDER_SUBMISSION")){
            return false;
        }if (bodyOfWorkDto.getEmbargoUntilPublished() != null &&  bodyOfWorkDto.getEmbargoUntilPublished() == true && bodyOfWorkDto.getPmids() == null){
            return false;
        }if(bodyOfWorkDto.getEmbargoDate() != null && new LocalDate().isBefore(bodyOfWorkDto.getEmbargoDate())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isUnpublished(Publication publication, Collection<BodyOfWorkDto> bomList){
        if(bodyOfWorkRepository.findByPubMedId(publication.getPubmedId()) != null){
            return true;
        }else{
            List<BodyOfWork> bodiesOfWork = bodyOfWorkRepository.findAll();
            for (BodyOfWork bom : bodiesOfWork) {
                if (publication.getPubmedId().equals(bom.getPubMedId())) {
                    return true;
                }else if(bom.getPubMedId() != null && bom.getPubMedId().contains(publication.getPubmedId())){
                    return  true;
                }
            }
            for(BodyOfWorkDto bom: bomList){
                String bomId = String.join(",", bom.getPmids());
                if (bomId.contains(publication.getPubmedId())){
                    return  true;
                }
            }

        }
        return false;
    }
}
