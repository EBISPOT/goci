package uk.ac.ebi.spot.goci.pussycat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Created by dwelter on 13/07/15.
 */

@Component
public class PussycatConfiguration {

    @NotNull @Value("${sparql.endpoint.url}")
    private URL sparqlServer;

    public URL getSparqlServer() {
        return sparqlServer;
    }
}
