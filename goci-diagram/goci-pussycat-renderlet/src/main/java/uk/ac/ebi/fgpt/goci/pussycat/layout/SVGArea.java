package uk.ac.ebi.fgpt.goci.pussycat.layout;

/**
 * A simple class that represents a region of SVG occupied by a glyph.  This area has an x and y co-ordinate (relative
 * to the SVG document), a width and a height, and a z-index (representing whether the glyph appears in the foreground
 * or background).
 *
 * @author Tony Burdett
 * @date 27/02/12
 */
public class SVGArea {
    private double x, y;
    private double width, height;
    private int zIndex;

    public SVGArea(double x, double y, double width, double height, int zIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int get_zIndex() {
        return zIndex;
    }
}
