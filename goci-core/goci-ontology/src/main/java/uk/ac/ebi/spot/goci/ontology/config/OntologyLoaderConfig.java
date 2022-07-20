package uk.ac.ebi.spot.goci.ontology.config;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.ontology.owl.ReasonedOntologyLoader;

import java.net.URI;
import java.util.Collections;

/**
 * Created by dwelter on 09/02/17.
 */
@Service
public class OntologyLoaderConfig {

    public OntologyLoader setEfoOntologyLoader(Resource efoResource){
        ReasonedOntologyLoader loader = new ReasonedOntologyLoader();
        loader.setOntologyName("efo");
        loader.setOntologyURI(URI.create("http://www.ebi.ac.uk/efo"));
        loader.setOntologyResource(efoResource);
        loader.setExclusionClassURI(URI.create("http://www.geneontology.org/formats/oboInOwl#ObsoleteClass"));
        // loader.setExclusionAnnotationURI(URI.create("http://www.ebi.ac.uk/efo/organizational_class"));
        loader.setSynonymURIs(Collections.singleton(URI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym")));
        loader.init();
        return loader;
    }
}
