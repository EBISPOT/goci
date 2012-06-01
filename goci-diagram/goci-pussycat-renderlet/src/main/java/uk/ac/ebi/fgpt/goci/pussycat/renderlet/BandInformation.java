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
    private String bandName;

    public BandInformation(String name){
        bandName = name;
        associations = new ArrayList<OWLNamedIndividual>();
        traitNames = new ArrayList<String>();
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





}
