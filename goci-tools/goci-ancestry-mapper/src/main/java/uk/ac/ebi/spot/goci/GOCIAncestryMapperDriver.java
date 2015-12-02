package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.AncestryMappingService;

/**
 * Created by dwelter on 06/10/15.
 */

@SpringBootApplication
public class GOCIAncestryMapperDriver {

    @Autowired
    AncestryMappingService mappingService;

    private AncestryMappingService getMappingService() {
        return mappingService;
    }

    public static void main(String[] args) {
        System.out.println("Starting ancestry mapping application...");
        ApplicationContext ctx = SpringApplication.run(GOCIAncestryMapperDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            System.out.println("About to map all database values");
            this.mapAncestry();
            System.out.println("Mapping complete");
        };
    }

    private void mapAncestry() {
        getMappingService().processAncestries();

    }

}
