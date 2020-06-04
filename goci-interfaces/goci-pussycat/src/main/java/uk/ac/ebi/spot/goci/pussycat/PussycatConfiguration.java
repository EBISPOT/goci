package uk.ac.ebi.spot.goci.pussycat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import uk.ac.ebi.spot.goci.ontology.config.OntologyLoaderConfig;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.pussycat.listener.PussycatAwareHttpSessionListener;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSessionListener;
import javax.validation.constraints.NotNull;

/**
 * Created by dwelter on 13/07/15.
 */

@Configuration
public class PussycatConfiguration {

    @Bean PussycatSessionStrategy pussycatSessionStrategy() {
        return PussycatSessionStrategy.JOIN;
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new PussycatAwareHttpSessionListener();
    }

    @NotNull
    @Value("${efo.location}")
    private Resource efoResource;

    @Autowired
    OntologyLoaderConfig ontologyLoaderConfig;

    @Bean
    OntologyLoader ontologyLoader() {
        OntologyLoader loader = ontologyLoaderConfig.setEfoOntologyLoader(efoResource);
        return loader;
    }
}
