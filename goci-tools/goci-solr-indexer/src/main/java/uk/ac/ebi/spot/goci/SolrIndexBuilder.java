package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.GwasSolrIndexer;

import java.util.Arrays;

@SpringBootApplication
public class SolrIndexBuilder {
    @Autowired GwasSolrIndexer gwasSolrIndexer;

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
            gwasSolrIndexer.enableSysOutLogging();
            System.out.print("Converting all GWAS database objects...");
            int docCount = gwasSolrIndexer.fetchAndIndex();
            System.out.println("done!\n");
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Successfully mapped " + docCount + " documents into the GWAS solr index\n");
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
        };
    }
}
