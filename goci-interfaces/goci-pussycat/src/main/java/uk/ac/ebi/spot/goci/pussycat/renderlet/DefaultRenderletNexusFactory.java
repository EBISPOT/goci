package uk.ac.ebi.spot.goci.pussycat.renderlet;

import uk.ac.ebi.spot.goci.pussycat.session.PussycatSession;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 25/07/14
 */
public class DefaultRenderletNexusFactory implements RenderletNexusFactory {
    @Override public RenderletNexus createRenderletNexus(PussycatSession pussycatSession) {
        return new DefaultRenderletNexus();
    }
}
