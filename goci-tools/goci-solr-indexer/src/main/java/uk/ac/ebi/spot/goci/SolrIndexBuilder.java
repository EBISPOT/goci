package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.goci.index.TraitIndex;
import uk.ac.ebi.spot.goci.index.SnpIndex;
import uk.ac.ebi.spot.goci.index.StudyIndex;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.TraitDocument;
import uk.ac.ebi.spot.goci.model.ObjectDocumentMapper;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.service.SingleNucleotidePolymorphismService;

import java.util.Arrays;

@SpringBootApplication
@EnableTransactionManagement
public class SolrIndexBuilder {
    @Autowired StudyRepository studyRepository;
    @Autowired SingleNucleotidePolymorphismService snpService;
    @Autowired DiseaseTraitRepository diseaseTraitRepository;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired TraitIndex diseaseTraitIndex;

    public static void main(String[] args) {
        System.out.println("Starting Solr indexing application...");
        ApplicationContext ctx = SpringApplication.run(SolrIndexBuilder.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            convertDocuments();
            System.out.println("Indexing building complete - application will now exit");
        };
    }

    void convertDocuments() {
        System.out.print("Converting studies");
        ObjectDocumentMapper<Study, StudyDocument> studyMapper =
                new ObjectDocumentMapper<>(StudyDocument.class, studyIndex, true);
        studyRepository.findAll().forEach(studyMapper::map);
        System.out.println("done!");

        System.out.print("Converting SNPs");
        ObjectDocumentMapper<SingleNucleotidePolymorphism, SnpDocument> snpMapper =
                new ObjectDocumentMapper<>(SnpDocument.class, snpIndex, true);
        snpService.deepFindAll().forEach(snpMapper::map);
        System.out.println("done!");

        System.out.print("Converting disease traits");
        ObjectDocumentMapper<DiseaseTrait, TraitDocument> traitMapper =
                new ObjectDocumentMapper<>(TraitDocument.class, diseaseTraitIndex, true);
        diseaseTraitRepository.findAll().forEach(traitMapper::map);
        System.out.println("done!");
    }
}
