package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

/**
 * A renderlet is a small plugin that is capable of rendering small chunks of semantically meaningful data into SVG
 * fragments.  Renderlets can be generically typed by the type of objects they are capable of rendering (usually OWLAPI
 * objects).
 *
 * @author Tony Burdett
 * @author Rob Davey
 * @date 27/02/12
 */
public interface Renderlet<O> {
    /**
     * The name of this renderlet - ideally, should be unique.  Names should be specified in lower case with underscores
     * instead of spaces where possible.
     *
     * @return the string represententing the unique name of this renderlet.
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
     * @param owlEntity the entity we are attempting to render
     * @return true if this renderlet can render the supplied entity
     */
    boolean canRender(RenderletNexus nexus, Object owlEntity);

    /**
     * Render the supplied entity as SVG, and return a valid SVG string
     *
     * @param owlEntity the entity to render
     * @return a well formatted SVG element that can be used to display the supplied owl entity
     */
    String render(RenderletNexus nexus, O owlEntity);
}
