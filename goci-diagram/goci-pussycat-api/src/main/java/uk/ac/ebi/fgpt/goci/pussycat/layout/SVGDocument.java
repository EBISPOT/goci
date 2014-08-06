package uk.ac.ebi.fgpt.goci.pussycat.layout;

/**
 * An abstraction over an SVG XML document.  Contains the content required to wrap individual SVG elements in a
 * syntactically correct SVG document, including headers and footers.
 *
 * @author Tony Burdett
 * @date 06/08/14
 */
public class SVGDocument {
    private final String header =
            "<?xml version='1.0' encoding='UTF-8'?> " +
                    "<svg xmlns='http://www.w3.org/2000/svg' " +
                    "contentScriptType='text/ecmascript' " +
                    "width='1200' " +
                    "zoomAndPan='magnify' " +
                    "contentStyleType='text/css' " +
                    "viewBox='0 0 1200 900' " +
                    "height='900' " +
                    "id='goci-svg' " +
                    "preserveAspectRatio='xMinYMin slice' " +
                    "version='1.0'>" +
                    "<defs>" +
                    "<linearGradient y2='100%' id='blacktobg' x1='0%' x2='0%' y1='0%'>" +
                    "<stop style='stop-color:black;stop-opacity:1' offset='0%' />" +
                    "<stop style='stop-color:#fffaea;stop-opacity:1' offset='100%' />" +
                    "</linearGradient>" +
                    "<linearGradient y2='100%' id='bgtoblack' x1='0%' x2='0%' y1='0%'>" +
                    "<stop style='stop-color:#fffaea;stop-opacity:1' offset='0%' />" +
                    "<stop style='stop-color:black;stop-opacity:1' offset='100%' />" +
                    "</linearGradient>" +
                    "<mask width='1200' maskUnits='userSpaceOnUse' x='0' height='800' y='-100' id='traitMask'>" +
                    "<rect fill='grey' x='0' width='1200' height='900' y='-100' opacity='.25' />" +
                    "</mask>" +
                    "</defs>";

    private final String globalTranslationStart = "<g transform='translate(x,y)'>";

    private final String globalTranslationEnd = "</g>";

    private final String footer = "</svg>";

    private boolean doTranslation;
    private int translationX;
    private int translationY;

    public SVGDocument() {
        this.doTranslation = false;
    }

    public SVGDocument(int translationX, int translationY) {
        this.doTranslation = true;
        this.translationX = translationX;
        this.translationY = translationY;
    }

    public String getHeader() {
        if (doTranslation) {
            return header.concat(getTranslation());
        }
        else {
            return header;
        }
    }

    public String getFooter() {
        if (doTranslation) {
            return globalTranslationEnd.concat(footer);
        }
        else {
            return footer;
        }
    }

    private String getTranslation() {
        return globalTranslationStart
                .replace("x", Integer.toString(translationX))
                .replace("y", Integer.toString(translationY));
    }
}
