package uk.ac.ebi.spot.goci;

/**
 * Created by dwelter on 04/11/16.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RestApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
