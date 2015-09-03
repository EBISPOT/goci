package uk.ac.ebi.spot.goci.sparql.pussycat.renderlet;

import com.hp.hpl.jena.query.QuerySolution;
import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;
import uk.ac.ebi.spot.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.spot.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.spot.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.spot.goci.pussycat.renderlet.AssociationRenderlet;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.QueryManager;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.QuerySolutionMapper;
import uk.ac.ebi.spot.goci.sparql.pussycat.query.SparqlTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 21/08/14
 */
@ServiceProvider
public class SparqlAssociationRenderlet extends AssociationRenderlet<SparqlTemplate, URI> {
    @Override public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        if (renderingContext instanceof SparqlTemplate && renderingEntity instanceof URI) {
            SparqlTemplate template = (SparqlTemplate) renderingContext;
            URI uri = (URI) renderingEntity;
            return template.ask(uri, URI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        }
        else {
            return false;
        }
    }

    @Override protected String getAssociationID(SparqlTemplate context, URI association) {
        return association.toString();
    }

    /**
     * Fetches the band information about the cytogenetic region the current association is located in
     *
     * @param sparqlTemplate the sparqlTemplate
     * @param association    the association to lookup band information for
     * @return the band information for this association
     * @throws DataIntegrityViolationException
     */
    protected BandInformation getBandInformation(SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        URI bandIndividual =
                QueryManager.getCachingInstance().getCytogeneticBandForAssociation(sparqlTemplate, association);
        if (bandIndividual != null) {
            return QueryManager.getCachingInstance().getBandInformation(sparqlTemplate, bandIndividual);
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    /**
     * For the given association, identifies the cytogenetic band it is located in, then identifies the total number of
     * traits located in the same cytogenetic band and returns the count
     *
     * @param sparqlTemplate the sparqlTemplate
     * @param association    the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected int getNumberOfTraitsInSameBand(RenderletNexus nexus, SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        URI bandIndividual =
                QueryManager.getCachingInstance().getCytogeneticBandForAssociation(sparqlTemplate, association);
        if (bandIndividual != null) {
//            Set<URI> associations =
//                    QueryManager.getCachingInstance()
//                            .getAssociationsLocatedInCytogeneticBand(sparqlTemplate, bandIndividual);
//            return associations.size();
            Set<URI> currentBandTraits =
                    QueryManager.getCachingInstance().getTraitsLocatedInCytogeneticBand(sparqlTemplate,
                                                                                        bandIndividual,
                                                                                        nexus.getRenderingContext());
            return currentBandTraits.size();
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    /**
     * For the given association, identifies the previous cytogenetic band to the one this association is located in,
     * then identifies the total number of traits located in that cytogenetic band and returns the count
     *
     * @param sparqlTemplate the sparqlTemplate
     * @param association    the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected int getNumberOfTraitsInPreviousBand(RenderletNexus nexus, SparqlTemplate sparqlTemplate, URI association)
            throws DataIntegrityViolationException {
        BandInformation band = getBandInformation(sparqlTemplate, association);
        if (band != null) {
            BandInformation previousBand = getPreviousBandMap(nexus, sparqlTemplate).get(band);

            // now find the traits in the previous band
//            Set<URI> previousBandAssociations =
//                    QueryManager.getCachingInstance().getAssociationsLocatedInCytogeneticBand(
//                            sparqlTemplate,
//                            previousBand.getBandName());
//            return previousBandAssociations.size();
            Set<URI> previousBandTraits =
                    QueryManager.getCachingInstance().getTraitsLocatedInCytogeneticBand(sparqlTemplate,
                                                                                        previousBand.getBandName(),
                                                                                        nexus.getRenderingContext());
            return previousBandTraits.size();
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    protected SVGArea getLocationOfPreviousAssociation(RenderletNexus nexus,
                                                       SparqlTemplate sparqlTemplate,
                                                       URI association)
            throws DataIntegrityViolationException {
        BandInformation band = getBandInformation(sparqlTemplate, association);
        if (band != null) {
            BandInformation previousBand = getPreviousBandMap(nexus, sparqlTemplate).get(band);
            if (previousBand == null) {
                return null;
            }

            // now find the traits in the previous band
            Set<URI> previousBandAssociations =
                    QueryManager.getCachingInstance().getAssociationsLocatedInCytogeneticBand(
                            sparqlTemplate,
                            previousBand.getBandName(),
                            nexus.getRenderingContext());

            // get first not-null location for an association in the previous band
            for (URI previousBandAssociation : previousBandAssociations) {
                SVGArea prevLocation = nexus.getLocationOfRenderedEntity(previousBandAssociation);
                if (prevLocation != null) {
                    return prevLocation;
                }
            }
            // if we get to here, no associations are located in the previous region so return null
            getLog().trace(
                    "Unable to identify any associations in the previous cytogenetic region '" +
                            previousBand.getBandName() + "'");
            return null;
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    protected Map<BandInformation, BandInformation> sortBandsWithData(SparqlTemplate sparqlTemplate) {
        Map<BandInformation, BandInformation> bandMap = new HashMap<BandInformation, BandInformation>();

        // use the sparqlTemplate to get all individuals of type "cytogenic region"
        getLog().trace("Retrieving all cytogenetic bands to sort into rendering order...");
        List<BandInformation> bands = sparqlTemplate.query(
                "SELECT DISTINCT ?band WHERE { ?bandUri a gt:CytogeneticRegion ; rdfs:label ?band . FILTER (STR(?band) != 'NR') .}",
                new QuerySolutionMapper<BandInformation>() {
                    @Override public BandInformation mapQuerySolution(QuerySolution qs) {
                        return new BandInformation(qs.getLiteral("band").getLexicalForm());
                    }
                });
        getLog().trace("Got " + bands.size() + " bands, starting sorting...");

        // now we've added all band info, sort the set of unique bands
        Collections.sort(bands);

        for (int i = 1; i < bands.size(); i++) {
            BandInformation band = bands.get(i);
            BandInformation previousBand = bands.get(i - 1);
            bandMap.put(band, previousBand);
        }

        getLog().trace("Mapped " + bandMap.keySet().size() + " bands to their 'previous' band");
        return bandMap;
    }
}
