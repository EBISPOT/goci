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
 * Created by dwelter on 29/09/15.
 */

@Component
public class GOCIUriLabelCheckerConfiguration {


    @NotNull
    @Value("${efo.location}")
    private Resource efoResource;

    private OntologyLoaderConfig ontologyLoaderConfig;

    @Autowired
    public GOCIUriLabelCheckerConfiguration(OntologyLoaderConfig ontologyLoaderConfig){
        this.ontologyLoaderConfig = ontologyLoaderConfig;
    }

    @Bean OntologyLoader ontologyLoader() {
        ReasonedOntologyLoader loader = ontologyLoaderConfig.setEfoOntologyLoader(efoResource);
        return loader;
    }
}
