package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A factory that is capable of producing {@link RenderletNexus} instances.
 *
 * @author Tony Burdett
 * @date 01/03/12
 */
public class RenderletNexusFactory {
    public static RenderletNexus createDefaultRenderletNexus() {
        return new DefaultRenderletNexus();
    }

    private static final class DefaultRenderletNexus implements RenderletNexus {
        private Set<Renderlet> renderlets;
        private Map<Object, SVGArea> renderedEntityLocations;

        private DefaultRenderletNexus() {
            this.renderlets = new HashSet<Renderlet>();
            this.renderedEntityLocations = new HashMap<Object, SVGArea>();
        }

        public boolean register(Renderlet renderlet) {
            return renderlets.add(renderlet);
        }

        public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
            renderedEntityLocations.put(evt.getRenderedEntity(), evt.getSvgArea());
        }

        public <O> SVGArea getLocationOfRenderedEntity(O owlEntity) {
            return renderedEntityLocations.get(owlEntity);
        }
    }
}
