package uk.ac.ebi.spot.goci;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jstewart on 23/07/2019
 */
@SpringBootApplication
public class DepositionSyncApp implements CommandLineRunner {

    @Autowired
    private DepositionSyncService syncService;

    @Bean(name = "JodaMapper")
    @Primary
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule()).configure(SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false).configure(SerializationFeature.INDENT_OUTPUT, true).setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        converter.setPrettyPrint(true);
        return converter;
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        //find and replace Jackson message converter with our own
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            final HttpMessageConverter<?> httpMessageConverter = restTemplate.getMessageConverters().get(i);
            if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                restTemplate.getMessageConverters().set(i, mappingJackson2HttpMessageConverter());
            }
        }

        return restTemplate;
    }

    @Override
    public void run(String... args) {
        long start_time = System.currentTimeMillis();

        if (args.length != 0 && args[0].equalsIgnoreCase("initial")) {
            // execute publisher
            syncService.syncPublications(true);
        } else if (args.length != 0 && args[0].equalsIgnoreCase("unpublished")) {
            // execute publisher
            syncService.syncUnpublishedStudies();
        } else if (args.length != 0 && args[0].equalsIgnoreCase("fix")) {
            // execute publisher
            syncService.fixPublications();
        } else {
            syncService.syncPublications(false);
        }
        long end_time = System.currentTimeMillis();
        String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
        System.out.println("Completed sync in " + time + " s. - application will now exit");
    }

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DepositionSyncApp.class);
        System.out.println("Starting Goci Deposition Sync...");
        SpringApplication app =
                builder.web(false).addCommandLineProperties(true).build(args);
        ApplicationContext ctx = app.run(args);
        System.out.println("Application executed successfully!");
        app.exit(ctx);
    }
}
