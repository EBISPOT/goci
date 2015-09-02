package uk.ac.ebi.spot.goci.pussycat.renderlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.spot.goci.pussycat.layout.SVGDocument;

import java.util.*;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 25/07/14
 */
@Component
public class DefaultRenderletNexus implements RenderletNexus {
    private Set<Renderlet> renderlets;
    private Map<Object, SVGArea> entityLocations;
    private Map<Object, RenderingEvent> renderedEntities;
    private SVGDocument svgDocument;

    private List<Filter> filters;

    private final Logger log = LoggerFactory.getLogger("rendering");

    public DefaultRenderletNexus() {
        this.renderlets = new HashSet<Renderlet>();
        this.entityLocations = new HashMap<Object, SVGArea>();
        this.renderedEntities = new LinkedHashMap<Object, RenderingEvent>();
        this.svgDocument = new SVGDocument(0, 150);
        this.filters = new ArrayList<Filter>();
    }

    protected Logger getLog() {
        return log;
    }

    public boolean register(Renderlet renderlet) {
        getLog().debug("Registering renderlet '" + renderlet.getName() + "' " +
                "(" + renderlet.getDescription() + ") " +
                "[" + renderlet.getClass().getSimpleName() + "]");
        getLog().debug("Renderlets now: " + (renderlets.size() + 1));

        return renderlets.add(renderlet);
    }

    public synchronized <O> void renderingEventOccurred(RenderingEvent<O> evt) {
        entityLocations.put(evt.getRenderedEntity(), evt.getSVGArea());
        renderedEntities.put(evt.getRenderedEntity(), evt);
    }


    @Override
    public <O> SVGArea getLocationOfRenderedEntity(O renderedEntity) {
        return entityLocations.get(renderedEntity);
    }

    @Override
    public String getSVG() {
        StringBuilder svgBuilder = new StringBuilder();
        svgBuilder.append(svgDocument.getHeader());

        // collect rendering events
        List<RenderingEvent> events = new ArrayList<RenderingEvent>();
        for (Object entity : renderedEntities.keySet()) {
            events.add(renderedEntities.get(entity));
        }
        // sort (naturally ordered according to priority)
        Collections.sort(events);
        // and add all the SVG required
        getLog().debug("There are " + events.size() + " stored rendering events");
        for (RenderingEvent re : events) {
            svgBuilder.append(re.getRenderedSVG());
        }
        svgBuilder.append(svgDocument.getFooter());

        return svgBuilder.toString();
    }

    @Override
    public void reset() {
        entityLocations.clear();
        renderedEntities.clear();
    }

    @Override
    public void setRenderingContext(Filter context) {
        filters.add(context);

    }

    @Override
    public List<Filter> getRenderingContext() {
        return filters;
    }

}
