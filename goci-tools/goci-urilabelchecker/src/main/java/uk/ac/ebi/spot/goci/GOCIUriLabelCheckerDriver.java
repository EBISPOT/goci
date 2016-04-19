package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.spot.goci.service.CheckingService;

@SpringBootApplication
public class GOCIUriLabelCheckerDriver {
    @Autowired
    CheckingService checker;

    public static void main(String[] args) {
        System.out.println("Starting URI-label checker application...");
        ApplicationContext ctx = SpringApplication.run(GOCIUriLabelCheckerDriver.class, args);
        System.out.println("Application executed successfully!");
        SpringApplication.exit(ctx);
    }

    @Bean CommandLineRunner run() {
        return strings -> {
            System.out.println("About to check all URI-label pairs");
            checker.checkURIs();
            System.out.println("All URIs-label pairs checked");
        };
    }

}
