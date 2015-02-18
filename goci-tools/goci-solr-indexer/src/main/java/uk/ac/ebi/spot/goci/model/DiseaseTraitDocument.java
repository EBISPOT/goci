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
    @Field("study_pubmedId") private Collection<String> pubmedIds;
    @Field("study_title") private Collection<String> titles;
    @Field("study_author") private Collection<String> authors;
    @Field("study_publication") private Collection<String> publications;
    @Field("study_publicationDate") private Collection<String> publicationDates;
    @Field("study_catalogAddedDate") private Collection<String> catalogAddedDates;

    @Field("study_initialSampleDescription") private Collection<String> initialSampleDescriptions;
    @Field("study_replicateSampleDescription") private Collection<String> replicateSampleDescriptions;

    // embedded Association info
    @Field("association_qualifier") private Collection<String> qualifiers;
    @Field("association_pValue") private Collection<Float> pValues;
    @Field("association_orPerCopyNum") private Collection<Float> orPerCopyNums;
    @Field("association_orPerCopyUnitDescr") private Collection<String> orPerCopyUnitDescrs;
    @Field("association_orPerCopyRange") private Collection<String> orPerCopyRanges;
    @Field("association_orType") private Collection<String> orTypes;
    @Field("association_rsId") private Collection<String> rsIds;
    @Field("association_strongestAllele") private Collection<String> strongestAlleles;
    @Field("association_context") private Collection<String> contexts;
    @Field("association_region") private Collection<String> regions;
    @Field("association_mappedGene") private Collection<String> mappedGenes;
    @Field("association_reportedGene") private Collection<String> reportedGenes;
    @Field("association_chromosomeName") private Collection<String> chromosomeNames;
    @Field("association_chromosomePosition") private Collection<Integer> chromosomePositions;
    @Field("association_last_modified") private Collection<String> lastModifiedDates;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

    public DiseaseTraitDocument(DiseaseTrait diseaseTrait) {
        super(diseaseTrait);
        this.traitName = diseaseTrait.getTrait();

        this.pubmedIds = new LinkedHashSet<>();
        this.titles = new LinkedHashSet<>();
        this.authors = new LinkedHashSet<>();
        this.publications = new LinkedHashSet<>();
        this.publicationDates = new LinkedHashSet<>();
        this.catalogAddedDates = new LinkedHashSet<>();
        this.initialSampleDescriptions = new LinkedHashSet<>();
        this.replicateSampleDescriptions = new LinkedHashSet<>();

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
        this.pubmedIds.add(pubmedId);
    }

    public void addTitle(String title) {
        this.titles.add(title);
    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void addPublication(String publication) {
        this.publications.add(publication);
    }

    public void addPublicationDate(String publicationDate) {
        this.publicationDates.add(publicationDate);
    }

    public void addCatalogAddedDate(String catalogAddedDate) {
        this.catalogAddedDates.add(catalogAddedDate);
    }

    public void addInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescriptions.add(initialSampleDescription);
    }

    public void addReplicateSampleDescription(String replicateSampleDescription) {
        this.replicateSampleDescriptions.add(replicateSampleDescription);
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
