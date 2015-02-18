package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.ProcessView;

import java.util.List;

/**
 * Created by emma on 17/02/15.
 */
@SpringBootApplication
public class DownloadApplication {

    @Autowired ProcessView processView;

    public static void main(String[] args) {
        System.out.println("Starting catalog download service...");
        ApplicationContext ctx = SpringApplication.run(DownloadApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run(String... args) {
        return strings -> {
            System.out.print("Querying database for studies ready to send to NCBI...");
            List<String> viewsAsStrings = processView.serialiseViews();
            System.out.print("Querying database for studies ready to send to NCBI...");
            String fileName = args.toString();
            processView.createFileForNcbi(fileName, viewsAsStrings);
            System.out.println("done!\n");
        };
    }

}