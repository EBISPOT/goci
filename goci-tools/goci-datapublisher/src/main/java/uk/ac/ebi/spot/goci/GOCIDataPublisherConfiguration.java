package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.ontology.config.OntologyLoaderConfig;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;

/**
 * Created by dwelter on 10/06/15.
 */
@Component
public class GOCIDataPublisherConfiguration {


    @NotNull @Value("${efo.location}")
    private Resource efoResource;

    private OntologyLoaderConfig ontologyLoaderConfig;

    @Autowired
    public GOCIDataPublisherConfiguration(OntologyLoaderConfig ontologyLoaderConfig){
        this.ontologyLoaderConfig = ontologyLoaderConfig;
    }

    @Bean OntologyLoader ontologyLoader() {
        return ontologyLoaderConfig.setEfoOntologyLoader(efoResource);
    }
}
