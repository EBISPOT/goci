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

//    public void setCoordinates(SVGArea area) {
//        coordinates = area;
//    }
//
//    public SVGArea getCoordinates() {
//        return coordinates;
//    }
//
//    public void setAssociation(OWLNamedIndividual association, String traitName, float pvalue, Date date) {
//        Association assoc = new Association(association, traitName, pvalue, date);
//        associations.add(assoc);
//    }
//
//    public ArrayList<Association> getAssociations() {
//        return associations;
//    }
//
//    public void setTraitName(String traitName) {
//        traitNames.add(traitName);
//    }
//
//    public ArrayList<String> getTraitNames() {
//        return traitNames;
//    }
//
//    public void setPreviousBand(String name) {
//        previousBand = name;
//    }
//
//    public String getPreviousBand() {
//        return previousBand;
//    }
//
//    public void setNextBand(String name) {
//        nextBand = name;
//    }
//
//    public String getNextBand() {
//        return nextBand;
//    }
//
//    public void setY(double y) {
//        traitY = y;
//    }
//
//    public double getY() {
//        return traitY;
//    }
//
//    public void setRenderedAssociation(OWLNamedIndividual association) {
//        renderedAssociations.add(association);
//    }
//
//    public ArrayList<OWLNamedIndividual> getRenderedAssociations() {
//        return renderedAssociations;
//    }
//
//    public void setRenderedTrait(String traitName, OWLNamedIndividual trait) {
//        renderedTraits.put(traitName, trait);
//    }
//
//    public Set<String> getRenderedTraits() {
//        return renderedTraits.keySet();
//    }
//
//    public OWLNamedIndividual getRenderedTrait(String name) {
//        return renderedTraits.get(name);
//    }
//
//    public void sortByDate() {
//        Association current;
//        int n = associations.size();
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 1; j < (n - i); j++) {
//                if ((associations.get(j - 1).getDate() != null) && (associations.get(j).getDate() != null)) {
//                    if (associations.get(j - 1).getDate().compareTo(associations.get(j).getDate()) > 0) {
//                        current = associations.get(j - 1);
//                        associations.set(j - 1, associations.get(j));
//                        associations.set(j, current);
//                    }
//                }
//                //render any associatons with a null date last
//                else if (associations.get(j - 1).getDate() == null) {
//                    current = associations.get(j - 1);
//                    associations.set(j - 1, associations.get(j));
//                    associations.set(j, current);
//                }
//            }
//        }
//    }

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
