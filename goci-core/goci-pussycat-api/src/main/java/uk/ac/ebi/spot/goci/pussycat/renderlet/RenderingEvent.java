package uk.ac.ebi.spot.goci.pussycat.renderlet;

import uk.ac.ebi.spot.goci.pussycat.layout.SVGArea;

/**
 * An object that encapsulates key pieces of information about a rendering event.  This includes information about the
 * entity rendered, the renderlet that rendered it, and the size and position of the glyph that was rendered
 *
 * @author Tony Burdett
 * @date 27/02/12
 */
public class RenderingEvent<O> implements Comparable<RenderingEvent<O>> {
    private O renderedEntity;
    private String renderedSVG;

    private SVGArea svgArea;

    private int priority;

    private Renderlet renderingRenderlet;

    public RenderingEvent(O renderedEntity,
                          String renderedSVG,
                          SVGArea svgArea,
                          Renderlet renderingRenderlet) {
        this(renderedEntity, renderedSVG, svgArea, 0, renderingRenderlet);
    }

    public RenderingEvent(O renderedEntity,
                          String renderedSVG,
                          SVGArea svgArea,
                          int priority,
                          Renderlet renderingRenderlet) {
        this.renderedEntity = renderedEntity;
        this.renderedSVG = renderedSVG;
        this.svgArea = svgArea;
        this.priority = priority;
        this.renderingRenderlet = renderingRenderlet;
    }

    public O getRenderedEntity() {
        return renderedEntity;
    }

    public String getRenderedSVG() {
        return renderedSVG;
    }

    public int getPriority() {
        return priority;
    }

    public SVGArea getSVGArea() {
        return svgArea;
    }

    public Renderlet getRenderingRenderlet() {
        return renderingRenderlet;
    }

    @Override public int compareTo(RenderingEvent<O> o) {
        return getPriority() - o.getPriority();
    }
}
