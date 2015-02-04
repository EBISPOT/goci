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
import uk.ac.ebi.spot.goci.index.AssociationIndex;
import uk.ac.ebi.spot.goci.index.SnpIndex;
import uk.ac.ebi.spot.goci.index.StudyIndex;
import uk.ac.ebi.spot.goci.index.TraitIndex;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;

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
    @Autowired StudyService studyService;
    @Autowired SingleNucleotidePolymorphismService snpService;
    @Autowired DiseaseTraitRepository diseaseTraitRepository;
    @Autowired AssociationService associationService;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired TraitIndex traitIndex;
    @Autowired AssociationIndex associationIndex;

    @Autowired StudyMapper studyMapper;
    @Autowired SingleNucleotidePolymorphismMapper snpMapper;
    @Autowired TraitMapper traitMapper;
    @Autowired AssociationMapper associationMapper;

    private int pageSize = 1000;
    private boolean sysOutLogging = false;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
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
        Future<Integer> snpCountFuture = taskExecutor.submit(this::mapSnps);
        Future<Integer> traitCountFuture = taskExecutor.submit(this::mapTraits);
        Future<Integer> associationCountFuture = taskExecutor.submit(this::mapAssociations);
        try {
            int studyCount = studyCountFuture.get();
            int snpCount = snpCountFuture.get();
            int traitCount = traitCountFuture.get();
            int associationCount = associationCountFuture.get();
            return studyCount + snpCount + traitCount + associationCount;
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
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "studyDate"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<Study> studyPage = studyService.deepFindPublished(pager);
        studyMapper.map(studyPage.getContent());
        while (studyPage.hasNext()) {
            pager = pager.next();
            studyPage = studyService.deepFindPublished(pager);
            studyMapper.map(studyPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) studyPage.getTotalElements();
    }

    Integer mapSnps() {
        Sort sort = new Sort(new Sort.Order("rsId"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<SingleNucleotidePolymorphism> snpPage = snpService.deepFindAll(pager);
        snpMapper.map(snpPage.getContent());
        while (snpPage.hasNext()) {
            pager = pager.next();
            snpPage = snpService.deepFindAll(pager);
            snpMapper.map(snpPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) snpPage.getTotalElements();
    }

    Integer mapTraits() {
        Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<DiseaseTrait> diseaseTraitPage = diseaseTraitRepository.findAll(pager);
        traitMapper.map(diseaseTraitPage.getContent());
        while (diseaseTraitPage.hasNext()) {
            pager = pager.next();
            diseaseTraitPage = diseaseTraitRepository.findAll(pager);
            traitMapper.map(diseaseTraitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) diseaseTraitPage.getTotalElements();
    }

    Integer mapAssociations() {
        Sort sort = new Sort(new Sort.Order("id"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<Association> associationPage = associationService.deepFindAll(pager);
        associationMapper.map(associationPage.getContent());
        while (associationPage.hasNext()) {
            pager = pager.next();
            associationPage = associationService.deepFindAll(pager);
            associationMapper.map(associationPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) associationPage.getTotalElements();
    }
}
