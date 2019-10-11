package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.SolrIndexingException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class SolrIndexer {
    private StudyService studyService;
    private DiseaseTraitRepository diseaseTraitRepository;
    private AssociationService associationService;
    private EfoTraitRepository efoTraitRepository;

    private StudyMapper studyMapper;
    private TraitMapper traitMapper;
    private AssociationMapper associationMapper;
    private EfoMapper efoMapper;

    @Value("${solr_index.page_size:100}")
    private int pageSize = 100;
    private int maxPages = -1;
    @Value("${solr_index.thread_count:1}")
    private int threadCount = 1;
    private boolean sysOutLogging = false;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public SolrIndexer(StudyService studyService,
                       DiseaseTraitRepository diseaseTraitRepository,
                       EfoTraitRepository efoTraitRepository,
                       AssociationService associationService,
                       StudyMapper studyMapper,
                       TraitMapper traitMapper,
                       AssociationMapper associationMapper,
                       EfoMapper efoMapper) {
        this.studyService = studyService;
        this.diseaseTraitRepository = diseaseTraitRepository;
        this.efoTraitRepository = efoTraitRepository;
        this.associationService = associationService;
        this.studyMapper = studyMapper;
        this.traitMapper = traitMapper;
        this.associationMapper = associationMapper;
        this.efoMapper = efoMapper;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void enableSysOutLogging() {
        this.sysOutLogging = true;
    }

    public void disableSysOutLogging() {
        this.sysOutLogging = false;
    }

    /**
     * rewrite of fetchAndIndex using all threads per task. So rather than having a thread per
     * studies/associations/traits/efo, we use all threads to process all types, then move on to the next type
     * @return
     */
    public int fetchAndIndexAllThreads(){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(threadCount);
        int count = -1;
        try {
            int processors = Runtime.getRuntime().availableProcessors();
            log.debug("available processors: " + processors);
            log.debug("running with # threads: " + threadCount);
            int associationCount = mapAllAssociations(taskExecutor);
            int studyCount = mapAllStudies(taskExecutor);
            int efoCount = mapAllEfos(taskExecutor);
            int traitCount = mapAllTraits(taskExecutor);
            count = studyCount + associationCount + traitCount + efoCount;
        }catch(InterruptedException e){
            getLog().error(e.getMessage(), e);
        }
        finally {
            taskExecutor.shutdown();
            int s = 5;
            try {
                taskExecutor.awaitTermination(s, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                getLog().error("The application failed to terminate cleanly in " + s + " seconds.");
            }
        }
        return count;
    }


    private int mapAllStudies(ExecutorService taskExecutor) throws InterruptedException {
        //Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.publicationDate"));
        Pageable pager = new PageRequest(0, pageSize);
        Page<Study> studyPage = studyService.findPublishedStudies(pager);
        studyMapper.map(studyPage.getContent());
        CountDownLatch latch = new CountDownLatch(studyPage.getTotalPages() - 1);
        if(sysOutLogging){
            System.out.println("mapping " + studyPage.getTotalPages() + " study pages");
        }
        while (studyPage.hasNext()) {
            //pass parsing of page off to thread
            if (maxPages != -1 && studyPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            studyPage = studyService.findPublishedStudies(pager);
            taskExecutor.execute(new StudyThread(studyPage.getContent(), latch, pager.getPageNumber()));
        }
        latch.await();
        return (int) studyPage.getTotalElements();
    }

    private int mapAllAssociations(ExecutorService taskExecutor) throws InterruptedException {
        //Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize);
        Page<Association> associationPage = associationService.findPublishedAssociations(pager);
        associationMapper.map(associationPage.getContent());
        if(sysOutLogging){
            System.out.println("mapping " + associationPage.getTotalPages() + " association pages");
        }
        CountDownLatch latch = new CountDownLatch(associationPage.getTotalPages() - 1);
        while (associationPage.hasNext()) {
            if (maxPages != -1 && associationPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            associationPage = associationService.findPublishedAssociations(pager);
            taskExecutor.execute(new AssociationThread(associationPage.getContent(), latch, pager.getPageNumber()));
        }
        latch.await();
        return (int) associationPage.getTotalElements();
    }

    private int mapAllTraits(ExecutorService taskExecutor) throws InterruptedException {
        //Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize);
        Page<DiseaseTrait> diseaseTraitPage = diseaseTraitRepository.findAll(pager);
        traitMapper.map(diseaseTraitPage.getContent());
        if(sysOutLogging){
            System.out.println("mapping " + diseaseTraitPage.getTotalPages() + " disease trait pages");
        }
        CountDownLatch latch = new CountDownLatch(diseaseTraitPage.getTotalPages() - 1);
        while (diseaseTraitPage.hasNext()) {
            if (maxPages != -1 && diseaseTraitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            diseaseTraitPage = diseaseTraitRepository.findAll(pager);
            taskExecutor.execute(new TraitThread(diseaseTraitPage.getContent(), latch, pager.getPageNumber()));
        }
        latch.await();
        return (int) diseaseTraitPage.getTotalElements();
    }

    private int mapAllEfos(ExecutorService taskExecutor) throws InterruptedException {
        //Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize);
        Page<EfoTrait> efoTraitPage = efoTraitRepository.findAll(pager);
        if(sysOutLogging){
            System.out.println("mapping " + efoTraitPage.getTotalPages() + " efo trait pages");
        }
        efoMapper.map(efoTraitPage.getContent());
        CountDownLatch latch = new CountDownLatch(efoTraitPage.getTotalPages() - 1);
        while (efoTraitPage.hasNext()) {
            if (maxPages != -1 && efoTraitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            efoTraitPage = efoTraitRepository.findAll(pager);
            taskExecutor.execute(new EfoThread(efoTraitPage.getContent(), latch, pager.getPageNumber()));
        }
        latch.await();
        return (int) efoTraitPage.getTotalElements();
    }

    public int fetchAndIndexPublications(Collection<String> pubmedIds) {
        return _fetchAndIndex(pubmedIds);
    }

    public int fetchAndIndex() {
        return _fetchAndIndex(Collections.emptyList());
    }

    private int _fetchAndIndex(final Collection<String> pubmedIds) {
        int processors = Runtime.getRuntime().availableProcessors();
        log.debug("available processors: " + processors);
        log.debug("running with # threads: " + threadCount);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(threadCount);

        Future<Integer> studyCountFuture = taskExecutor.submit(() -> this.mapStudies(pubmedIds));
        Future<Integer> associationCountFuture = taskExecutor.submit(() -> this.mapAssociations(pubmedIds));
        Future<Integer> traitCountFuture = taskExecutor.submit(() -> this.mapTraits(pubmedIds));
        Future<Integer> efoCountFuture = taskExecutor.submit(() -> this.mapEfo(pubmedIds));

        try {
            int studyCount = studyCountFuture.get();
            int associationCount = associationCountFuture.get();
            int traitCount = traitCountFuture.get();
            int efoCount = efoCountFuture.get();
            return studyCount + associationCount + traitCount + efoCount;
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SolrIndexingException("Failed to map one or more documents into Solr", e);
        }
        finally {
            taskExecutor.shutdown();
            int s = 5;
            try {
                taskExecutor.awaitTermination(s, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                getLog().error("The application failed to terminate cleanly in " + s + " seconds.");
            }
        }
    }

    /**
     * If no publication ids are provided all studies will be mapped
     * @param pubmedIds
     * @return
     */
    Integer mapStudies(Collection<String> pubmedIds) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.publicationDate"));
        Pageable pager = new PageRequest(0, pageSize);

        Page<Study> studyPage = null;
        int totalElements = 0;
        if (!pubmedIds.isEmpty()) {
            for (String pmid : pubmedIds) {
                studyPage = studyService.findPublishedStudiesByPublicationId(pmid, pager);
                totalElements += _mapPagedStudies(pmid, studyPage, pager);
            }
        }
        else {
            studyPage = studyService.findPublishedStudies(pager);
            totalElements = _mapPagedStudies(null, studyPage, pager);
        }
        return totalElements;
    }

    private Integer _mapPagedStudies(String pubmedId, Page<Study> studyPage, Pageable pager) {
        studyMapper.map(studyPage.getContent());
        while (studyPage.hasNext()) {
            if (maxPages != -1 && studyPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            if (pubmedId == null) {
                studyPage = studyService.findPublishedStudies(pager);
            }
            else  {
                studyPage = studyService.findPublishedStudiesByPublicationId(pubmedId, pager);
            }
            studyMapper.map(studyPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) studyPage.getTotalElements();
    }

    Integer mapStudies() {
        return  mapStudies(Collections.emptyList());
    }

    Integer mapAssociations() {
        return  mapAssociations(Collections.emptyList());

    }

    Integer mapAssociations(Collection<String> pubmedIds) {
        Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize);

        Page<Association> associationPage = null;
        int totalElements = 0;

        if (!pubmedIds.isEmpty()) {
            for (String pmid : pubmedIds) {
                associationPage = associationService.findPublishedAssociationsPublicationId(pmid, pager);
                totalElements += _mapPagedAssociations(pmid, associationPage, pager);
            }
        }
        else {
            associationPage = associationService.findPublishedAssociations(pager);
            totalElements = _mapPagedAssociations(null, associationPage, pager);
        }

        return totalElements;
    }

    private Integer _mapPagedAssociations(String pubmedId, Page<Association> associationPage, Pageable pager) {
        associationMapper.map(associationPage.getContent());
        while (associationPage.hasNext()) {
            if (maxPages != -1 && associationPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            if (pubmedId == null) {
                associationPage = associationService.findPublishedAssociations(pager);
            }
            else  {
                associationPage = associationService.findPublishedAssociationsPublicationId(pubmedId, pager);
            }
            associationMapper.map(associationPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) associationPage.getTotalElements();
    }


    Integer mapTraits(Collection<String> pubmedIds) {
        Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize);

        Page<DiseaseTrait> diseaseTraitPage = null;
        int totalElements = 0;

        if (!pubmedIds.isEmpty()) {
            for (String pmid : pubmedIds) {
                diseaseTraitPage = diseaseTraitRepository.findByStudiesPublicationIdPubmedId(pmid, pager);
                totalElements += _mapPagedTraits(pmid, diseaseTraitPage, pager);
            }
        }
        else {
            diseaseTraitPage = diseaseTraitRepository.findAll(pager);
            totalElements = _mapPagedTraits(null, diseaseTraitPage, pager);
        }

        return totalElements;

    }

    private Integer _mapPagedTraits(String pubmedId, Page<DiseaseTrait> traitPage, Pageable pager) {
        traitMapper.map(traitPage.getContent());
        while (traitPage.hasNext()) {
            if (maxPages != -1 && traitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            if (pubmedId == null) {
                traitPage = diseaseTraitRepository.findAll(pager);
            }
            else  {
                traitPage = diseaseTraitRepository.findByStudiesPublicationIdPubmedId(pubmedId, pager);
            }
            traitMapper.map(traitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) traitPage.getTotalElements();
    }



    Integer mapTraits() {
        return mapTraits(Collections.emptyList());
    }

    Integer mapEfo(Collection<String> pubmedIds) {
        Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize);

        Page<EfoTrait> efoTraitPage = null;
        int totalElements = 0;

        if (!pubmedIds.isEmpty()) {
            for (String pmid : pubmedIds) {
                efoTraitPage = efoTraitRepository.findByStudiesPublicationIdPubmedId(pmid, pager);
                totalElements += _mapPagedEfo(pmid, efoTraitPage, pager);
            }
        }
        else {
            efoTraitPage = efoTraitRepository.findAll(pager);
            totalElements = _mapPagedEfo(null, efoTraitPage, pager);
        }

        return totalElements;
    }

    Integer mapEfo() {
        return mapEfo(Collections.emptyList());
    }

    Integer _mapPagedEfo(String pubmedId, Page<EfoTrait> efoTraitPage, Pageable pager) {
        efoMapper.map(efoTraitPage.getContent());
        while (efoTraitPage.hasNext()) {
            if (maxPages != -1 && efoTraitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            if (pubmedId == null) {
                efoTraitPage = efoTraitRepository.findAll(pager);
            }
            else  {
                efoTraitPage = efoTraitRepository.findByStudiesPublicationIdPubmedId(pubmedId, pager);
            }
            efoMapper.map(efoTraitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) efoTraitPage.getTotalElements();
    }

    private class StudyThread implements Runnable{

        private final List<Study> studyList;
        private final CountDownLatch latch;
        private final int pageNumber;

        public StudyThread(List<Study> studyList, CountDownLatch latch, int pageNumber){
            this.studyList = studyList;
            this.latch = latch;
            this.pageNumber = pageNumber;
        }

        @Override
        public void run() {
            studyMapper.map(studyList);
            if (sysOutLogging) {
                System.out.print(".");
            }
            latch.countDown();
        }
    }

    private class AssociationThread implements  Runnable{

        private final List<Association> associationList;
        private final CountDownLatch latch;
        private final int pageNumber;

        public AssociationThread(List<Association> associationList, CountDownLatch latch, int pageNumber){
            this.associationList = associationList;
            this.latch = latch;
            this.pageNumber = pageNumber;
        }

        @Override
        public void run() {
            associationMapper.map(associationList);
            if (sysOutLogging) {
                System.out.print(".");
            }
            latch.countDown();
        }
    }

    private class TraitThread implements  Runnable{

        private final List<DiseaseTrait> traitList;
        private final CountDownLatch latch;
        private final int pageNumber;

        public TraitThread(List<DiseaseTrait> traitList, CountDownLatch latch, int pageNumber){
            this.traitList = traitList;
            this.latch = latch;
            this.pageNumber = pageNumber;
        }

        @Override
        public void run() {
            traitMapper.map(traitList);
            if (sysOutLogging) {
                System.out.print(".");
            }
            latch.countDown();
        }
    }

    private class EfoThread implements  Runnable{

        private final List<EfoTrait> traitList;
        private final CountDownLatch latch;
        private final int pageNumber;

        public EfoThread(List<EfoTrait> traitList, CountDownLatch latch, int pageNumber){
            this.traitList = traitList;
            this.latch = latch;
            this.pageNumber = pageNumber;
        }

        @Override
        public void run() {
            efoMapper.map(traitList);
            if (sysOutLogging) {
                System.out.print(".");
            }
            latch.countDown();
        }
    }
}