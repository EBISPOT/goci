package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.TimeZone;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 */
public class StudyDocument extends OntologyEnabledDocument<Study> {
    // basic study information
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;
    @Field private String publicationDate;
    @Field private String catalogAddedDate;
    @Field private String publicationLink;

    @Field private String platform;
    @Field private Boolean cnv;

    @Field private String initialSampleDescription;
    @Field private String replicateSampleDescription;

    @Field @NonEmbeddableField private int associationCount;

    // embedded Association info
    @Field("association_qualifier") private Collection<String> qualifiers;
    @Field("association_rsId") private Collection<String> rsIds;
    @Field("association_strongestAllele") private Collection<String> strongestAlleles;
    @Field("association_context") private Collection<String> contexts;
    @Field("association_region") private Collection<String> regions;
    @Field("association_mappedGene") private Collection<String> mappedGenes;
    @Field("association_mappedGeneLinks") private Collection<String> mappedGeneLinks;
    @Field("association_reportedGene") private Collection<String> reportedGenes;
    @Field("association_reportedGeneLinks") private Collection<String> reportedGeneLinks;
    @Field("association_chromosomeName") private Collection<String> chromosomeNames;
    @Field("association_chromosomePosition") private Collection<Integer> chromosomePositions;
    @Field("association_last_modified") private Collection<String> lastModifiedDates;
    @Field("association_locusDescription") private Collection<String> locusDescriptions;
//    @Field("association_merged") private Long merged;

    // embedded DiseaseTrait info
    @Field("traitName") private Collection<String> traitNames;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

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

        String year;
        if (study.getStudyDate() != null) {
            Calendar studyCal = Calendar.getInstance();
            studyCal.setTime(study.getStudyDate());
            year = String.valueOf(studyCal.get(Calendar.YEAR));
        }
        else {
            year = "N/A";
        }
        this.publicationLink = author.concat("|").concat(year).concat("|").concat(pubmedId);

        this.qualifiers = new LinkedHashSet<>();
        this.rsIds = new LinkedHashSet<>();
        this.strongestAlleles = new LinkedHashSet<>();
        this.contexts = new LinkedHashSet<>();
        this.regions = new LinkedHashSet<>();
        this.mappedGenes = new LinkedHashSet<>();
        this.mappedGeneLinks = new LinkedHashSet<>();
        this.reportedGenes = new LinkedHashSet<>();
        this.reportedGeneLinks = new LinkedHashSet<>();
        this.chromosomeNames = new LinkedHashSet<>();
        this.chromosomePositions = new LinkedHashSet<>();
        this.lastModifiedDates = new LinkedHashSet<>();
        this.locusDescriptions = new LinkedHashSet<>();

        this.traitNames = new LinkedHashSet<>();

        this.mappedLabels = new LinkedHashSet<>();
        this.mappedUris = new LinkedHashSet<>();
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

    public String getPublicationLink() {
        return publicationLink;
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

    public void addMappedGeneLinks(Collection<String> mappedGeneLinks) {
        this.mappedGeneLinks.addAll(mappedGeneLinks);
    }

    public void addReportedGenes(Collection<String> reportedGenes) {
        this.reportedGenes.addAll(reportedGenes);
    }

    public void addReportedGeneLinks(Collection<String> reportedGeneLinks) {
        this.reportedGeneLinks.addAll(reportedGeneLinks);
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

//    public void addMerged(Long merged){
//        this.merged = merged;
//    }

    public void addTraitName(String traitName) {
        this.traitNames.add(traitName);
    }

    public void addMappedLabel(String mappedLabel) {
        this.mappedLabels.add(mappedLabel);
    }

    public void addMappedUri(String mappedUri) {
        this.mappedUris.add(mappedUri);
    }

    public void addLocusDescription(String locusDescription){
        this.locusDescriptions.add(locusDescription);
    }
}
