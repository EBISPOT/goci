package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.index.SnpIndex;
import uk.ac.ebi.spot.goci.index.StudyIndex;
import uk.ac.ebi.spot.goci.index.TraitIndex;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.ObjectDocumentMapper;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.model.TraitDocument;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
@Service
public class GwasSolrIndexer {
    @Autowired StudyService studyService;
    @Autowired SingleNucleotidePolymorphismService snpService;
    @Autowired EfoTraitRepository efoTraitRepository;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired TraitIndex traitIndex;

    private boolean sysOutLogging = false;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void enableSysOutLogging() {
        this.sysOutLogging = true;
    }

    public void disableSysOutLogging() {
        this.sysOutLogging = false;
    }

    public int fetchAndIndex() {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(4);

        Future<Integer> studyCountFuture = taskExecutor.submit(this::mapStudies);
        Future<Integer> snpCountFuture = taskExecutor.submit(this::mapSnps);
        Future<Integer> traitCountFuture = taskExecutor.submit(this::mapTraits);
        try {
            int studyCount = studyCountFuture.get();
            int snpCount = snpCountFuture.get();
            int traitCount = traitCountFuture.get();
            return studyCount + snpCount + traitCount;
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

    @Transactional Integer mapStudies() {
        ObjectDocumentMapper<Study, StudyDocument> studyMapper =
                new ObjectDocumentMapper<>(StudyDocument.class, studyIndex);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "studyDate"));
        PageRequest page1 = new PageRequest(1, 50, sort);
        List<Study> studies = studyService.deepFindAll(page1).getContent();
        ForkJoinPool pool = new ForkJoinPool(1);
        try {
            pool.submit(() -> studies.parallelStream()
                    .map(studyMapper::map)
                    .forEach(studyDocument -> {if (sysOutLogging) System.out.print(".");})).get();
            return studies.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SolrIndexingException("Failed to map one or more studies into Solr", e);
        }
    }

    @Transactional Integer mapSnps() {
        ObjectDocumentMapper<SingleNucleotidePolymorphism, SnpDocument> snpMapper =
                new ObjectDocumentMapper<>(SnpDocument.class, snpIndex);
        Sort sort = new Sort(new Sort.Order("rsId"));
        PageRequest page1 = new PageRequest(1, 50, sort);
        List<SingleNucleotidePolymorphism> snps = snpService.deepFindAll(page1).getContent();
        ForkJoinPool pool = new ForkJoinPool(1);
        try {
            pool.submit(() -> snps.parallelStream()
                    .map(snpMapper::map)
                    .forEach(snpDocument -> {if (sysOutLogging) System.out.print(".");})).get();
            return snps.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SolrIndexingException("Failed to map one or more studies into Solr", e);
        }
    }

    @Transactional Integer mapTraits() {
        ObjectDocumentMapper<EfoTrait, TraitDocument> traitMapper =
                new ObjectDocumentMapper<>(TraitDocument.class, traitIndex);
        Sort sort = new Sort(new Sort.Order("trait"));
        PageRequest page1 = new PageRequest(1, 50, sort);
        List<EfoTrait> traits = efoTraitRepository.findAll(page1).getContent();
        ForkJoinPool pool = new ForkJoinPool(1);
        try {
            pool.submit(() -> traits.parallelStream()
                    .map(traitMapper::map)
                    .forEach(traitDocument -> {if (sysOutLogging) System.out.print(".");})).get();
            return traits.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SolrIndexingException("Failed to map one or more studies into Solr", e);
        }
    }
}
