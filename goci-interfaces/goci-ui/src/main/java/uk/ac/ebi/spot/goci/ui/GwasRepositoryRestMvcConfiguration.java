package uk.ac.ebi.spot.goci.ui;

        import org.springframework.context.annotation.Configuration;
        import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
        import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

        import java.net.URI;

/**
 * Created by emma on 11/12/14.
 * @author emma
 */
@Configuration
public class GwasRepositoryRestMvcConfiguration extends RepositoryRestMvcConfiguration {
    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setBaseUri(URI.create("/api"));
    }
}
