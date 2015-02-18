package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.ProcessView;

/**
 * Created by emma on 17/02/15.
 */
@SpringBootApplication
public class Application {

    @Autowired ProcessView processView;

    public static void main(String[] args) {
        System.out.println("Starting catalog download service...");
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            processView.createFileForNcbi();
            System.out.print("Converting all GWAS database objects...");
            System.out.println("done!\n");
        };
    }

}