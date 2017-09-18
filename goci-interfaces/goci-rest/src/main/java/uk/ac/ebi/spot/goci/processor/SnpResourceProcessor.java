package uk.ac.ebi.spot.goci.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

/**
 * Created by dwelter on 13/09/17.
 */

@Component
public class SnpResourceProcessor implements ResourceProcessor<Resource<SingleNucleotidePolymorphism>> {

    @Autowired
    private RepositoryRestMvcConfiguration configuration;

    @Override
    public Resource<SingleNucleotidePolymorphism> process(Resource<SingleNucleotidePolymorphism> resource) {

        LinkBuilder link = configuration.entityLinks().linkForSingleResource(SingleNucleotidePolymorphism.class, resource.getContent().getRsId());
        resource.add(link.slash("/associations?projection=associationBySnp").withRel("associationsBySnpSummary"));

        return resource;
    }
}
