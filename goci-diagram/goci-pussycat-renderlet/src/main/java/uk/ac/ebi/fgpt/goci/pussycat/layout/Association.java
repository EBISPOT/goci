package uk.ac.ebi.fgpt.goci.pussycat.layout;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 03/08/12
 * Time: 13:24
 * To change this template use File | Settings | File Templates.
 */
public class Association {

    private OWLNamedIndividual association;
    private String traitName;
    private Date date;
    private float pvalue;

    public Association(OWLNamedIndividual association, String traitName, float pvalue, Date date){
        this.association = association;
        this.traitName = traitName;
        this.pvalue = pvalue;
        this.date = date;
    }
    public OWLNamedIndividual getAssociation(){
        return association;
    }
    public String getTraitName(){
        return traitName;
    }
    public double getPvalue(){
        return pvalue;
    }
    public Date getDate(){
        return date;
    }
}
