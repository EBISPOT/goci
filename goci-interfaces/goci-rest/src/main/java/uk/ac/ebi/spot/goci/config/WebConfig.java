package uk.ac.ebi.spot.goci.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * Created by dwelter on 10/11/16.
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET")
                .allowCredentials(false).maxAge(3600);
    }

/*** The config below is needed in order for PersistentEntityResourceAssembler to work in a @RestController
 * Otherwise it works only with a @RepositoryRestController
    ***/

    @Autowired
    @Qualifier("repositoryExporterHandlerAdapter")
    RequestMappingHandlerAdapter repositoryExporterHandlerAdapter;

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> argumentResolvers) {
        List<HandlerMethodArgumentResolver> customArgumentResolvers = repositoryExporterHandlerAdapter.getCustomArgumentResolvers();
        argumentResolvers.addAll(customArgumentResolvers);
    }
}