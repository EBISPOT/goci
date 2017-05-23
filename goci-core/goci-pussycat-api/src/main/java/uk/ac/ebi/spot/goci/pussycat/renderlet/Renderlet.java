package uk.ac.ebi.spot.goci.pussycat.renderlet;

import uk.ac.ebi.spot.goci.spi.Spi;

/**
 * A renderlet is a small plugin that is capable of rendering small chunks of semantically meaningful data into SVG
 * fragments.  Renderlets can be generically typed by the type of objects they are capable of rendering (usually OWLAPI
 * objects).
 *
 * @author Tony Burdett
 * @author Rob Davey Date 27/02/12
 */
@Spi
public interface Renderlet<C, O> {
    /**
     * The name of this renderlet - ideally, should be unique.  Names should be specified in lower case with underscores
     * instead of spaces where possible.
     *
     * @return the string representing the unique name of this renderlet.
     */
    String getName();

    /**
     * The name of the renderlet that can be displayed in user menus for configurable views.
     *
     * @return this renderlet display name
     */
    String getDisplayName();

    /**
     * A simple description of this renderlet, what it displays and how it displays it
     *
     * @return the renderlet description
     */
    String getDescription();

    /**
     * Returns true if this renderlet is capable of rendering the supplied object as SVG, false if it cannot
     *
     * @param nexus            the renderlet nexus, allowing renderlets to query for dependencies on other renderings
     * @param renderingContext the context in which this entity should be rendered
     * @param renderingEntity  the entity we are attempting to render  @return true if this renderlet can render the
     *                         supplied entity
     * @return true if this renderlet can render the given entity, false otherwise
     */
    boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity);

    /**
     * Render the supplied entity as SVG, and return a valid SVG string
     *
     * @param nexus            the renderlet nexus, allowing renderlets to query for dependencies on other renderings
     * @param renderingContext the context in which this entity should be rendered
     * @param renderingEntity  the entity to render
     */
    void render(RenderletNexus nexus, C renderingContext, O renderingEntity);
}
