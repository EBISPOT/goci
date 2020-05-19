package uk.ac.ebi.spot.goci.pussycat.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.pussycat.renderlet.Renderlet;

import java.util.*;

/**
 * An abstract pussycat session that simply provides session ID definitions
 *
 * @author Tony Burdett
 * @date 03/08/12
 */
@Component
public abstract class AbstractPussycatSession implements PussycatSession {
    private Collection<Renderlet> renderlets;
    private Map<Class, Renderlet> renderletMap;

    private Logger log = LoggerFactory.getLogger("rendering");

    protected AbstractPussycatSession() {
        //        this.sessionID = generateSessionID();
        //        this.renderlets = getAvailableRenderlets();
    }

    private void initRenderlets(){
        ServiceLoader<Renderlet> renderletLoader = ServiceLoader.load(Renderlet.class);
        renderlets = new HashSet<Renderlet>();
        renderletMap = new HashMap<>();
        for (Renderlet renderlet : renderletLoader) {
            renderlets.add(renderlet);
            renderletMap.put(renderlet.getClass(), renderlet);
        }
        getLog().debug("Loaded " + renderlets.size() + " renderlets");

    }

    protected Logger getLog() {
        return log;
    }

    public Renderlet getRenderlet(Class renderletClass){
        if(renderletMap == null){
            initRenderlets();
        }
        return renderletMap.get(renderletClass);
    }

    public Collection<Renderlet> getAvailableRenderlets() {
        if(renderlets == null){
            initRenderlets();
        }
        return renderlets;
    }

}
