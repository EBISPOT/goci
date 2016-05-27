package uk.ac.ebi.spot.goci.sparql.pussycat.renderlet.chromosome;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;

import java.net.URI;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: dwelter Date: 01/03/12 Time: 10:48 To change this template use File | Settings | File
 * Templates.
 */

@ServiceProvider
public class SparqlChromosomeXRenderlet extends SparqlChromosomeRenderlet {
    //    private URI chromosomeURI = URI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI + "/Chromosome_X");
    private URI chromosomeURI = URI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI + "/Chromosome_X");
    private int position = 22;

    @Override public String getName() {
        return "Chromosome X";
    }

    @Override
    protected URL getSVGFile() {
        return getClass().getClassLoader().getResource("chromosomes/X.svg");
    }

    @Override
    protected URI getChromosomeURI() {
        return chromosomeURI;
    }

    @Override
    protected int getPosition() {
        return position;
    }
}
