package uk.ac.ebi.spot.goci.ui;

/**
 * Created by emma on 24/11/14.
 * <p>
 * Configuration class for configuring Spring MVC in the application.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // core pages
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/home").setViewName("index");
        registry.addViewController("/search").setViewName("search");
        registry.addViewController("/diagram").setViewName("diagram");
        registry.addViewController("/downloads").setViewName("downloads");
        registry.addViewController("/search/traits").setViewName("traitlist");
        registry.addViewController("/downloads/summary-statistics").setViewName("summary-statistics");
        registry.addRedirectViewController("/search/most-recent", "/search?query=*&filter=recent");
        registry.addViewController("/snp").setViewName("snp-page");
        registry.addViewController("/ancestry").setViewName("ancestry");
        registry.addViewController("/variants").setViewName("variants");

        // dynamically generated docs pages
        registry.addViewController("/docs").setViewName("docs");
        registry.addViewController("/docs/about").setViewName("docs-template");
        registry.addViewController("/docs/file-downloads").setViewName("docs-template");
        registry.addViewController("/docs/diagram-downloads").setViewName("docs-template");
        registry.addViewController("/docs/faq").setViewName("docs-template");
        registry.addViewController("/docs/methods").setViewName("docs-template");
        registry.addViewController("/docs/ontology").setViewName("docs-template");
        registry.addViewController("/docs/abbreviations").setViewName("docs-template");
        registry.addViewController("/docs/fileheaders").setViewName("docs-template");
        registry.addViewController("/docs/related-resources").setViewName("docs-template");
        registry.addViewController("/docs/programmatic-access").setViewName("docs-template");
        registry.addViewController("/docs/known-issues").setViewName("docs-template");
        registry.addViewController("/docs/mappingfileheaders").setViewName("docs-template");
        registry.addViewController("/docs/ancestry").setViewName("docs-template");
        registry.addViewController("/docs/pilots").setViewName("docs-template");

    }
}