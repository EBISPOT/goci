package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.goci.curation.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.curation.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.goci.curation.repository.StudyRepository;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitIndex;
import uk.ac.ebi.spot.goci.repository.SnpIndex;
import uk.ac.ebi.spot.goci.repository.StudyIndex;

import javax.transaction.Transactional;
import java.util.Arrays;

@SpringBootApplication
@EnableTransactionManagement
public class SolrIndexBuilder {
    @Autowired SingleNucleotidePolymorphismRepository snpRepository;
    @Autowired StudyRepository studyRepository;
    @Autowired DiseaseTraitRepository diseaseTraitRepository;

    @Autowired SnpIndex snpIndex;
    @Autowired StudyIndex studyIndex;
    @Autowired DiseaseTraitIndex diseaseTraitIndex;

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

    @Transactional
    void convertDocuments() {
        studyRepository.findAll().forEach(
                study -> studyIndex.save(new StudyDocument(study)));
        snpRepository.findAll().forEach(
                snp -> snpIndex.save(new SnpDocument(snp)));
        diseaseTraitRepository.findAll().forEach(
                traitAssociation -> diseaseTraitIndex.save(new DiseaseTraitDocument(traitAssociation)));
    }
}
