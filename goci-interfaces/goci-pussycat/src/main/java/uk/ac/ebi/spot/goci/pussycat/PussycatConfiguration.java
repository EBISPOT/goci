package uk.ac.ebi.spot.goci.pussycat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.goci.pussycat.listener.PussycatAwareHttpSessionListener;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSessionListener;

/**
 * Created by dwelter on 13/07/15.
 */

@Configuration
public class PussycatConfiguration {

        @Bean PussycatSessionStrategy pussycatSessionStrategy() {
            return PussycatSessionStrategy.JOIN;
        }

    @Bean
    public HttpSessionListener httpSessionListener(){
        return new PussycatAwareHttpSessionListener();
    }

}
