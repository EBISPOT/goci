package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @Field private int associationCount;

    // embedded association info
    @Field("riskFrequency") private Collection<String> riskFrequencies;
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

    // embedded trait info


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

        this.riskFrequencies = new LinkedHashSet<>();
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

    public Collection<String> getRiskFrequencies() {
        return riskFrequencies;
    }

    public void addRiskFrequency(String riskFrequency) {
        this.riskFrequencies.add(riskFrequency);
    }

    public Collection<String> getQualifiers() {
        return qualifiers;
    }

    public void addQualifier(String qualifier) {
        this.qualifiers.add(qualifier);
    }

    public Collection<Float> getpValues() {
        return pValues;
    }

    public void addPValue(float pValue) {
        this.pValues.add(pValue);
    }

    public Collection<Float> getOrPerCopyNums() {
        return orPerCopyNums;
    }

    public void addOrPerCopyNum(float orPerCopyNum) {
        this.orPerCopyNums.add(orPerCopyNum);
    }

    public Collection<String> getOrPerCopyUnitDescrs() {
        return orPerCopyUnitDescrs;
    }

    public void addOrPerCopyUnitDescr(String orPerCopyUnitDescr) {
        this.orPerCopyUnitDescrs.add(orPerCopyUnitDescr);
    }

    public Collection<String> getOrPerCopyRanges() {
        return orPerCopyRanges;
    }

    public void addOrPerCopyRange(String orPerCopyRange) {
        this.orPerCopyRanges.add(orPerCopyRange);
    }

    public Collection<String> getOrTypes() {
        return orTypes;
    }

    public void addOrType(String orType) {
        this.orTypes.add(orType);
    }

    public Collection<String> getRsIds() {
        return rsIds;
    }

    public void addRsId(String rsId) {
        this.rsIds.add(rsId);
    }

    public Collection<String> getStrongestAlleles() {
        return strongestAlleles;
    }

    public void addStrongestAllele(String strongestAllele) {
        this.strongestAlleles.add(strongestAllele);
    }

    public Collection<String> getContexts() {
        return contexts;
    }

    public void addContext(String context) {
        this.contexts.add(context);
    }

    public Collection<String> getRegions() {
        return regions;
    }

    public void addRegion(String region) {
        this.regions.add(region);
    }

    public Collection<String> getMappedGenes() {
        return mappedGenes;
    }

    public void addMappedGene(String mappedGene) {
        this.mappedGenes.add(mappedGene);
    }

    public Collection<String> getReportedGenes() {
        return reportedGenes;
    }

    public void addReportedGenes(Collection<String> reportedGenes) {
        this.reportedGenes.addAll(reportedGenes);
    }


    public Collection<String> getChromosomeNames() {
        return chromosomeNames;
    }

    public void addChromosomeNames(Collection<String> chromosomeNames) {
        this.chromosomeNames.addAll(chromosomeNames);
    }

    public Collection<Integer> getChromosomePositions() {
        return chromosomePositions;
    }

    public void addChromosomePositions(Collection<Integer> chromosomePositions) {
        this.chromosomePositions.addAll(chromosomePositions);
    }

    public Collection<String> getLastModifiedDates() {
        return lastModifiedDates;
    }

    public void addLastModifiedDates(Collection<String> lastModifiedDates) {
        this.lastModifiedDates.addAll(lastModifiedDates);
    }
}
