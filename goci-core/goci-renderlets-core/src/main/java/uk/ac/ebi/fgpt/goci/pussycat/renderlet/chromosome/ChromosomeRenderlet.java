package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderingEvent;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Abstract renderlet capable of rendering OWL classes representing Chromosomes into SVG
 *
 * @author Dani Welter
 * @date 29/02/12
 */
public abstract class ChromosomeRenderlet<C, E> implements Renderlet<C, E> {
    public String getDisplayName() {
        return getName();
    }

    public String getDescription() {
        return ("This is a renderlet displaying " + getDisplayName());
    }

    public void render(RenderletNexus nexus, C context, E entity) {
        int position = getPosition();
        int height = SVGCanvas.canvasHeight;
        int width = SVGCanvas.canvasWidth;

        double chromWidth = (double) width / 12;
        double chromHeight = (double) height / 2;
        double xCoordinate;
        double yCoordinate = 0;

        if (position < 12) {
            xCoordinate = position * chromWidth;
        }
        else {
            xCoordinate = (position - 12) * chromWidth;
            yCoordinate = (double) height / 2;
        }

        InputStream in = null;
        try {
            in = getSVGFile().openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document chromSVG = db.parse(in);
            Element root = chromSVG.getDocumentElement();
            Element g = (Element) root.getElementsByTagName("g").item(0).cloneNode(true);

            setChromBands(g, nexus);

            StringBuilder builder = new StringBuilder();
            builder.append("translate(");
            builder.append(Double.toString(xCoordinate));
            builder.append(",");
            builder.append(Double.toString(yCoordinate));
            builder.append(")");

            g.setAttribute("transform", builder.toString());
            g.setAttribute("gwasname", getName());
            g.removeAttribute("title");

            SVGArea currentArea = new SVGArea(xCoordinate, yCoordinate, chromWidth, chromHeight, 0);

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(g), new StreamResult(buffer));
            String str = buffer.toString();

            RenderingEvent<E> event =
                    new RenderingEvent<E>(entity, str, currentArea, this);
            nexus.renderingEventOccurred(event);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to read in template chromosome SVG from original resource", e);
        }
        catch (SAXException e) {
            throw new RuntimeException("Failed to read in template chromosome SVG from original resource", e);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to render chromosome SVG", e);
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException("Failed to render chromosome SVG", e);
        }
        catch (TransformerException e) {
            throw new RuntimeException("Failed to render chromosome SVG", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    // tried our best!
                }
            }
        }
    }

    protected void setChromBands(Element g, RenderletNexus nexus) {
        NodeList paths = g.getElementsByTagName("path");
        for (int i = 0; i < paths.getLength(); i++) {
            Element path = (Element) paths.item(i);
            String id = path.getAttribute("id");
            String d = path.getAttribute("d");

            if (!id.contains("centre") && !id.contains("outline") && !id.contains("satellite") && !id.contains("-o") &&
                    !id.contains("centromere")) {
                StringTokenizer tokenizer = new StringTokenizer(d);
                ArrayList<String> pathElements = new ArrayList<String>();

                while (tokenizer.hasMoreTokens()) {
                    pathElements.add(tokenizer.nextToken());
                }
                //bottom left corner of the chromosomal band
                String[] xy = pathElements.get(1).split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                double width;
                double height = 0;

                //special case for top- and bottom-most bands
                if (pathElements.contains("c")) {
                    //top band: starts bottom left -> br -> arc
                    if (id.contains("p")) {
                        double original = y;
                        String[] w = pathElements.get(2).split(",");
                        width = Math.abs(Double.parseDouble((w[0])));

                        for (int k = 3; k < pathElements.size() - 1; k++) {
                            String element = pathElements.get(k);

                            if (element.contains("c")) {
                                while (!pathElements.get(k + 1).contains("l") &&
                                        !pathElements.get(k + 1).contains("z")) {
                                    k = k + 3;
                                    String point = pathElements.get(k);
                                    String[] coords = point.split(",");
                                    double temp = Double.parseDouble(coords[1]);
                                    if (temp < 0) {
                                        y = y + temp;
                                    }
                                }
                            }
                            else if (element.contains("l")) {
                                k = k + 1;
                                String point = pathElements.get(k);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if (temp < 0) {
                                    y = y + temp;
                                }
                            }
                            else {
                                String point = pathElements.get(k);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if (temp < 0) {
                                    y = y + temp;
                                }
                            }
                            height = original - y;
                        }
                    }
                    //bottom band: starts top left -> arc -> tr
                    else {
                        int last = pathElements.size() - 2;
                        String[] w = pathElements.get(last).split(",");
                        width = Math.abs(Double.parseDouble(w[0]));
                        height = 0;

                        for (int j = 3; j < pathElements.size() - 1; j++) {
                            String element = pathElements.get(j);

                            if (element.contains("c")) {
                                while (!pathElements.get(j + 1).contains("l")) {
                                    j = j + 3;
                                    String point = pathElements.get(j);
                                    String[] coords = point.split(",");
                                    double temp = Double.parseDouble(coords[1]);
                                    if (temp > 0) {
                                        height = height + temp;
                                    }
                                }
                            }
                            else if (element.contains("l")) {
                                j = j + 1;
                                String point = pathElements.get(j);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if (temp > 0) {
                                    height = height + temp;
                                }
                            }
                            else {
                                String point = pathElements.get(j);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if (temp > 0) {
                                    height = height + temp;
                                }
                            }
                        }

                    }
                }

                //all the other bands
                else {
                    //width of the band
                    String[] w = pathElements.get(2).split(",");
                    width = Math.abs(Double.parseDouble((w[0])));

                    //height of the band
                    String[] h = pathElements.get(3).split(",");
                    height = Math.abs(Double.parseDouble((h[1])));
                    y = y - height;
                }

                // SVG area for the chromosomal bands gives the x&y coordinates for its top left corner, and its width and height
                SVGArea band = new SVGArea(x, y, width, height, 0);
                String chromName = getName().split(" ")[1];

                BandInformation info = new BandInformation(id, chromName);
                nexus.renderingEventOccurred(new RenderingEvent<BandInformation>(info, "", band, this));
            }
        }
    }

    protected abstract URL getSVGFile();

    protected abstract URI getChromosomeURI();

    protected abstract int getPosition();
}

