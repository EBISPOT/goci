package uk.ac.ebi.spot.goci;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collections;

/**
 * Created by Dani on 15/02/2016.
 */

@Component
public class ParentMappingConfiguration {

    @NotNull @Value("${efo.location}")
    private Resource efoResource;


    @Bean(name = "parentTermOntologyLoader") ReasonedOntologyLoader ontologyLoader() {
        ReasonedOntologyLoader loader = new ReasonedOntologyLoader();
        loader.setOntologyName("efo");
        loader.setOntologyURI(URI.create("http://www.ebi.ac.uk/efo"));
        loader.setOntologyResource(efoResource);
        loader.setExclusionClassURI(URI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
        loader.setExclusionAnnotationURI(URI.create("http://www.ebi.ac.uk/efo/organizational_class"));
        loader.setSynonymURIs(Collections.singleton(URI.create("http://www.ebi.ac.uk/efo/alternative_term")));
        loader.init();
        return loader;
    }
}
