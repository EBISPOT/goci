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
 *
 * @author emma
 *         <p>
 *         Application to create a text file that can be sent to NCBI pipeline.
 *         Takes a file name as an arguement
 */
@SpringBootApplication
public class DownloadApplication {

    @Autowired
    private ProcessView processView;

    public static void main(String... args) {
        System.out.println("Starting catalog download service...");
        ApplicationContext ctx = SpringApplication.run(DownloadApplication.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }


    @Bean CommandLineRunner run() {
        return strings -> {

            System.out.println("Querying database for studies ready to send to NCBI...");
            List<String> serialisedViews = processView.serialiseViews();

            String fileName = strings[0];
            processView.createFileForNcbi(fileName, serialisedViews);
            System.out.println("Writing " + strings[0]);
        };

    }

}