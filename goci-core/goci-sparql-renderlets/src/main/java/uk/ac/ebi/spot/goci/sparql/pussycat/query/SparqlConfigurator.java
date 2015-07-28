package uk.ac.ebi.spot.goci.sparql.pussycat.query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.fgpt.lode.impl.JenaHttpExecutorService;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Created by Dani on 16/07/2015.
 */
@Component
public class SparqlConfigurator {

    @NotNull @Value("${lode.sparqlendpoint.url}")
    private URL sparqlServer;

    @Bean JenaQueryExecutionService jenaQueryExecutionService() {

        return new JenaHttpExecutorService();
    }


    @Bean(name = "prefixProperties")
    public PropertiesFactoryBean mapper() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("prefix.properties"));
        return bean;
    }

}
