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
      //  registry.addViewController("/login").setViewName("login");
      //  registry.addViewController("/studies").setViewName("studies");
    }
}