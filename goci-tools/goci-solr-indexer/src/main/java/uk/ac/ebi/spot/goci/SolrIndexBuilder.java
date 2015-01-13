package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.goci.curation.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.curation.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.curation.model.Study;
import uk.ac.ebi.spot.goci.curation.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.curation.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.goci.curation.repository.StudyRepository;
import uk.ac.ebi.spot.goci.model.DiseaseTraitDocument;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitIndex;
import uk.ac.ebi.spot.goci.repository.SnpIndex;
import uk.ac.ebi.spot.goci.repository.StudyIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    void convertDocuments() {
        System.out.print("Converting studies");
        DocumentConverter<StudyDocument, Study> studyConverter =
                new DocumentConverter<>(StudyDocument.class, studyIndex, true);
        studyRepository.findAll().forEach(studyConverter::convert);
        System.out.println("done!");

        System.out.print("Converting SNPs");
        DocumentConverter<SnpDocument, SingleNucleotidePolymorphism> snpConverter =
                new DocumentConverter<>(SnpDocument.class, snpIndex, true);
        snpRepository.findAll().forEach(snpConverter::convert);
        System.out.println("done!");

        System.out.print("Converting disease traits");
        DocumentConverter<DiseaseTraitDocument, DiseaseTrait> traitConverter =
                new DocumentConverter<>(DiseaseTraitDocument.class, diseaseTraitIndex, true);
        diseaseTraitRepository.findAll().forEach(traitConverter::convert);
        System.out.println("done!");
    }

    class DocumentConverter<D, O> {
        private boolean progressEnabled = false;
        private Class<?> documentType;
        private SolrCrudRepository<D, String> index;

        public DocumentConverter(Class<D> documentType, SolrCrudRepository<D, String> index) {
            this.documentType = documentType;
            this.index = index;
        }

        public DocumentConverter(Class<D> documentType, SolrCrudRepository<D, String> index, boolean enableProgress) {
            this(documentType, index);
            this.progressEnabled = enableProgress;
        }

        void convert(O object) {
            try {
                Class<?> paramType = documentType.getDeclaredConstructors()[0].getParameterTypes()[0];
                if (paramType.isAssignableFrom(object.getClass())) {
                    if (progressEnabled) {
                        System.out.print(".");
                    }
                    D document =
                            (D) documentType.getDeclaredConstructor(object.getClass()).newInstance(object);
                    index.save(document);
                }
                else {
                    throw new RuntimeException(
                            "Object of type '" + object.getClass().getName() + "' is of wrong type to convert " +
                                    "to document type '" + documentType.getName() + "'");
                }
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
