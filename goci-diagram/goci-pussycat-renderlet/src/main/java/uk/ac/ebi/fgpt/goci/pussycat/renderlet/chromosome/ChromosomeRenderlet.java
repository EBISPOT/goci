package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderingEvent;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 29/02/12
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChromosomeRenderlet implements Renderlet<OWLOntology, OWLClass> {
    
    private Map<String, SVGArea> chromBands;

    public String getDisplayName(){
        return getName();
    }

    public String getDescription() {
        return ("This is a renderlet displaying " + getDisplayName());
    }

    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object owlEntity) {

        /*
        * probably won't need nexus for rendering chromosomes, except possibly to check that chromosomes are rendered first
        *
        * extract OWL class type from owlEntity, then check if it is type chromosome
        * hardcode chromosome type into each individual chromosome renderlet into some new method liked "checkChromType",
        * then check return of that method against the chromosome in the owlEntity to make sure the right chromosome in rendered
        *
        * */
        boolean renderable = false;

        if (renderingContext instanceof OWLOntology){

            IRI chromIRI =  getChromIRI();

            if (owlEntity instanceof OWLClass){
                OWLClass thisClass = (OWLClass)owlEntity;

                if(thisClass.getIRI().equals(chromIRI)){
                      renderable = true;
                }

            }
        }

         return renderable;
    }

    public void render(RenderletNexus nexus, OWLOntology renderingContext, OWLClass owlEntity) {

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        InputStream svgstream = null;
        Document chromSVG = null;
        Element g=null;

        int position = getPosition();
        int height = SVGCanvas.canvasHeight;
        int width = SVGCanvas.canvasWidth;

        double chromWidth = (double)width/12;
        double chromHeight = (double)height/2;
        double xCoordinate;
        double yCoordinate = 0;

        if (position < 12){
            xCoordinate = position * chromWidth;
        }
        else{
            xCoordinate = (position-12) * chromWidth;
            yCoordinate = (double)height/2;
        }

        try {
            svgstream = getSVGFile().openStream();
            chromSVG = f.createDocument(getSVGFile().toString(), getSVGFile().openStream());

            if (chromSVG != null) {
                Element root = chromSVG.getDocumentElement();
                g = (Element)root.getElementsByTagName("g").item(0).cloneNode(true);

                setChromBands(g);

                StringBuilder builder = new StringBuilder();
                builder.append("translate(");
                builder.append(Double.toString(xCoordinate));
                builder.append(",");
                builder.append(Double.toString(yCoordinate));
                builder.append(")");

                g.setAttribute("transform", builder.toString());

                String mo = "showTooltip('" + getName() + "')";
                g.setAttribute("onmouseover",mo);
                g.setAttribute("onmouseout", "hideTooltip()");
                g.removeAttribute("title");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        finally {
            try {
                if (svgstream != null){
                    svgstream.close();

                    for(String band : chromBands.keySet()){
                        nexus.setRenderableBand(band);
                    }

                    SVGArea currentArea = new SVGArea(xCoordinate,yCoordinate,chromWidth,chromHeight,0); 

                    // todo - work out how to do this! --> consider adding new method to each chromRenderlet along the lines of getChromToLeft and hardcode
                    // id of previous chrom into it, then query by chromID
                    //nexus.getLocationOfRenderedEntity(chromosomeToTheLeft);
                    nexus.addSVGElement(g);
                    RenderingEvent event = new RenderingEvent(owlEntity, g, currentArea, this);
                    nexus.renderingEventOccurred(event);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<String, SVGArea> getBands(){
        return chromBands;
    }

    protected void setChromBands(Element g){
        chromBands = new HashMap<String, SVGArea>();

        NodeList paths = g.getElementsByTagName("path");

        for(int i = 0; i < paths.getLength(); i++){
            Element path = (Element)paths.item(i);
            String id = path.getAttribute("id");
            String d = path.getAttribute("d");

            if(id.contains("centre") || id.contains("outline") || id.contains("satellite") || id.contains("-o") || id.contains("centromere")){
                continue;
            }
            else {
                StringTokenizer tokenizer = new StringTokenizer(d);
                ArrayList<String> pathElements = new ArrayList<String>();

                while(tokenizer.hasMoreTokens()){
                    pathElements.add(tokenizer.nextToken());
                }
  //bottom left corner of the chromosomal band
                String[] xy = pathElements.get(1).split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                double width;
                double height=0;

//special case for top- and bottom-most bands
                if(pathElements.contains("c")){
//top band: starts bottom left -> br -> arc
                    if(id.contains("p")){
                        double original = y;
                        String[] w = pathElements.get(2).split(",");
                        width = Math.abs(Double.parseDouble((w[0])));

                        for(int k = 3; k < pathElements.size()-1; k++){
                            String element = pathElements.get(k);

                            if(element.contains("c")){
                                while(!pathElements.get(k+1).contains("l") && !pathElements.get(k+1).contains("z")){
                                    k = k+3;
                                    String point = pathElements.get(k);
                                    String[] coords = point.split(",");
                                    double temp = Double.parseDouble(coords[1]);
                                    if(temp < 0){
                                        y = y+temp;
                                    }
                                }
                            }
                            else if(element.contains("l")){
                                k = k+1;
                                String point = pathElements.get(k);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if(temp < 0){
                                    y = y+temp;
                                }
                            }
                            else{
                                String point = pathElements.get(k);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if(temp < 0){
                                    y = y+temp;
                                }
                            }
                            height = original - y;
                        }
                    }
//bottom band: starts top left -> arc -> tr
                    else{
                        int last = pathElements.size()-2;
                        String[] w = pathElements.get(last).split(",");
                        width = Math.abs(Double.parseDouble(w[0]));
                        height = 0;

                        for(int j = 3; j < pathElements.size()-1; j++){
                            String element = pathElements.get(j);

                            if(element.contains("c")){
                                while(!pathElements.get(j+1).contains("l")){
                                    j = j+3;
                                    String point = pathElements.get(j);
                                    String[] coords = point.split(",");
                                    double temp = Double.parseDouble(coords[1]);
                                    if(temp > 0){
                                        height = height+temp;
                                    }
                                }
                            }
                            else if(element.contains("l")){
                                j= j+1;
                                String point = pathElements.get(j);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if(temp > 0){
                                    height = height+temp;
                                }
                            }
                            else{
                                String point = pathElements.get(j);
                                String[] coords = point.split(",");
                                double temp = Double.parseDouble(coords[1]);
                                if(temp > 0){
                                    height = height+temp;
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

/*SVG area for the chromosomal bands gives the x&y coordinates for its top left corner, and its width and height*/
                SVGArea band = new SVGArea(x,y,width,height,0);

                chromBands.put(id, band);
            }
        }
    }

    protected abstract URL getSVGFile();

    protected abstract IRI getChromIRI();
    
    protected abstract int getPosition();
}

