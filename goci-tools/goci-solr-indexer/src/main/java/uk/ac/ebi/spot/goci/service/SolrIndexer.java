package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    private int pageSize = 1000;
    private int maxPages = -1;
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

    public int fetchAndIndex() {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(1);

        Future<Integer> studyCountFuture = taskExecutor.submit(this::mapStudies);
        Future<Integer> associationCountFuture = taskExecutor.submit(this::mapAssociations);
        Future<Integer> traitCountFuture = taskExecutor.submit(this::mapTraits);
        Future<Integer> efoCountFuture = taskExecutor.submit(this::mapEfo);

        try {
            int studyCount = studyCountFuture.get();
            int associationCount = associationCountFuture.get();
            int traitCount = traitCountFuture.get();
            int efoCount = efoCountFuture.get();
            return studyCount + traitCount + associationCount + efoCount;
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

    Integer mapStudies() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "publicationDate"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<Study> studyPage = studyService.findPublishedStudies(pager);
        studyMapper.map(studyPage.getContent());
        while (studyPage.hasNext()) {
            if (maxPages != -1 && studyPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            studyPage = studyService.findPublishedStudies(pager);
            studyMapper.map(studyPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) studyPage.getTotalElements();
    }

    Integer mapAssociations() {
        Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<Association> associationPage = associationService.findPublishedAssociations(pager);
        associationMapper.map(associationPage.getContent());
        while (associationPage.hasNext()) {
            if (maxPages != -1 && associationPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            associationPage = associationService.findPublishedAssociations(pager);
            associationMapper.map(associationPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) associationPage.getTotalElements();
    }

    Integer mapTraits() {
        Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<DiseaseTrait> diseaseTraitPage = diseaseTraitRepository.findAll(pager);
        traitMapper.map(diseaseTraitPage.getContent());
        while (diseaseTraitPage.hasNext()) {
            if (maxPages != -1 && diseaseTraitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            diseaseTraitPage = diseaseTraitRepository.findAll(pager);
            traitMapper.map(diseaseTraitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) diseaseTraitPage.getTotalElements();
    }

    Integer mapEfo(){
        Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<EfoTrait> efoTraitPage = efoTraitRepository.findAll(pager);
        efoMapper.map(efoTraitPage.getContent());
        while (efoTraitPage.hasNext()) {
            if (maxPages != -1 && efoTraitPage.getNumber() >= maxPages - 1) {
                break;
            }
            pager = pager.next();
            efoTraitPage = efoTraitRepository.findAll(pager);
            efoMapper.map(efoTraitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) efoTraitPage.getTotalElements();
    }
}
