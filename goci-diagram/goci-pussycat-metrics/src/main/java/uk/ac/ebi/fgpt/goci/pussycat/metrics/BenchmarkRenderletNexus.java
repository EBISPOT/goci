package uk.ac.ebi.fgpt.goci.pussycat.metrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderingEvent;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 11/09/12
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkRenderletNexus implements RenderletNexus {
    @Override
    public boolean register(Renderlet renderlet) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOWLOntologyManager(OWLOntologyManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OWLOntologyManager getManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setReasoner(OWLReasoner reasoner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public OWLReasoner getReasoner() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEfoLabels(Map<IRI, String> efoLabels) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<IRI, String> getEfoLabels() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <O> void renderingEventOccurred(RenderingEvent<O> evt) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <O> SVGArea getLocationOfEntity(O renderedEntity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSVG(OWLClassExpression classExpression) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<Renderlet> getRenderlets() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <O> RenderingEvent getRenderingEvent(O renderedEntity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBandLocation(String band, BandInformation information) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, BandInformation> getBandLocations() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
