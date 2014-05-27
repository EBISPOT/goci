package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.service.QueryManager;

import java.util.Map;

/**
 * A factory that is capable of producing {@link RenderletNexus} instances.
 *
 * @author Tony Burdett Date 01/03/12
 */
public class RenderletNexusFactory {
    public static RenderletNexus createOWLRenderletNexus(OWLOntologyManager manager, OWLReasoner reasoner, Map<IRI, String> efoLabels) {
//        return new OWLRenderletNexus();
        OWLRenderletNexus nexus = new OWLRenderletNexus();
        nexus.setOWLOntologyManager(manager);
        nexus.setReasoner(reasoner);
        nexus.setEfoLabels(efoLabels);
        return nexus;
    }

    public static RenderletNexus createSPARQLRenderletNexus(QueryManager manager){
        SPARQLRenderletNexus nexus = new SPARQLRenderletNexus();
        nexus.setQueryManager(manager);

        return nexus;
    }
}
