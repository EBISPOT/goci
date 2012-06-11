package uk.ac.ebi.fgpt.goci.pussycat.renderlet;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 31/05/12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class BandInformation {

    private ArrayList<OWLNamedIndividual> associations;
    private ArrayList<String> traitNames;
    private String bandName, nextBand, previousBand;
 //   private boolean shift;
    double traitY;

    public BandInformation(String name){
        bandName = name;
        associations = new ArrayList<OWLNamedIndividual>();
        traitNames = new ArrayList<String>();
 //       shift = false;
        traitY = 0;
    }

    public void setAssociation(OWLNamedIndividual association){
         associations.add(association);
    }

    public ArrayList<OWLNamedIndividual> getAssociations(){
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

 /*   public void setShift(boolean fan){
        shift = fan;
    }

    public boolean getShift(){
        return shift;
    }       */

    public void setY(double y){
        traitY = y;
    }

    public double getY(){
        return traitY;
    }
}
