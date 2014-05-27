package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.IRI;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

import java.util.Map;
import java.util.Set;

/**
 * A RenderletNexus represents the intersection between Renderlets, allowing Renderlets that are dependent on each
 * others output to communicate.  Renderlets are fundamentally stateless, and can render their SVG in a stateless
 * manner, but where two entities might need to be rendered interdependently (for example, as distinct but connected
 * glyphs, or where one glyph must always appear in front of the other), the RenderletNexus is required to act as a
 * mediator in the rendering operation.
 *
 * @author Tony Burdett
 * @author Rob Davey
 * Date 27/02/12
 */
public interface RenderletNexus<S> {
    /**
     * Register a renderlet to this nexus.
     *
     * @param renderlet a renderlet to register against this nexus, notifying of updates
     * @return true if the registration was successful
     */
    boolean register(Renderlet renderlet);


    void setEfoLabels(Map<IRI, String> efoLabels);

    Map<IRI, String> getEfoLabels();

    /**
     * Called whenever a renderlet renders an entity
     *
     * @param evt the rendering event that occurred
     */
    <O> void renderingEventOccurred(RenderingEvent<O> evt);

    /**
     * Gets the area of SVG, relative to the whole SVG canvas, in which the supplied entity was rendered.
     *
     * @param renderedEntity the entity being rendered
     * @param <O>            the type of entity that was rendered
     * @return the area of svg in which this entity was rendered
     */
    <O> SVGArea getLocationOfEntity(O renderedEntity);


    /**
     * Gets a string of SVG to be rendered
     *
     * @param renderableSelection the expression to be rendered
     * @return the SVG in the form of a string
     */
    String getSVG(S renderableSelection);

    Set<Renderlet> getRenderlets();

    <O> RenderingEvent getRenderingEvent(O renderedEntity);


}
