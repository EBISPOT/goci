package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import uk.ac.ebi.spot.goci.service.SolrIndexer;

import java.util.Arrays;

@SpringBootApplication
public class SolrIndexerApplication {
    @Autowired SolrIndexer solrIndexer;
    @Value("${solr_index.all_threads:false}")
    private boolean allThreads = false;


    public static void main(String[] args) {
        System.out.println("Starting Solr indexing application...");
        ApplicationContext ctx = new SpringApplicationBuilder(SolrIndexerApplication.class).web(false).run(args);
        //ApplicationContext ctx = SpringApplicationBuilder.run(SolrIndexerApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            long start_time = System.currentTimeMillis();
            System.out.println("Building indexes with supplied params: " + Arrays.toString(strings));
            solrIndexer.enableSysOutLogging();
            System.out.print("Converting all GWAS database objects...");
            int docCount;
            if(allThreads) {
                docCount = solrIndexer.fetchAndIndexAllThreads();
            }else{
                docCount = solrIndexer.fetchAndIndex();
            }
            System.out.println("done!\n");
            long end_time = System.currentTimeMillis();
            String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
            System.out.println("Successfully mapped " + docCount + " documents into the GWAS solr index\n");
            System.out.println("Indexing building complete in " + time + " s. - application will now exit");
        };
    }
}
