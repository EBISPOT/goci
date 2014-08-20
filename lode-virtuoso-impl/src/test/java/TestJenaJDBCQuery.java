import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.impl.JenaExploreService;
import uk.ac.ebi.fgpt.lode.impl.JenaVirtuosoExecutorService;


import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Simon Jupp
 * @date 08/08/2013
 * Functional Genomics Group EMBL-EBI
 */
public class TestJenaJDBCQuery {



    String query ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
            "PREFIX obo: <http://purl.obolibrary.org/obo/>\n" +
            "PREFIX efo: <http://www.ebi.ac.uk/efo/>\n" +
            "PREFIX aeexperiment: <http://rdf.ebi.ac.uk/resource/zooma/arrayexpress/experiment/>\n" +
            "PREFIX aeannotation: <http://rdf.ebi.ac.uk/resource/zooma/arrayexpress/annotation/>\n" +
            "PREFIX aeproperty: <http://rdf.ebi.ac.uk/resource/zooma/arrayexpress//property/>\n" +
            "PREFIX gxaexperiment: <http://rdf.ebi.ac.uk/resource/zooma/gxa/experiment/>\n" +
            "PREFIX gxaannotation: <http://rdf.ebi.ac.uk/resource/zooma/gxa/annotation/>\n" +
            "PREFIX gxaproperty: <http://rdf.ebi.ac.uk/resource/zooma/gxa/property/>\n" +
            "PREFIX gwassnp: <http://rdf.ebi.ac.uk/resource/zooma/gwas/snp/>\n" +
            "PREFIX gwasannotation: <http://rdf.ebi.ac.uk/resource/zooma/gwas/annotation/>\n" +
            "PREFIX gwasproperty: <http://rdf.ebi.ac.uk/resource/zooma/gwas/property/>\n" +
            "PREFIX zoomaresource: <http://rdf.ebi.ac.uk/resource/zooma/>\n" +
            "PREFIX zoomaterms: <http://rdf.ebi.ac.uk/terms/zooma/>\n" +
            "PREFIX oac: <http://www.openannotation.org/ns/>\n" +
            "PREFIX ebi: <http://www.ebi.ac.uk/>\n" +
            "SELECT DISTINCT (?annotationid as ?_annotationid) (?bioentityid as ?_bioentityid) (?study as ?_study) ?databaseid ?evidence (?propertyvalueid as ?_propertyvalueid) (?propertyname  as ?_propertyname) (?propertyvalue as ?_propertyvalue) (?semantictag as ?_semantictag) ?annotator ?annotated ?generator ?generated WHERE {\n" +
            "?annotationid rdf:type oac:DataAnnotation .\n" +
            "?annotationid oac:hasBody ?propertyvalueid .\n" +
            "?propertyvalueid zoomaterms:propertyName ?propertyname .\n" +
            "?propertyvalueid zoomaterms:propertyValue ?propertyvalue .\n" +
            "?propertyvalueid rdf:type zoomaterms:PropertyValue .\n" +
            "\n" +
            "?annotationid oac:hasTarget ?bioentityid .\n" +
            "?bioentityid dc:isPartOf ?study .\n" +
            "OPTIONAL {\n" +
            "    ?annotationid oac:hasBody ?semantictag .\n" +
            "    ?semantictag rdf:type oac:SemanticTag\n" +
            "} .\n" +
            "OPTIONAL {?annotationid dc:source ?databaseid} .\n" +
            "OPTIONAL {?annotationid zoomaterms:hasEvidence ?evidence} .\n" +
            "OPTIONAL {?annotationid oac:annotator ?annotator} .\n" +
            "OPTIONAL {?annotationid oac:annotated ?annotated} .\n" +
            "OPTIONAL {?annotationid oac:generator ?generator} .\n" +
            "OPTIONAL {?annotationid oac:generated ?generated} .\n" +
            "}\n";

    @Ignore
    public void testQuery() {

        ApplicationContext context = new ClassPathXmlApplicationContext("test-explorer-config.xml");
        JenaVirtuosoExecutorService sv = (JenaVirtuosoExecutorService) context.getBean("jenaVirtuosoExecutorService");

        sv.setEndpointURL("jdbc:virtuoso://orange:1114");
        QuerySolutionMap sol = new QuerySolutionMap();
        sol.add("study", new ResourceImpl("http://europepmc.org/abstract/MED/2194357") );
        try {
            QueryExecution ex = sv.getQueryExecution(sv.getDefaultGraph(), query, sol, false);
            ResultSet res = ex.execSelect();
            while (res.hasNext()) {
                QuerySolution sols = res.next();
                Iterator<String> ts  = sols.varNames();
                while (ts.hasNext()) {
                    String vad = ts.next();
                    System.out.println(vad + " = " + sols.get(vad));

                }

            }


        } catch (LodeException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestJenaJDBCQuery jdbc = new TestJenaJDBCQuery();
        jdbc.testQuery();
    }


}
