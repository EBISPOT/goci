package uk.ac.ebi.spot.goci.pussycat.utils;

/**
 * Some static utils for working with and constructing SVG elements
 *
 * @author Tony Burdett
 * @author Rob Davey
 */
public class SVGUtils {
    public static String buildSVGHeader(String id) {
        return buildSVGHeader(id, "", 0, 0);
    }

    public static String buildSVGHeader(String id, int defaultWidth, int defaultHeight) {
        return buildSVGHeader(id, "", defaultWidth, defaultHeight);
    }

    public static String buildSVGHeader(String id, String onloadEvt, int defaultWidth, int defaultHeight) {
        String svgid = "id='" + id + "'";
//        String version = "version='1.1'";
//        String baseprofile = "baseProfile='full'";
        String xmlns = "xmlns='http://www.w3.org/2000/svg' " +
                "xmlns:svg='http://www.w3.org/2000/svg' " +
                "xmlns:xlink='http://www.w3.org/1999/xlink' " +
                "xmlns:sodipodi='http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd'";

        String width = "width='" + defaultWidth + "'";
        String height = "height='" + defaultHeight + "'";
        String viewBox = "viewBox='0 0 " + defaultWidth + " " + defaultHeight + "' " +
                "preserveAspectRatio='xMinYMin slice'";

//        String onload = "onload='" + onloadEvt + "'";
//        String onzoom = "onzoom=''";
//        String onscroll = "onscroll=''";
//        String onresize = "onresize=''";

        String blacktobg_def = "<linearGradient id='blacktobg' x1='0%' y1='0%' x2='0%' y2='100%'>" +
                "<stop offset='0%' style='stop-color:black; stop-opacity:1'/>" +
                "<stop offset='100%' style='stop-color:#fffaea; stop-opacity:1'/>" +
                "</linearGradient>";
        String bgtoblack_def = "<linearGradient id='bgtoblack' x1='0%' y1='0%' x2='0%' y2='100%'>" +
                "<stop offset='0%' style='stop-color:#fffaea; stop-opacity:1'/>" +
                "<stop offset='100%' style='stop-color:black; stop-opacity:1'/>" +
                "</linearGradient>";

        String defs = "<defs xmlns='http://www.w3.org/2000/svg'>" + blacktobg_def + bgtoblack_def + "</defs>";

        //return "<svg " + version + "\n" + baseprofile + "\n" + xmlns + "\n" + xlinkns + "\n" + onload + "\n" + viewBox + ">";
        //return "<svg " + version + " " + baseprofile + " " + xmlns + " " + onload + " " + onzoom + " " + onscroll + " " + onresize + ">";
        //return "<svg " + svgid + " " + version + " " + baseprofile + " " + xmlns + " " + onload + " " + width + " " + height + ">";

        return "<svg " + svgid + " " + xmlns + " " + width + " " + height + " " + viewBox + ">" + defs;
    }

    public static String closeSVG() {
        return "</svg>";
    }

    public static String buildScriptIncludes() {
        StringBuilder b = new StringBuilder();
        b.append("<script xlink:href='ajax.js'/>");
        b.append("<script xlink:href='ensembl.js'/>");
        b.append("<script xlink:href='external.js'/>");
        b.append("<script xlink:href='genestruct.js'/>");
        b.append("<script xlink:href='interpro.js'/>");
        b.append("<script xlink:href='maputils.js'/>");
        b.append("<script xlink:href='mouseover.js'/>");
        b.append("<script xlink:href='overlay_maputils.js'/>");
        b.append("<script xlink:href='phosphabase.js'/>");
        b.append("<script xlink:href='publisher.js'/>");
        b.append("<script xlink:href='search.js'/>");
        b.append("<script xlink:href='utils.js'/>");
        return b.toString();
    }

    public static String drawElementsOnArc(String[] elementIDs,
                                           int parentX,
                                           int parentY,
                                           int parentRadius,
                                           int numElements,
                                           int arcAngle,
                                           int offsetAngle,
                                           boolean spokes) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < numElements; i++) {
            double x = parentX +
                    Math.round(Math.cos(2 * Math.PI / numElements * (i % numElements) + offsetAngle) * parentRadius);
            double y = parentY +
                    Math.round(Math.sin(2 * Math.PI / numElements * (i % numElements) + offsetAngle) * parentRadius);
            if (spokes) {
                b.append("<line x1='" + parentX + "' y1='" + parentY + "' x2='" + x + "' y2='" + y +
                                 "' style='stroke:black; stroke-width:0.1;'/>");
            }
            b.append("<circle id='" + elementIDs[i] + ":circmenu' cx='" + x + "' cy='" + y + "' r='" + 3 +
                             "' style='fill:green; fill-opacity:" + 0.5 + "' onclick='overlayGlyphMenu(evt, \"" +
                             elementIDs[i] +
                             "\");' cursor='pointer' onmouseover='highlight(evt)' onmouseout='unhighlight(evt)'/>");
            b.append("<text id='" + elementIDs[i] + ":circtext' x='" + (x + 5) + "' y='" + (y + 5) +
                             "' font-size='7' style='fill-opacity:" + 0.5 + "'>" + elementIDs[i] + "</text>");
        }
        return b.toString();
    }
}
