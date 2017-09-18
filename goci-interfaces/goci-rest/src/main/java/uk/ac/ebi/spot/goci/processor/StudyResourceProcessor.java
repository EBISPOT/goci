package uk.ac.ebi.spot.goci.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.Study;

/**
 * Created by dwelter on 13/09/17.
 */

@Component
public class StudyResourceProcessor implements ResourceProcessor<Resource<Study>> {

    @Autowired
    private RepositoryRestMvcConfiguration configuration;

    @Override
    public Resource<Study> process(Resource<Study> resource) {

        LinkBuilder link = configuration.entityLinks().linkForSingleResource(Study.class, resource.getContent().getAccessionId());
        resource.add(link.slash("/associations?projection=associationByStudy").withRel("associationsByStudySummary"));

        return resource;
    }
}
