package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.LayoutUtils;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;

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
public class AssociationRenderlet implements Renderlet<OWLReasoner, OWLNamedIndividual> {
    private final Map<OWLReasoner, Map<BandInformation, BandInformation>> previousBandMapByReasoner;
    private final Map<BandInformation, SVGArea> renderedBands;

    private Logger log = LoggerFactory.getLogger(getClass());

    public AssociationRenderlet() {
        this.previousBandMapByReasoner = new HashMap<OWLReasoner, Map<BandInformation, BandInformation>>();
        this.renderedBands = new HashMap<BandInformation, SVGArea>();
    }

    protected Logger getLog() {
        return log;
    }

    @Override
    public String getName() {
        return "Association renderlet";
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDescription() {
        return ("Renderlet capable of creating GWAS trait-SNP association visualisations");
    }

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

    @Override
    public void render(RenderletNexus nexus, OWLReasoner reasoner, OWLNamedIndividual association) {
        getLog().trace("Association: " + association);
        if (!previousBandMapByReasoner.containsKey(reasoner)) {
            sortBandsWithData(reasoner);
        }

        try {
            BandInformation band = getBandInformation(reasoner, association);
            if (!renderedBands.containsKey(band)) {
                // there is no other association in this chromosomal band yet - render
                getLog().trace("First association for this band");

                StringBuilder svg = new StringBuilder();
                String transform = getTransformation(band.getChromosome());
                svg.append("<g ")
                        .append("id='").append(association.getIRI().toString()).append("' ")
                        .append("transform='").append(transform).append("' ")
                        .append("class='gwas-trait'>");

                SVGArea location = nexus.getLocationOfRenderedEntity(band);
                if (location != null) {
                    double x = location.getX();
                    double y = location.getY();
                    double width = location.getWidth();
                    double height = location.getHeight();
                    double newY = y + (height / 2);
                    double endY = newY;
                    double length = 1.75 * width;
                    double newHeight = 0;

                    // fanning algorithm
                    SVGArea previousLocation = null;
                    Map<BandInformation, BandInformation> previousBandMap = previousBandMapByReasoner.get(reasoner);
                    if (previousBandMap != null) {
                        BandInformation previousBand = previousBandMap.get(band);
                        previousLocation = nexus.getLocationOfRenderedEntity(previousBand);
                    }
                    if (previousLocation != null) {
                        double prevY = previousLocation.getY();
                        double prevHeight = previousLocation.getHeight(); // todo - I think?
                        double radius = 0.35 * width;

                        if (band.getBandName().contains("p")) {
                            int traitCount = getNumberOfTraitsInSameBand(reasoner, association);
                            int drop = ((traitCount - 1) / 6) + 2;
                            double min = prevY - (drop * radius);
                            if (min <= newY) {
                                endY = min;
                                newHeight = endY - newY;
                            }
                        }
                        else {
//                        int drop = ((previous.getTraitNames().size() - 1) / 6) + 2;
//                        double min = prevY + (drop * radius);
                            double min = prevY + prevHeight;
                            if (min >= newY) {
                                endY = min;
                                newHeight = endY - newY;
                            }
                        }
                    }

                    StringBuilder d = new StringBuilder();
                    if (previousLocation == null || newHeight == 0) {
                        d.append("m ");
                        d.append(Double.toString(x));
                        d.append(",");
                        d.append(Double.toString(newY));
                        d.append(" ");
                        d.append(Double.toString(length));
                        d.append(",0.0");
                    }

                    else {
                        double width2 = 0.75 * width;
                        d.append("m ");
                        d.append(Double.toString(x));
                        d.append(",");
                        d.append(Double.toString(newY));
                        d.append(" ");
                        d.append(Double.toString(width));
                        d.append(",0.0, ");
                        d.append(Double.toString(width2));
                        d.append(",");
                        d.append(Double.toString(newHeight));
                    }

                    svg.append("<path ")
                            .append("d='").append(d.toString()).append("' ")
                            .append("style='fill:none;stroke:#211c1d;stroke-width:1.1;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none'")
                            .append(" />");
                    svg.append("</g>");

                    SVGArea currentArea = new SVGArea(x, newY, length, newHeight, transform, 0);
                    RenderingEvent<OWLNamedIndividual> event =
                            new RenderingEvent<OWLNamedIndividual>(association, svg.toString(), currentArea, this);
                    nexus.renderingEventOccurred(event);

                    // add band to renderedBands set
                    renderedBands.put(band, currentArea);
                }
                else {
                    getLog().error("Unable to render association '" + association + "' - " +
                                           "no location for band '" + band.getBandName() + "'");
                }
            }
            else {
                // we've already rendered the required association line, so we don't need to do it again
                // but we do need to log the rendering event for this association individual
                getLog().trace("Already rendered an association line to band '" + band.getBandName() + "', " +
                                       "logging secondary event for association '" + association + "'");
                SVGArea area = renderedBands.get(band);
                nexus.renderingEventOccurred(new RenderingEvent<OWLNamedIndividual>(association, "", area, this));
            }
        }
        catch (DataIntegrityViolationException e) {
            getLog().error("Cannot render association '" + association + "'", e);
        }
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
            Set<OWLNamedIndividual> traits =
                    LayoutUtils.getCachingInstance().getTraitsLocatedInCytogeneticBand(reasoner, bandIndividual);
            return traits.size();
        }
        else {
            throw new DataIntegrityViolationException(
                    "Unable to identify the cytogenetic region where association '" + association + "' is located");
        }
    }

    private String getTransformation(String chromosomeName) {
        int position;
        if (chromosomeName.equals("X")) {
            position = 22;
        }
        else if (chromosomeName.equals("Y")) {
            position = 23;
        }
        else {
            position = Integer.parseInt(chromosomeName) - 1;
        }
        int height = SVGCanvas.canvasHeight;
        int width = SVGCanvas.canvasWidth;

        double chromWidth = (double) width / 12;
        double xCoordinate;
        double yCoordinate = 0;

        if (position < 12) {
            xCoordinate = position * chromWidth;
        }
        else {
            xCoordinate = (position - 12) * chromWidth;
            yCoordinate = (double) height / 2;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("translate(");
        builder.append(Double.toString(xCoordinate));
        builder.append(",");
        builder.append(Double.toString(yCoordinate));
        builder.append(")");

        return builder.toString();
    }

    private void sortBandsWithData(OWLReasoner reasoner) {
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
        previousBandMapByReasoner.put(reasoner, bandMap);
    }
}