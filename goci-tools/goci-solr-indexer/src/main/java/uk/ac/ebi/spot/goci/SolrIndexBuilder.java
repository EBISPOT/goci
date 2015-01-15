package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.SingleNucleotidePolymorphismService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableTransactionManagement
public class SolrIndexBuilder {
    @Autowired StudyRepository studyRepository;
    @Autowired SingleNucleotidePolymorphismService snpService;
    @Autowired EfoTraitRepository efoTraitRepository;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired TraitIndex traitIndex;

    public static void main(String[] args) {
        System.out.println("Starting Solr indexing application...");
        ApplicationContext ctx = SpringApplication.run(SolrIndexBuilder.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            convertDocuments();
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
        };
    }

    void convertDocuments() {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(4);

        System.out.print("Converting all GWAS database objects...");
        Future<Integer> studyCountFuture = taskExecutor.submit(this::mapStudies);
        Future<Integer> snpCountFuture = taskExecutor.submit(this::mapSnps);
        Future<Integer> traitCountFuture = taskExecutor.submit(this::mapTraits);
        try {
            int studyCount = studyCountFuture.get();
            int snpCount = snpCountFuture.get();
            int traitCount = traitCountFuture.get();
            System.out.println("done!\n");
            System.out.println(
                    "Successfully mapped " + studyCount + " studies, " + snpCount + " SNPs and " + traitCount +
                            " traits into Solr\n");
        }
        catch (InterruptedException | ExecutionException e) {
            System.out.println("failed!\n");
            throw new RuntimeException("Failed to map one or more documents into Solr", e);
        }
        finally {
            taskExecutor.shutdown();
            System.out.print("Finalizing...");
            int s = 5;
            try {
                taskExecutor.awaitTermination(s, TimeUnit.SECONDS);
                System.out.println("done!\n");
            }
            catch (InterruptedException e) {
                System.out.println("failed!\n");
                System.out.println("The application failed to terminate cleanly in " + s + " seconds.");
            }
        }
    }

    @Transactional Integer mapStudies() {
        ObjectDocumentMapper<Study, StudyDocument> studyMapper =
                new ObjectDocumentMapper<>(StudyDocument.class, studyIndex);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "studyDate"));
        PageRequest page1 = new PageRequest(1, 50, sort);
        List<Study> studies = studyRepository.findAll(page1).getContent();
        ForkJoinPool pool = new ForkJoinPool(1);
        try {
            pool.submit(() -> studies.parallelStream()
                    .map(studyMapper::map)
                    .forEach(studyDocument -> System.out.print("."))).get();
            return studies.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to map one or more studies into Solr", e);
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
                    .forEach(snpDocument -> System.out.print("."))).get();
            return snps.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to map one or more studies into Solr", e);
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
                    .forEach(traitDocument -> System.out.print("."))).get();
            return traits.size();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to map one or more studies into Solr", e);
        }
    }
}
