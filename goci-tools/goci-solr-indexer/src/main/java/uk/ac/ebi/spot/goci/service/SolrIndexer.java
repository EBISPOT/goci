package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.index.AssociationIndex;
import uk.ac.ebi.spot.goci.index.SnpIndex;
import uk.ac.ebi.spot.goci.index.StudyIndex;
import uk.ac.ebi.spot.goci.index.TraitIndex;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.ObjectDocumentMapper;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.model.TraitDocument;
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
    @Autowired StudyService studyService;
    @Autowired SingleNucleotidePolymorphismService snpService;
    @Autowired EfoTraitRepository efoTraitRepository;
    @Autowired AssociationService associationService;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired TraitIndex traitIndex;
    @Autowired AssociationIndex associationIndex;

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
        ObjectDocumentMapper<Study, StudyDocument> studyMapper =
                new ObjectDocumentMapper<>(StudyDocument.class, studyIndex);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "studyDate"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<Study> studyPage = studyService.deepFindAll(pager);
        studyMapper.map(studyPage.getContent());
        while (studyPage.hasNext()) {
            pager = pager.next();
            studyPage = studyService.deepFindAll(pager);
            studyMapper.map(studyPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) studyPage.getTotalElements();
    }

    Integer mapSnps() {
        ObjectDocumentMapper<SingleNucleotidePolymorphism, SnpDocument> snpMapper =
                new ObjectDocumentMapper<>(SnpDocument.class, snpIndex);
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
        ObjectDocumentMapper<EfoTrait, TraitDocument> traitMapper =
                new ObjectDocumentMapper<>(TraitDocument.class, traitIndex);
        Sort sort = new Sort(new Sort.Order("trait"));
        Pageable pager = new PageRequest(0, pageSize, sort);
        Page<EfoTrait> efoTraitPage = efoTraitRepository.findAll(pager);
        traitMapper.map(efoTraitPage.getContent());
        while (efoTraitPage.hasNext()) {
            pager = pager.next();
            efoTraitPage = efoTraitRepository.findAll(pager);
            traitMapper.map(efoTraitPage.getContent());
            if (sysOutLogging) {
                System.out.print(".");
            }
        }
        return (int) efoTraitPage.getTotalElements();
    }

    Integer mapAssociations() {
        ObjectDocumentMapper<Association, AssociationDocument> associationMapper =
                new ObjectDocumentMapper<>(AssociationDocument.class, associationIndex);
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
