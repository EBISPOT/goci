package uk.ac.ebi.fgpt.goci.pussycat.layout;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: dwelter Date: 31/05/12 Time: 16:15 To change this template use File | Settings |
 * File Templates.
 */
public class BandInformation {
    private final String bandName;
    private final String chromosome;

    private ArrayList<Association> associations;
    private ArrayList<String> traitNames;
    private String nextBand, previousBand;
    private double traitY;
    private SVGArea coordinates;
    private ArrayList<OWLNamedIndividual> renderedAssociations;
    private HashMap<String, OWLNamedIndividual> renderedTraits;

    public BandInformation(String name, String chrom) {
        this.bandName = name;
        this.chromosome = chrom;

        associations = new ArrayList<Association>();
        traitNames = new ArrayList<String>();
        traitY = 0;
        renderedAssociations = new ArrayList<OWLNamedIndividual>();
        renderedTraits = new HashMap<String, OWLNamedIndividual>();
    }

    public String getBandName() {
        return bandName;
    }

    public String getChromosome() {
        return chromosome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BandInformation that = (BandInformation) o;

        if (bandName != null ? !bandName.equals(that.bandName) : that.bandName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return bandName != null ? bandName.hashCode() : 0;
    }
}
