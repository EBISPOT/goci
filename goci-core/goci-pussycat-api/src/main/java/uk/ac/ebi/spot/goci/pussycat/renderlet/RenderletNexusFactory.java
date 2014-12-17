package uk.ac.ebi.spot.goci.pussycat.renderlet;

import uk.ac.ebi.spot.goci.pussycat.session.PussycatSession;

/**
 * An interface describing a factory class for instances of {@link uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus}
 *
 * @author Tony Burdett
 * @date 02/06/14
 */
public interface RenderletNexusFactory {
    /**
     * Creates a renderlet nexus that is associated with the given pussycat session, so the renderlet nexus can acquire
     * data from the pussycat session if required
     *
     * @param pussycatSession the pussycat session storing required visualisation data
     * @return a newly created renderlet nexus
     */
    RenderletNexus createRenderletNexus(PussycatSession pussycatSession);
}
