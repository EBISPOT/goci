package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

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

    @Field private float orPerCopyNum;
    @Field private float pValue;

    @Field("gene") private Collection<String> genes;
    @Field("trait") private Collection<String> traits;
    @Field("traitUri") private Collection<String> traitUris;

    // additional fields from study
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;

    // additional fields from snp
    // NB. gene field is already captured in author reported genes, may need to split?
    @Field("rsId") private Set<String> rsIds;
    @Field("chromosomeName") private Set<String> chromosomeNames;
    @Field("chromosomePosition") private Set<Integer> chromosomePositions;
    @Field("region") private Set<String> regions;
    @Field("last_modified") private Set<String> lastModifiedDates;


    public AssociationDocument(Association association) {
        super(association);
        this.strongestAllele = association.getStrongestAllele();
        this.riskFrequency = association.getRiskFrequency();
        this.qualifier = association.getPvalueText();
        this.orPerCopyUnitDescr = association.getOrPerCopyUnitDescr();
        if(association.getOrPerCopyNum() != null) {
            this.orPerCopyNum = association.getOrPerCopyNum();
        }
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

        Study study = association.getStudy();
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();
        if (study.getDiseaseTrait() != null) {
            traits.add(study.getDiseaseTrait().getTrait());
        }
        this.traitUris = new ArrayList<>();
        study.getEfoTraits().forEach(efoTrait -> traitUris.add(efoTrait.getUri()));

        this.rsIds = new HashSet<>();
        this.chromosomeNames = new HashSet<>();
        this.chromosomePositions = new HashSet<>();
        this.regions = new HashSet<>();
        this.lastModifiedDates = new HashSet<>();
        for (SingleNucleotidePolymorphism snp : association.getSnps()) {
            rsIds.add(snp.getRsId());
            chromosomeNames.add(snp.getChromosomeName());
            if (snp.getChromosomePosition() != null) {
                chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
            }
            snp.getRegions().forEach(region -> regions.add(region.getName()));
            snp.getGenes().forEach(gene -> genes.add(gene.getGeneName()));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            if (snp.getLastUpdateDate() != null) {
                lastModifiedDates.add(df.format(snp.getLastUpdateDate()));
            }
        }
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

    public String getPubmedId() {
        return pubmedId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublication() {
        return publication;
    }

    public Set<String> getRsIds() {
        return rsIds;
    }

    public Set<String> getChromosomeNames() {
        return chromosomeNames;
    }

    public Set<Integer> getChromosomePositions() {
        return chromosomePositions;
    }

    public Set<String> getRegions() {
        return regions;
    }

    public Set<String> getLastModifiedDates() {
        return lastModifiedDates;
    }

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }
}
