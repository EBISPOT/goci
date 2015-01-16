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
public class AssociationDocument extends Document<Association> {
    @Field private String strongestAllele;
    @Field private String riskFrequency;
    @Field private String qualifier;
    @Field private String orPerCopyUnitDescr;

    @Field private float pValue;

    @Field("gene") private Collection<String> genes;
    @Field("trait") private Collection<String> traits;
    @Field("traitUri") private Collection<String> traitUris;


    public AssociationDocument(Association association) {
        super(association);
        this.strongestAllele = association.getStrongestAllele();
        this.riskFrequency = association.getRiskFrequency();
        this.qualifier = association.getPvalueText();
        this.orPerCopyUnitDescr = association.getOrPerCopyUnitDescr();
        if (association.getPvalueFloat() != null) {
            this.pValue = association.getPvalueFloat();
        }

        this.genes = new HashSet<>();
        association.getReportedGenes().forEach(gene -> genes.add(gene.getGeneName()));
        this.traits = new HashSet<>();
        this.traitUris = new HashSet<>();
        association.getEfoTraits().forEach(trait -> {
            traits.add(trait.getTrait());
            traitUris.add(trait.getUri());
        });
    }

    public String getStrongestAllele() {
        return strongestAllele;
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

    public float getpValue() {
        return pValue;
    }

    public Collection<String> getGenes() {
        return genes;
    }

    public Collection<String> getTraits() {
        return traits;
    }

    public Collection<String> getTraitUris() {
        return traitUris;
    }
}
