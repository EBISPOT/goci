package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.w3c.dom.Element;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;

/**
 * An object that encapsulates key pieces of information about a rendering event.  This includes information about the
 * entity rendered, the renderlet that rendered it, and the size and position of the glyph that was rendered
 *
 * @author Tony Burdett
 * Date 27/02/12
 */
public class RenderingEvent<O> {
    private O renderedEntity;
    private Element renderedSVG;

    private SVGArea svgArea;

    private Renderlet renderingRenderlet;

    public RenderingEvent(O renderedEntity,
                          Element renderedSVG,
                          SVGArea svgArea,
                          Renderlet renderingRenderlet) {
        this.renderedEntity = renderedEntity;
        this.renderedSVG = renderedSVG;
        this.svgArea = svgArea;
        this.renderingRenderlet = renderingRenderlet;
    }

    public O getRenderedEntity() {
        return renderedEntity;
    }

    public Element getRenderedSVG() {
        return renderedSVG;
    }

    public SVGArea getSvgArea() {
        return svgArea;
    }

    public void setSvgArea(SVGArea svgArea) {
        this.svgArea = svgArea;
    }

    public Renderlet getRenderingRenderlet() {
        return renderingRenderlet;
    }
}
