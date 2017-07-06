package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.DataDeletionService;

import java.util.Arrays;

/**
 * Created by dwelter on 09/01/17.
 */

@SpringBootApplication
public class GociDataExportDriver {

    @Autowired
    DataDeletionService dataDeletionervice;

    public static void main(String[] args) {
        System.out.println("Starting public data export application...");
        ApplicationContext ctx = SpringApplication.run(GociDataExportDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            System.out.println("Running application with supplied params: " + Arrays.toString(strings));

            dataDeletionervice.deleteNonPublicStudies();
            System.out.println("Export complete - application will now exit");
        };
    }
}
