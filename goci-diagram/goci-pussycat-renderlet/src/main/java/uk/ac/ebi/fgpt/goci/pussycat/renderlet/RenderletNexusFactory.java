package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Map;

/**
 * A factory that is capable of producing {@link RenderletNexus} instances.
 *
 * @author Tony Burdett Date 01/03/12
 */
public class RenderletNexusFactory {
    public static RenderletNexus createDefaultRenderletNexus(OWLOntologyManager manager, OWLReasoner reasoner, Map<IRI, String> efoLabels) {
//        return new DefaultRenderletNexus();
        DefaultRenderletNexus nexus = new DefaultRenderletNexus();
        nexus.setOWLOntologyManager(manager);
        nexus.setReasoner(reasoner);
        nexus.setEfoLabels(efoLabels);
        return nexus;
    }
}
