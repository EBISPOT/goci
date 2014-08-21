package uk.ac.ebi.fgpt.goci.owl.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.owl.pussycat.layout.LayoutUtils;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.AssociationRenderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renderlet that can generate SVG for OWLIndividuals representing GWAS trait associations
 *
 * @author dwelter
 * @date 18/04/12
 */
@ServiceProvider
public class OWLAssociationRenderlet extends AssociationRenderlet<OWLReasoner, OWLNamedIndividual> {
    @Override
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object renderingEntity) {
        if (renderingContext instanceof OWLReasoner) {
            if (renderingEntity instanceof OWLNamedIndividual) {
                OWLOntology ontology = ((OWLReasoner) renderingContext).getRootOntology();
                OWLNamedIndividual individual = (OWLNamedIndividual) renderingEntity;
                if (nexus.getLocationOfRenderedEntity(individual) == null) {
                    for (OWLClassExpression type : individual.getTypes(ontology)) {
                        OWLClass typeClass = type.asOWLClass();
                        if (typeClass.getIRI().equals(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override protected String getAssociationID(OWLReasoner context, OWLNamedIndividual association) {
        return association.getIRI().toString();
    }

    /**
     * Fetches the band information about the cytogenetic region the current association is located in
     *
     * @param reasoner    the reasoner
     * @param association the association to lookup band information for
     * @return the band information for this association
     * @throws DataIntegrityViolationException
     */
    protected BandInformation getBandInformation(OWLReasoner reasoner, OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        OWLNamedIndividual bandIndividual =
                LayoutUtils.getCachingInstance().getCytogeneticBandForAssociation(reasoner, association);
        if (bandIndividual != null) {
            return LayoutUtils.getCachingInstance().getBandInformation(reasoner, bandIndividual);
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
     * @param reasoner    the reasoner
     * @param association the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected int getNumberOfTraitsInSameBand(OWLReasoner reasoner, OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        OWLNamedIndividual bandIndividual =
                LayoutUtils.getCachingInstance().getCytogeneticBandForAssociation(reasoner, association);
        if (bandIndividual != null) {
            Set<OWLNamedIndividual> associations =
                    LayoutUtils.getCachingInstance().getAssociationsLocatedInCytogeneticBand(reasoner, bandIndividual);
            return associations.size();
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
     * @param reasoner    the reasoner
     * @param association the association to identify co-located traits for
     * @return the number of traits in the same cytogenetic region as this association
     * @throws DataIntegrityViolationException
     */
    protected int getNumberOfTraitsInPreviousBand(OWLReasoner reasoner, OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        BandInformation band = getBandInformation(reasoner, association);
        if (band != null) {
            BandInformation previousBand = getPreviousBandMap(reasoner).get(band);

            // now find the traits in the previous band
            Set<OWLNamedIndividual> previousBandAssociations =
                    LayoutUtils.getCachingInstance().getAssociationsLocatedInCytogeneticBand(
                            reasoner,
                            previousBand.getBandName());
            return previousBandAssociations.size();
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    protected SVGArea getLocationOfPreviousAssociation(RenderletNexus nexus,
                                                       OWLReasoner reasoner,
                                                       OWLNamedIndividual association)
            throws DataIntegrityViolationException {
        BandInformation band = getBandInformation(reasoner, association);
        if (band != null) {
            BandInformation previousBand = getPreviousBandMap(reasoner).get(band);
            if (previousBand == null) {
                return null;
            }

            // now find the traits in the previous band
            Set<OWLNamedIndividual> previousBandAssociations =
                    LayoutUtils.getCachingInstance().getAssociationsLocatedInCytogeneticBand(
                            reasoner,
                            previousBand.getBandName());

            // get first not-null location for an association in the previous band
            for (OWLNamedIndividual previousBandAssociation : previousBandAssociations) {
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

    protected Map<BandInformation, BandInformation> sortBandsWithData(OWLReasoner reasoner) {
        OWLOntology ontology = reasoner.getRootOntology();
        OWLOntologyManager manager = reasoner.getRootOntology().getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        Set<BandInformation> bandSet = new HashSet<BandInformation>();
        Map<BandInformation, BandInformation> bandMap = new HashMap<BandInformation, BandInformation>();

        // use the reasoner to get all individuals of type "cytogenic region"
        OWLClass bandCls = factory.getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));
        getLog().trace("Retrieving all cytogenetic bands to sort into rendering order...");
        Set<OWLNamedIndividual> bands = reasoner.getInstances(bandCls, false).getFlattened();
        getLog().trace("Got " + bands.size() + " bands, starting sorting...");

        for (OWLNamedIndividual band : bands) {
            // get the band name
            OWLDataProperty has_name = factory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

            if (band.getDataPropertyValues(has_name, ontology).size() != 0) {
                Set<OWLLiteral> bandNames = reasoner.getDataPropertyValues(band, has_name);
                if (bandNames.size() == 0) {
                    getLog().warn("No band name data property value for band individual '" + band + "'");
                }
                else {
                    if (bandNames.size() > 1) {
                        getLog().warn("There are " + bandNames.size() + " band name data property values " +
                                              "for band individual '" + band + "', only using the first observed name");
                    }
                    String bandName = bandNames.iterator().next().getLiteral();

                    BandInformation bandInfo = new BandInformation(bandName);
                    bandSet.add(bandInfo);
                }
            }
        }

        // now we've added all band info, sort the set of unique bands
        List<BandInformation> bandList = new ArrayList<BandInformation>();
        bandList.addAll(bandSet);
        Collections.sort(bandList);

        for (int i = 1; i < bandList.size(); i++) {
            BandInformation band = bandList.get(i);
            BandInformation previousBand = bandList.get(i - 1);
            bandMap.put(band, previousBand);
        }

        getLog().trace("Mapped " + bandMap.keySet().size() + " bands to their 'previous' band");
        return bandMap;
    }
}