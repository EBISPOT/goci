package uk.ac.ebi.spot.goci;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.service.StudyService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootApplication
public class OracleDBTest implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AbstractEnvironment environment;

    @PostConstruct
    public void printProperties() {

        System.out.println("**** APPLICATION PROPERTIES SOURCES ****");

        Set<String> properties = new TreeSet<>();
        for (PropertiesPropertySource p : findPropertiesPropertySources()) {
            System.out.println(p.toString());
            properties.addAll(Arrays.asList(p.getPropertyNames()));
        }

        System.out.println("**** APPLICATION PROPERTIES VALUES ****");
        print(properties);

    }

    private List<PropertiesPropertySource> findPropertiesPropertySources() {
        List<PropertiesPropertySource> propertiesPropertySources = new LinkedList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof PropertiesPropertySource) {
                propertiesPropertySources.add((PropertiesPropertySource) propertySource);
            }
        }
        return propertiesPropertySources;
    }

    private void print(Set<String> properties) {
        for (String propertyName : properties) {
            System.out.println(propertyName + " = " + environment.getProperty(propertyName));
        }
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


    @Bean(name = "JodaMapper")
    @Primary
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule()).configure(SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false).configure(SerializationFeature.INDENT_OUTPUT, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        converter.setPrettyPrint(true);
        return converter;
    }

    @Autowired
    private StudyService studyService;

    private void testDBConnections(int threads) {
        Session session = (Session) entityManager.getDelegate();

        ExecutorService service = Executors.newFixedThreadPool(threads);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        //      String dsName = dataSource.getDriverClassName();
        System.out.println("Starting threads");
        int pageSize = 10;
        int maxPages = 10;
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread("DB Test thread " + i) {
                public void run() {
                    Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "publicationId.publicationDate"));
                    Pageable pager = new PageRequest(0, pageSize, sort);
                    Page<Study> studyPage = studyService.findPublishedStudies(pager);
                    while (studyPage.hasNext()) {
                        if (maxPages != -1 && studyPage.getNumber() >= maxPages - 1) {
                            break;
                        }
                        pager = pager.next();
                        studyPage = studyService.findPublishedStudies(pager);
                        System.out.println(getName() + " page number " + pager.getOffset());
                    }
                }
            };
            Future f = service.submit(t);
            futures.add(f);
        }

        System.out.println("Waiting for threads to finish.");
        for (Future<Runnable> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //shut down the executor service so that this thread can exit
        service.shutdownNow();
    }

    @Override
    public void run(String... args) {
        long start_time = System.currentTimeMillis();

        if (args.length != 0 && Character.isDigit(args[0].codePointAt(0))) {
            testDBConnections(Integer.parseInt(args[0]));
        } else {
            testDBConnections(1);
        }
        long end_time = System.currentTimeMillis();
        String time = String.format("%.1f", ((double) (end_time - start_time)) / 1000);
        System.out.println("Completed sync in " + time + " s. - application will now exit");
    }

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(OracleDBTest.class);
        System.out.println("Starting Oracle DBTest...");
        SpringApplication app = builder.web(false).addCommandLineProperties(true).build(args);
        ApplicationContext ctx = app.run(args);
        System.out.println("Application executed successfully!");
        app.exit(ctx);
    }
}
