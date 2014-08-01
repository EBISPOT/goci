package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGArea;
import uk.ac.ebi.fgpt.goci.pussycat.layout.SVGCanvas;

import java.util.HashMap;
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

    private Logger log = LoggerFactory.getLogger(getClass());

    public AssociationRenderlet() {
        this.previousBandMapByReasoner = new HashMap<OWLReasoner, Map<BandInformation, BandInformation>>();
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
    public boolean canRender(RenderletNexus nexus, Object renderingContext, Object owlEntity) {
        if (renderingContext instanceof OWLOntology) {
            if (owlEntity instanceof OWLNamedIndividual) {
                OWLOntology ontology = (OWLOntology) renderingContext;
                OWLNamedIndividual individual = (OWLNamedIndividual) owlEntity;
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
    public void render(RenderletNexus nexus, OWLReasoner reasoner, OWLNamedIndividual individual) {
        getLog().trace("Association: " + individual);

        OWLOntology ontology = reasoner.getRootOntology();
        if (!previousBandMapByReasoner.containsKey(reasoner)) {
            sortBandsWithData(reasoner);
        }

        Map<BandInformation, BandInformation> previousBandMap = previousBandMapByReasoner.get(reasoner);
        BandInformation band = getBandInformation(individual, ontology);
        BandInformation previousBand = previousBandMap.get(band);

        StringBuilder svg = new StringBuilder();
        SVGArea location = nexus.getLocationOfRenderedEntity(band);
        SVGArea previousLocation = nexus.getLocationOfRenderedEntity(band);

        //there is no other association in this chromosmal band yet - render
        if (band.getRenderedAssociations().size() == 0) {
            getLog().trace("First association for this band");

            svg.append("<g ")
                    .append("id='").append(individual.getIRI().toString()).append("' ")
                    .append("transform='").append(chromosomeTransform(band.getChromosome())).append("' ")
                    .append("class='gwas-trait'>");

            SVGArea bandCoords = band.getCoordinates();
            if (bandCoords != null) {
                double x = bandCoords.getX();
                double y = bandCoords.getY();
                double width = bandCoords.getWidth();
                double height = bandCoords.getHeight();
                double newY = y + (height / 2);
                double endY = newY;
                double length = 1.75 * width;
                double newHeight = 0;


                // fanning algorithm
                if (previousLocation != null) {
                    double prevY = previousLocation.getY();
                    double prevHeight = previousLocation.getHeight(); // todo - I think?
                    double radius = 0.35 * width;

                    if (band.getBandName().contains("p")) {
                        int drop = ((band.getTraitNames().size() - 1) / 6) + 2;
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
                band.setY(endY);

                StringBuilder d = new StringBuilder();
                if (band.getPreviousBand() == null || newHeight == 0) {
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

                SVGArea currentArea = new SVGArea(x, newY, length, newHeight, 0);
                RenderingEvent event = new RenderingEvent(individual, svg.toString(), currentArea, this);
                nexus.renderingEventOccurred(event);
                band.setRenderedAssociation(individual);
            }
        }
//        else { // todo - do we actually need to do anything here?
//            //there is already another association in this band - can't render the association but need to render the trait as well as add to various nexus lists
//            getLog().trace("Secondary association: " + individual + " for band " + band.getBandName());
//            //get the SVG for the first assocation rendered for this band and reuse it for this association, but without adding it to the SVG file
//            OWLNamedIndividual previousEntity = band.getRenderedAssociations().get(0);
//            g = nexus.getRenderingEvent(previousEntity).getRenderedSVG();
//            g.setAttribute("id", renderingEntity.getIRI().toString());
//            RenderingEvent event =
//                    new RenderingEvent(individual, g.toString(), nexus.getLocationOfEntity(previousEntity), this);
//            nexus.renderingEventOccurred(event);
//            band.setRenderedAssociation(renderingEntity);
//        }
    }

    public BandInformation getBandInformation(OWLIndividual individual, OWLOntology ontology) {
        String bandName = null;
        OWLDataFactory dataFactory = OWLManager.createOWLOntologyManager().getOWLDataFactory();

        //get all the is_about individuals of this trait-assocation
        OWLObjectProperty is_about = dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.IS_ABOUT_IRI));
        Set<OWLIndividual> related = individual.getObjectPropertyValues(is_about, ontology);
        for (OWLIndividual snp : related) {
            //find the individual that is of type SNP
            for (OWLClassExpression allType : snp.getTypes(ontology)) {
                OWLClass typeClass = allType.asOWLClass();
                if (typeClass.getIRI().equals(IRI.create(OntologyConstants.SNP_CLASS_IRI))) {
                    //get the snp cytogenetic band
                    OWLObjectProperty has_band =
                            dataFactory.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));

                    Set<OWLIndividual> bands = snp.getObjectPropertyValues(has_band, ontology);
                    if (bands.size() == 1) {
                        OWLIndividual band = bands.iterator().next();

                        //get the band's name
                        OWLDataProperty has_name =
                                dataFactory.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));
                        Set<OWLLiteral> bandNames = band.getDataPropertyValues(has_name, ontology);
                        if (bandNames.size() == 1) {
                            bandName = bandNames.iterator().next().getLiteral();
                        }
                        else {
                            throw new RuntimeException(
                                    "Band OWLIndividual '" + band + "' has more than one band name");
                        }
                    }
                    else {
                        throw new RuntimeException("SNP OWLIndividual '" + individual + "' has more than one band");
                    }
                }
            }
        }
        if (bandName == null) {
            throw new IllegalArgumentException(
                    "Band has a null value set for it's name; this cannot be rendered");
        }
        return new BandInformation(bandName, "unknown");
    }

    public String chromosomeTransform(String chromosome) {
        int position;
        if (chromosome.equals("X")) {
            position = 22;
        }
        else if (chromosome.equals("Y")) {
            position = 23;
        }
        else {
            position = Integer.parseInt(chromosome) - 1;
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

    }
}