package uk.ac.ebi.spot.goci.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.EfoTrait;

/**
 * Created by dwelter on 13/09/17.
 */

@Component
public class EfoTraitResourceProcessor implements ResourceProcessor<Resource<EfoTrait>> {

    @Autowired
    private RepositoryRestMvcConfiguration configuration;

    @Override
    public Resource<EfoTrait> process(Resource<EfoTrait> resource) {

        LinkBuilder link = configuration.entityLinks().linkForSingleResource(EfoTrait.class, resource.getContent().getShortForm());
        resource.add(link.slash("/associations?projection=associationByEfoTrait").withRel("associationsByTraitSummary"));

        return resource;
    }
}
