package uk.ac.ebi.spot.goci.pussycat.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;

import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;

/**
 * An abstract pussycat session that simply provides session ID definitions
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
@Component
public abstract class AbstractPussycatSession implements PussycatSession {
    private Collection<Renderlet> renderlets;

    private Logger log = LoggerFactory.getLogger("rendering");

    protected AbstractPussycatSession() {
        //        this.sessionID = generateSessionID();
        //        this.renderlets = getAvailableRenderlets();
    }


    protected Logger getLog() {
        return log;
    }


    public Collection<Renderlet> getAvailableRenderlets() {
        if (renderlets == null) {
            ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
            Collection<Renderlet> loadedRenderlets = new HashSet<Renderlet>();
            for (Renderlet renderlet : renderletLoader) {
                loadedRenderlets.add(renderlet);
            }
            getLog().debug("Loaded " + loadedRenderlets.size() + " renderlets");
            this.renderlets = loadedRenderlets;
        }
        return renderlets;
    }

}
