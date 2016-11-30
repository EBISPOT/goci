package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/12/14
 *
 */
public class StudyDocument extends OntologyEnabledDocument<Study> {
    // basic study information
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;
    @Field private String publicationDate;
    @Field private String catalogPublishDate;
    @Field private String publicationLink;
    @Field private String platform;
    @Field private String accessionId;
    @Field @NonEmbeddableField private Boolean fullPvalueSet;

    @Field private String initialSampleDescription;
    @Field private String replicateSampleDescription;

    @Field private Collection<String> ancestralGroups;
    @Field private Collection<String> countriesOfOrigin;
    @Field private Collection<String> countriesOfRecruitment;
    @Field private Collection<Integer> numberOfIndividuals;
    @Field private Collection<String> additionalAncestryDescription;
    @Field private Collection<String> ancestryLinks;


    @Field @NonEmbeddableField private int associationCount;

    // embedded Association info
    @Field("association_qualifier") private Collection<String> qualifiers;
    @Field("association_rsId") private Collection<String> rsIds;
    @Field("association_strongestAllele") private Collection<String> strongestAlleles;
    @Field("association_context") private Collection<String> contexts;
    @Field("association_regions") private Collection<String> regions;
    @Field("association_entrezMappedGenes") private Collection<String> entrezMappedGenes;
    @Field("association_entrezMappedGeneLinks") private Collection<String> entrezMappedGeneLinks;
    //    @Field("association_ensemblMappedGenes") private Collection<String> ensemblMappedGenes;
    //    @Field("association_ensemblMappedGeneLinks") private Collection<String> ensemblMappedGeneLinks;
    @Field("association_reportedGene") private Collection<String> reportedGenes;
    @Field("association_reportedGeneLinks") private Collection<String> reportedGeneLinks;
    @Field("association_chromosomeName") private Collection<String> chromosomeNames;
    @Field("association_chromosomePosition") private Collection<Integer> chromosomePositions;
    @Field("association_last_modified") private Collection<String> lastModifiedDates;
    @Field("association_positionLinks") private Collection<String> positionLinks;

    //    @Field("association_locusDescription") private Collection<String> locusDescriptions;
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
        this.accessionId = study.getAccessionId();
        this.fullPvalueSet = study.getFullPvalueSet();

        this.initialSampleDescription = study.getInitialSampleSize();
        this.replicateSampleDescription = study.getReplicateSampleSize();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (study.getPublicationDate() != null) {
            this.publicationDate = df.format(study.getPublicationDate());
        }
        if (study.getHousekeeping().getCatalogPublishDate() != null) {
            this.catalogPublishDate = df.format(study.getHousekeeping().getCatalogPublishDate());
        }

        String year;
        if (study.getPublicationDate() != null) {
            Calendar studyCal = Calendar.getInstance();
            studyCal.setTime(study.getPublicationDate());
            year = String.valueOf(studyCal.get(Calendar.YEAR));
        }
        else {
            year = "N/A";
        }
        this.publicationLink = author.concat("|").concat(year).concat("|").concat(pubmedId);

        this.platform = embedPlatformField(study);

        this.ancestralGroups = new LinkedHashSet<>();
        this.countriesOfOrigin = new LinkedHashSet<>();
        this.countriesOfRecruitment = new LinkedHashSet<>();
        this.numberOfIndividuals = new LinkedHashSet<>();
        this.additionalAncestryDescription = new LinkedHashSet<>();
        this.ancestryLinks = new LinkedHashSet<>();
        embedAncestryData(study);

        this.qualifiers = new LinkedHashSet<>();
        this.rsIds = new LinkedHashSet<>();
        this.strongestAlleles = new LinkedHashSet<>();
        this.contexts = new LinkedHashSet<>();
        this.regions = new LinkedHashSet<>();
        this.entrezMappedGenes = new LinkedHashSet<>();
        this.entrezMappedGeneLinks = new LinkedHashSet<>();
        //        this.ensemblMappedGenes = new LinkedHashSet<>();
        //        this.ensemblMappedGeneLinks = new LinkedHashSet<>();
        this.reportedGenes = new LinkedHashSet<>();
        this.reportedGeneLinks = new LinkedHashSet<>();
        this.chromosomeNames = new LinkedHashSet<>();
        this.chromosomePositions = new LinkedHashSet<>();
        this.positionLinks = new LinkedHashSet<>();
        this.lastModifiedDates = new LinkedHashSet<>();
        //        this.locusDescriptions = new LinkedHashSet<>();

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

    public String getCatalogPublishDate() {
        return catalogPublishDate;
    }

    public String getPublicationLink() {
        return publicationLink;
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

    public void addRegion(Collection<String> regions) {
        this.regions.addAll(regions);
    }

    public void addEntrezMappedGenes(Collection<String> mappedGenes) {
        this.entrezMappedGenes.addAll(mappedGenes);
    }

    public void addEntrezMappedGeneLinks(Collection<String> mappedGeneLinks) {
        this.entrezMappedGeneLinks.addAll(mappedGeneLinks);
    }

    //    public void addEnsemblMappedGenes(Collection<String> mappedGenes) {
    //        this.ensemblMappedGenes.addAll(mappedGenes);
    //    }
    //
    //    public void addEnsemblMappedGeneLinks(Collection<String> mappedGeneLinks) {
    //        this.ensemblMappedGeneLinks.addAll(mappedGeneLinks);
    //    }


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

    public void addPositionLinks(Collection<String> positionLinks) {
        this.positionLinks.addAll(positionLinks);
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

    public Collection<String> getAncestralGroups() {
        return ancestralGroups;
    }

    public Collection<String> getCountriesOfRecruitment() {
        return countriesOfRecruitment;
    }


    public Collection<Integer> getNumberOfIndividuals() {
        return numberOfIndividuals;
    }


    public Collection<String> getAncestryLinks() {
        return ancestryLinks;
    }


    private void embedAncestryData(Study study) {
        study.getAncestries().forEach(
                ancestry -> {
                    String ancestryLink = "";

                    String type = ancestry.getType();

                    ancestryLink = type;

                    String coo;

                    if(ancestry.getCountryOfOrigin() != null) {
                        coo = ancestry.getCountryOfOrigin();
                    }
                    else {
                        coo = "NR";
                    }

                    ancestryLink = ancestryLink.concat("|").concat(coo);


                    String cor;

                    if (ancestry.getCountryOfRecruitment() != null) {
                        cor = ancestry.getCountryOfRecruitment();
                    }
                    else {
                        cor = "NR";
                    }
                    countriesOfRecruitment.add(cor);
                    ancestryLink = ancestryLink.concat("|").concat(cor);

                    String ances = "";

                    if (ancestry.getAncestralGroup() != null) {
                        ances = ancestry.getAncestralGroup();
                    }
                    else{
                        ances = "NR";
                    }

                    ancestralGroups.add(ances);
                    ancestryLink = ancestryLink.concat("|").concat(ances);

                    String noInds = "";

                    if (ancestry.getNumberOfIndividuals() != null) {
                        numberOfIndividuals.add(ancestry.getNumberOfIndividuals());
                        noInds = String.valueOf(ancestry.getNumberOfIndividuals());
                    }
                    else{
                        noInds = "NA";
                    }

                    ancestryLink = ancestryLink.concat("|").concat(noInds);

                    String description = "";

                    if(ancestry.getDescription() != null && ancestry.getDescription().trim() != ""){
                        additionalAncestryDescription.add(ancestry.getDescription());
                        description = ancestry.getDescription();
                    }
                    else {
                        description = "NA";
                    }

                    ancestryLink = ancestryLink.concat("|").concat(description);

                    ancestryLinks.add(ancestryLink);

                }
        );
    }

    public String getPlatform() {
        return platform;
    }


    //    public void addLocusDescription(String locusDescription){
    //        this.locusDescriptions.add(locusDescription);
    //    }

    private String embedPlatformField(Study study) {
        String platform = "";
        List<String> manufacturers = new ArrayList<>();
        study.getPlatforms().forEach(
                p -> {
                    String manufacturer = p.getManufacturer();
                    manufacturers.add(manufacturer);
                }
        );


        if (manufacturers.size() != 0) {
            Collections.sort(manufacturers);
            for (String m : manufacturers) {
                if (platform.equals("")) {
                    platform = m;
                }
                else {
                    platform = platform.concat(", ").concat(m);
                }
            }
        }
        else {
            platform = "NR";
        }


        platform = platform.concat(" [");

        if (study.getQualifier() != null) {
            platform = platform.concat(study.getQualifier()).concat(" ");
        }

        if (study.getSnpCount() != null) {
            platform = platform.concat(study.getSnpCount().toString()).concat("]");
        }
        else if (study.getSnpCount() == null && manufacturers.size() != 0 && study.getStudyDesignComment() != null){
             platform = platform.concat(study.getStudyDesignComment()).concat("]");
        }
        else {
            platform = platform.concat("NR]");
        }

        if (study.getImputed()) {
            platform = platform.concat(" (imputed)");
        }

        if (manufacturers.size() == 0 && study.getSnpCount() == null && study.getStudyDesignComment() != null) {
            platform = study.getStudyDesignComment();
         }


        return platform;
    }


    public Collection<String> getAdditionalAncestryDescription() {
        return additionalAncestryDescription;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public Boolean getFullPvalueSet() { return fullPvalueSet; }
}
