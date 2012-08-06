package uk.ac.ebi.fgpt.goci.pussycat.layout;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 31/05/12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class BandInformation {

    private ArrayList<Association> associations;
    private ArrayList<String> traitNames;
    private String bandName, nextBand, previousBand, chromosome;
    private double traitY;
    private SVGArea coordinates;
    private ArrayList<OWLNamedIndividual> renderedAssociations;
    private ArrayList<String> renderedTraits;

    public BandInformation(String name, String chrom){
        bandName = name;
        associations = new ArrayList<Association>();
        traitNames = new ArrayList<String>();
 //       shift = false;
        traitY = 0;
        renderedAssociations = new ArrayList<OWLNamedIndividual>();
        renderedTraits = new ArrayList<String>();
        chromosome = chrom;
    }

    public void setCoordinates(SVGArea area){
        coordinates = area;
    }

    public SVGArea getCoordinates(){
        return coordinates;
    }

    public String getChromosome(){
        return chromosome;
    }

    public void setAssociation(OWLNamedIndividual association, String traitName, float pvalue, Date date){
         Association assoc = new Association(association, traitName, pvalue, date);
         associations.add(assoc);
    }

    public ArrayList<Association> getAssociations(){
        return associations;
    }

    public void setTraitName(String traitName){
        traitNames.add(traitName);
    }

    public ArrayList<String> getTraitNames(){
        return traitNames;
    }

    public void setPreviousBand(String name){
        previousBand = name;
    }

    public String getPreviousBand(){
        return previousBand;
    }

    public void setNextBand(String name){
        nextBand = name;
    }

    public String getNextBand(){
        return nextBand;
    }

    public void setY(double y){
        traitY = y;
    }

    public double getY(){
        return traitY;
    }

    public void setRenderedAssociation(OWLNamedIndividual association){
        renderedAssociations.add(association);
    }

    public ArrayList<OWLNamedIndividual> getRenderedAssociations(){
        return renderedAssociations;
    }

    public void setRenderedTrait(String traitName){
        renderedTraits.add(traitName);
    }

    public ArrayList<String> getRenderedTraits(){
        return renderedTraits;
    }

    public void sortByDate(){
        Association current;
        int n = associations.size();

        for(int i = 0; i < n; i++){
           for(int j = 1; j < (n-i); j++){
               if(associations.get(j-1).getDate().compareTo(associations.get(j).getDate()) > 0){
                    current = associations.get(j-1);
                    associations.set(j - 1, associations.get(j));
                    associations.set(j, current);
                }

            }
        }
    }
}
