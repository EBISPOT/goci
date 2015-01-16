package uk.ac.ebi.spot.goci.ui;

/**
 * Created by emma on 24/11/14.
 *
 * Configuration class for configuring Spring MVC in the application.
 *
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("home");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/search").setViewName("search");
        registry.addViewController("/ontology").setViewName("ontology");
        registry.addViewController("/downloads").setViewName("downloads");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/faq").setViewName("faq");
        registry.addViewController("/methods").setViewName("methods");

        //  registry.addViewController("/login").setViewName("login");
      //  registry.addViewController("/studies").setViewName("studies");
    }
}