package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Collection;
import java.util.HashSet;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
public class AssociationDocument extends EmbeddableDocument<Association> {
    // basic Association information
    @Field private String riskFrequency;
    @Field private String qualifier;

    @Field private float pValue;
    @Field private float orPerCopyNum;
    @Field private String orPerCopyUnitDescr;
    @Field private String orPerCopyRange;
    @Field private String orType;

    public AssociationDocument(Association association) {
        super(association);
        this.riskFrequency = association.getRiskFrequency();
        this.qualifier = association.getPvalueText();
        this.orPerCopyUnitDescr = association.getOrPerCopyUnitDescr();
        this.orType = String.valueOf(association.getOrType());
        this.orPerCopyRange = association.getOrPerCopyRange();

        if (association.getOrPerCopyNum() != null) {
            this.orPerCopyNum = association.getOrPerCopyNum();
        }
        if (association.getPvalueFloat() != null) {
            this.pValue = association.getPvalueFloat();
        }
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getOrPerCopyUnitDescr() {
        return orPerCopyUnitDescr;
    }

    public String getOrPerCopyRange() {
        return orPerCopyRange;
    }

    public float getpValue() {
        return pValue;
    }

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public String getOrType() {
        return orType;
    }
}
