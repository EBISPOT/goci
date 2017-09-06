package uk.ac.ebi.spot.goci.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by dwelter on 10/11/16.
 */
@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET")
//                .allowedHeaders("header1", "header2", "header3")
//                .exposedHeaders("header1", "header2")
                .allowCredentials(false).maxAge(3600);
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // core pages
//        registry.addViewController("/").setViewName("index");
////        registry.addViewController("/docs/index").setViewName("docs-template");
////        registry.addViewController("/docs/api").setViewName("docs-template");
//
//
//    }
}