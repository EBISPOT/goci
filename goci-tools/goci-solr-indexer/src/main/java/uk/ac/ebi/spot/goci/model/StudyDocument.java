package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.TimeZone;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class StudyDocument extends EmbeddableDocument<Study> {
    // basic study information
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

    @Field @NonEmbeddableField private int associationCount;

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

    // embedded DiseaseTrait info
    @Field("traitName") private Collection<String> traitNames;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

    // embedded OntologyEnabledDocument info
    @Field("traitUri") private Collection<String> traitUris;
    @Field("shortForm") private Collection<String> shortForms;
    @Field("label") private Collection<String> labels;
    @Field("synonym") private Collection<String> synonyms;
    @Field("description") private Collection<String> descriptions;
    @Field("efoLink") private Collection<String> efoLinks;
    @Field("parent") private Collection<String> superclassLabels;

    public StudyDocument(Study study) {
        super(study);
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();

        this.platform = study.getPlatform();
        this.cnv = study.getCnv();

        this.initialSampleDescription = study.getInitialSampleSize();
        this.replicateSampleDescription = study.getReplicateSampleSize();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (study.getStudyDate() != null) {
            this.publicationDate = df.format(study.getStudyDate());
        }
        if (study.getHousekeeping().getPublishDate() != null) {
            this.catalogAddedDate = df.format(study.getHousekeeping().getPublishDate());
        }

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

        this.traitNames = new LinkedHashSet<>();

        this.mappedLabels = new LinkedHashSet<>();
        this.mappedUris = new LinkedHashSet<>();

        this.traitUris = new LinkedHashSet<>();
        this.shortForms = new LinkedHashSet<>();
        this.labels = new LinkedHashSet<>();
        this.synonyms = new LinkedHashSet<>();
        this.descriptions = new LinkedHashSet<>();
        this.efoLinks = new LinkedHashSet<>();
        this.superclassLabels = new LinkedHashSet<>();
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

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getCatalogAddedDate() {
        return catalogAddedDate;
    }

    public String getPlatform() {
        return platform;
    }

    public Boolean getCnv() {
        return cnv;
    }

    public String getInitialSampleDescription() {
        return initialSampleDescription;
    }

    public String getReplicateSampleDescription() {
        return replicateSampleDescription;
    }

    public int getAssociationCount() {
        return associationCount;
    }

    public void setAssociationCount(int associationCount) {
        this.associationCount = associationCount;
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

    public void addTraitName(String traitName) {
        this.traitNames.add(traitName);
    }

    public void addMappedLabel(String mappedLabel) {
        this.mappedLabels.add(mappedLabel);
    }

    public void addMappedUri(String mappedUri) {
        this.mappedUris.add(mappedUri);
    }

    public void addTraitUris(Collection<String> traitUris) {
        this.traitUris.addAll(traitUris);
    }

    public void addShortForms(Collection<String> shortForms) {
        this.shortForms.addAll(shortForms);
    }

    public void addLabels(Collection<String> labels) {
        this.labels.addAll(labels);
    }

    public void addSynonyms(Collection<String> synonyms) {
        this.synonyms.addAll(synonyms);
    }

    public void addDescriptions(Collection<String> descriptions) {
        this.descriptions.addAll(descriptions);
    }

    public void addEfoLinks(Collection<String> efoLinks) {
        this.efoLinks.addAll(efoLinks);
    }

    public void addSuperclassLabels(Collection<String> superclassLabels) {
        this.superclassLabels.addAll(superclassLabels);
    }
}
