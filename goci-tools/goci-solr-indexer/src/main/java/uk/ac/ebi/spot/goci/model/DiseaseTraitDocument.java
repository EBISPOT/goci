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
    @Field("study_pubmedId") private Collection<String> pubmedIds; // combine in one connected field
    @Field("study_title") private Collection<String> titles;
    @Field("study_author") private Collection<String> authors; // combine in one connected field
    @Field("study_orcid") private Collection<String> orcids; // combine in one connected field
    @Field("study_publication") private Collection<String> publications;
    @Field("study_publicationDate") private Collection<String> publicationDates; // combine in one connected field
    @Field("study_catalogPublishDate") private Collection<String> catalogPublishDates;
    @Field("study_publicationLink") private Collection<String> publicationLinks;

    @Field("study_platform") private Collection<String> platforms;
    @Field("study_accessionId") private Collection<String> accessionIds;

    @Field("study_initialSampleDescription") private Collection<String> initialSampleDescriptions;
    @Field("study_replicateSampleDescription") private Collection<String> replicateSampleDescriptions;
    @Field("study_ancestralGroups") private Collection<String> ancestralGroups;
    @Field("study_countriesOfOrigin") private Collection<String> countriesOfOrigin;
    @Field("study_countriesOfRecruitment") private Collection<String> countriesOfRecruitment;
    @Field("study_numberOfIndividuals") private Collection<Integer> numberOfIndividuals;
    @Field("study_additionalAncestryDescription") private Collection<String> additionalAncestryDescription;
    @Field("study_ancestryLinks") private Collection<String> ancestryLinks;

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
    @Field("association_positionLinks") private Collection<String> positionLinks;
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
        this.orcids = new LinkedHashSet<>();
        this.publications = new LinkedHashSet<>();
        this.publicationDates = new LinkedHashSet<>();
        this.catalogPublishDates = new LinkedHashSet<>();
        this.publicationLinks = new LinkedHashSet<>();

        this.platforms = new LinkedHashSet<>();
        this.accessionIds = new LinkedHashSet<>();

        this.initialSampleDescriptions = new LinkedHashSet<>();
        this.replicateSampleDescriptions = new LinkedHashSet<>();

        this.ancestralGroups = new LinkedHashSet<>();
        this.countriesOfOrigin = new LinkedHashSet<>();
        this.countriesOfRecruitment = new LinkedHashSet<>();
        this.numberOfIndividuals = new LinkedHashSet<>();
        this.additionalAncestryDescription = new LinkedHashSet<>();
        this.ancestryLinks = new LinkedHashSet<>();

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

    public void addAuthor(String author) { this.authors.add(author); }

    public void addOrcid(String orcid) {
        this.orcids.add(orcid);
    }

    public void addPublication(String publication) {
        this.publications.add(publication);
    }

    public void addPublicationDate(String publicationDate) {
        this.publicationDates.add(publicationDate);
    }

    public void addCatalogPublishDate(String catalogPublishDate) {
        this.catalogPublishDates.add(catalogPublishDate);
    }

    public void addPublicationLink(String publicationLink) {
        this.publicationLinks.add(publicationLink);
    }

    public void addPlatform(String platform) {
        this.platforms.add(platform);
    }

    public void addAccessionId(String accessionId){
        this.accessionIds.add(accessionId);
    }


    public void addInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescriptions.add(initialSampleDescription);
    }

    public void addReplicateSampleDescription(String replicateSampleDescription) {
        this.replicateSampleDescriptions.add(replicateSampleDescription);
    }

    public void addAncestralGroups(Collection<String> ancestralGroups) {
        this.ancestralGroups.addAll(ancestralGroups);
    }

    public void addCountriesOfOrigin(Collection<String> countriesOfOrigin) {
        this.countriesOfOrigin.addAll(countriesOfOrigin);
    }

    public void addCountriesOfRecruitment(Collection<String> countriesOfRecruitment) {
        this.countriesOfRecruitment.addAll(countriesOfRecruitment);
    }

    public void addNumberOfIndividuals(Collection<Integer> numberOfIndividuals) {
        this.numberOfIndividuals.addAll(numberOfIndividuals);
    }

    public void addAdditionalAncestryDescription(Collection<String> additionalAncestryDescription){
        this.additionalAncestryDescription.addAll(additionalAncestryDescription);
    }

    public void addAncestryLinks(Collection<String> ancestryLinks) {
        this.ancestryLinks.addAll(ancestryLinks);
    }

    public void addAncestralGroup(String ancestralGroup) {
        this.ancestralGroups.add(ancestralGroup);
    }

    public void addCountryOfRecruitment(String countryOfRecruitment) {
        this.countriesOfRecruitment.add(countryOfRecruitment);
    }

    public void addNumberOfIndiviuals(int numberOfIndividuals) {
        this.numberOfIndividuals.add(numberOfIndividuals);
    }

    public void addAncestryLink(String ancestryLink) {
        this.ancestryLinks.add(ancestryLink);
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

    public void addMappedLabel(String mappedLabel) {
        this.mappedLabels.add(mappedLabel);
    }

    public void addMappedUri(String mappedUri) {
        this.mappedUris.add(mappedUri);
    }
}
