package uk.ac.ebi.fgpt.goci.pussycat.renderlet.chromosome;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderingEvent;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: dwelter
 * Date: 29/02/12
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public abstract class ChromosomeRenderlet implements Renderlet<OWLOntology, OWLClass> {

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

            IRI chromIRI =  getIRI();

            if (owlEntity instanceof OWLClass){
                OWLClass thisClass = (OWLClass)owlEntity;

                if(thisClass.getIRI().equals(chromIRI)){
                      renderable = true;
                }

            }
        }

         return renderable;
    }

    public String render(RenderletNexus nexus, OWLOntology renderingContext, OWLClass owlEntity) {
        String fileContent = null;
        BufferedInputStream svgstream = null;
        byte[] buffer = new byte[1024];
        StringBuilder builder = new StringBuilder();

        int position = getPosition();
        int height = nexus.getCanvasHeight();
        int width = nexus.getCanvasWidth();

        int chromWidth = width/12;

        int xCoordinate = 0;
        int yCoordinate = 0;

        if (position < 12){
            xCoordinate = position * chromWidth;
        }
        else{
            xCoordinate = (position-12) * chromWidth;
            yCoordinate = height/2;
        }

        builder.append("<g transform=\"translate(");
        builder.append(Integer.toString(xCoordinate));
        builder.append(",");
        builder.append(Integer.toString(yCoordinate));
        builder.append(")\"> \n");

        try {
            svgstream = new BufferedInputStream(getSVGFile().openStream());

            int bytesRead = 0;

            while ((bytesRead = svgstream.read(buffer, 0, buffer.length)) != -1) {
                builder.append(new String(buffer, 0, bytesRead));
            }

            builder.append("</g>");

            fileContent = builder.toString();


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        finally {
            try {
                if (svgstream != null){
                    svgstream.close();

                    SVGArea currentArea = new SVGArea(0,0,0,0,0); //TODO put something useful into the SVG area constructor

                    // todo - work out how to do this! --> consider adding new method to each chromRenderlet along the lines of getChromToLeft and hardcode
                    // id of previous chrom into it, then query by chromID
                    //nexus.getLocationOfRenderedEntity(chromosomeToTheLeft);

                    RenderingEvent event = new RenderingEvent(owlEntity, fileContent, currentArea, this);
                    nexus.renderingEventOccurred(event);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return fileContent;
    }

    protected abstract URL getSVGFile();

    protected abstract IRI getIRI();
    
    protected abstract int getPosition();


}
