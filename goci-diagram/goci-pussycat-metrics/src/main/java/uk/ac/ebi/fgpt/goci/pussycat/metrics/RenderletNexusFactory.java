package uk.ac.ebi.fgpt.goci.pussycat.metrics;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 11/09/12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class RenderletNexusFactory {
    public static RenderletNexus createBenchmarkRenderletNexus(OWLOntologyManager manager, OWLReasoner reasoner, Map<IRI, String> efoLabels) {
//        return new DefaultRenderletNexus();
        BenchmarkRenderletNexus nexus = new BenchmarkRenderletNexus();
        nexus.setOWLOntologyManager(manager);
        nexus.setReasoner(reasoner);
        nexus.setEfoLabels(efoLabels);
        return nexus;
    }
}
