package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class DiseaseTraitDocument extends OntologyEnabledDocument<DiseaseTrait> {
    // basic DiseaseTrait information
    @Field private String traitName;

    // embedded study info
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;
    @Field private String publicationDate;
    @Field private String catalogAddedDate;

    @Field private String platform;
    @Field private Boolean cnv;

    @Field private String initialSampleDescription;
    @Field private String replicateSampleDescription;

    // embedded Association info
    @Field("qualifier") private Collection<String> qualifiers;
    @Field("pValue") private Collection<Float> pValues;
    @Field("orPerCopyNum") private Collection<Float> orPerCopyNums;
    @Field("orPerCopyUnitDescr") private Collection<String> orPerCopyUnitDescrs;
    @Field("orPerCopyRange") private Collection<String> orPerCopyRanges;
    @Field("orType") private Collection<String> orTypes;
    @Field("rsId") private Collection<String> rsIds;
    @Field("strongestAllele") private Collection<String> strongestAlleles;
    @Field("context") private Collection<String> contexts;
    @Field("region") private Collection<String> regions;
    @Field("mappedGene") private Collection<String> mappedGenes;
    @Field("reportedGene") private Collection<String> reportedGenes;
    @Field("chromosomeName") private Collection<String> chromosomeNames;
    @Field("chromosomePosition") private Collection<Integer> chromosomePositions;
    @Field("last_modified") private Collection<String> lastModifiedDates;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

    public DiseaseTraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.traitName = diseaseTrait.getTrait();

        this.qualifiers = new LinkedHashSet<>();
        this.pValues = new LinkedHashSet<>();
        this.orPerCopyNums = new LinkedHashSet<>();
        this.orPerCopyUnitDescrs = new LinkedHashSet<>();
        this.orPerCopyRanges = new LinkedHashSet<>();
        this.orTypes = new LinkedHashSet<>();
        this.rsIds = new LinkedHashSet<>();
        this.strongestAlleles = new LinkedHashSet<>();
        this.contexts = new LinkedHashSet<>();
        this.regions = new LinkedHashSet<>();
        this.mappedGenes = new LinkedHashSet<>();
        this.reportedGenes = new LinkedHashSet<>();
        this.chromosomeNames = new LinkedHashSet<>();
        this.chromosomePositions = new LinkedHashSet<>();
        this.lastModifiedDates = new LinkedHashSet<>();

        this.mappedLabels = new LinkedHashSet<>();
        this.mappedUris = new LinkedHashSet<>();
    }

    public String getTraitName() {
        return traitName;
    }

    public void addPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public void addTitle(String title) {
        this.title = title;
    }

    public void addAuthor(String author) {
        this.author = author;
    }

    public void addPublication(String publication) {
        this.publication = publication;
    }

    public void addPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void addCatalogAddedDate(String catalogAddedDate) {
        this.catalogAddedDate = catalogAddedDate;
    }

    public void addPlatform(String platform) {
        this.platform = platform;
    }

    public void addCnv(Boolean cnv) {
        this.cnv = cnv;
    }

    public void addInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescription = initialSampleDescription;
    }

    public void addReplicateSampleDescription(String replicateSampleDescription) {
        this.replicateSampleDescription = replicateSampleDescription;
    }

    public void addQualifier(String qualifier) {
        this.qualifiers.add(qualifier);
    }

    public void addPValue(float pValue) {
        this.pValues.add(pValue);
    }

    public void addOrPerCopyNum(float orPerCopyNum) {
        this.orPerCopyNums.add(orPerCopyNum);
    }

    public void addOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescrs.add(orPerCopyUnitDescr);
    }

    public void addOrPerCopyRange(String orPerCopyRange) {
        this.orPerCopyRanges.add(orPerCopyRange);
    }

    public void addOrType(String orType) {
        this.orTypes.add(orType);
    }

    public void addRsId(String rsId) {
        this.rsIds.add(rsId);
    }

    public void addStrongestAllele(String strongestAllele) {
        this.strongestAlleles.add(strongestAllele);
    }

    public void addContext(String context) {
        this.contexts.add(context);
    }

    public void addRegion(String region) {
        this.regions.add(region);
    }

    public void addMappedGene(String mappedGene) {
        this.mappedGenes.add(mappedGene);
    }

    public void addReportedGenes(Collection<String> reportedGenes) {
        this.reportedGenes.addAll(reportedGenes);
    }

    public void addChromosomeNames(Collection<String> chromosomeNames) {
        this.chromosomeNames.addAll(chromosomeNames);
    }

    public void addChromosomePositions(Collection<Integer> chromosomePositions) {
        this.chromosomePositions.addAll(chromosomePositions);
    }

    public void addLastModifiedDates(Collection<String> lastModifiedDates) {
        this.lastModifiedDates.addAll(lastModifiedDates);
    }

    public void addMappedLabel(String mappedLabel) {
        this.mappedLabels.add(mappedLabel);
    }

    public void addMappedUri(String mappedUri) {
        this.mappedUris.add(mappedUri);
    }
}
