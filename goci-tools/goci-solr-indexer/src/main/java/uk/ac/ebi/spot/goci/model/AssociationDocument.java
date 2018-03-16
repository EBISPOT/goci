package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 16/01/15
 */
public class AssociationDocument extends OntologyEnabledDocument<Association> {
    // basic Association information
    @Field @NonEmbeddableField private String riskFrequency;
    @Field private String qualifier;

    @Field @NonEmbeddableField private Integer pValueMantissa;
    @Field @NonEmbeddableField private Integer pValueExponent;
    @Field @NonEmbeddableField private Float orPerCopyNum;
    @Field @NonEmbeddableField private Float betaNum;
    @Field @NonEmbeddableField private String betaUnit;
    @Field @NonEmbeddableField private String betaDirection;
    @Field("orDescription") @NonEmbeddableField private String description;
    @Field @NonEmbeddableField private String range;
//    @Field @NonEmbeddableField private String orType;
    @Field @NonEmbeddableField private Boolean snpInteraction;
    @Field @NonEmbeddableField private Boolean multiSnpHaplotype;

    // additional included genetic data...
    // capture loci/risk alleles for association;
    // if many, collapse risk allele and snp into a single field and use
    // 'x' or ',' to separate SNP x SNP and haplotype associations respectively
    @Field private String rsId;
    @Field private String strongestAllele;
    @Field private String context;
    //    @Field private String region;
    @Field("region") private Set<String> region;

    // mapped genes and reported genes must be per snp -
    // if multiple, separate mapped genes with a hyphen (downstream-upstream) and reported genes with a slash,
    // and then include 'x' or ',' as designated by multiple loci/risk alleles
    @Field("entrezMappedGenes") private Collection<String> entrezMappedGenes;
    @Field("entrezMappedGeneLinks") private Collection<String> entrezMappedGeneLinks;
    //    @Field("ensemblMappedGenes") private Collection<String> ensemblMappedGenes;
    //    @Field("ensemblMappedGeneLinks") private Collection<String> ensemblMappedGeneLinks;
    @Field("reportedGene") private Collection<String> reportedGenes;
    @Field("reportedGeneLinks") private Collection<String> reportedGeneLinks;
    @Field @NonEmbeddableField private Long merged;
    @Field @NonEmbeddableField private String currentSnp;

    @Field("studyId") @NonEmbeddableField private Collection<String> studyIds;

    // pluralise all other information, but retain order
    @Field("chromosomeName") private Set<String> chromosomeNames;
    @Field("chromosomePosition") private Set<Integer> chromosomePositions;
    @Field("chromLocation") @NonEmbeddableField  private Set<String> chromLocations;
    @Field("positionLinks") private Collection<String> positionLinks;

    @Field("locusDescription") @NonEmbeddableField private String locusDescription;

    // embedded study info
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String orcid;
    @Field private String authorAscii;
    @Field private Collection<String> authorsList;
    @Field private String publication;
    @Field private String publicationDate;
    @Field private String catalogPublishDate;
    @Field private String publicationLink;

    @Field private String platform;
    @Field private String accessionId;

    @Field private String initialSampleDescription;
    @Field private String replicateSampleDescription;

    @Field private Collection<String> ancestralGroups;
    @Field private Collection<String> countriesOfOrigin;
    @Field private Collection<String> countriesOfRecruitment;
    @Field private Collection<Integer> numberOfIndividuals;
    @Field private Collection<String> additionalAncestryDescription;
    @Field private Collection<String> ancestryLinks;


    // embedded DiseaseTrait info
    @Field("traitName") private Collection<String> traitNames;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

    public AssociationDocument(Association association) {
        super(association);
        this.riskFrequency = association.getRiskFrequency();
        this.qualifier = association.getPvalueDescription();
        this.description = association.getDescription();
        this.range = association.getRange();
        this.betaUnit = association.getBetaUnit();
        this.betaDirection = association.getBetaDirection();

        if (association.getOrPerCopyNum() != null) {
            this.orPerCopyNum = association.getOrPerCopyNum();
        }
        if (association.getBetaNum() != null) {
            this.betaNum = association.getBetaNum();
        }
        if (association.getPvalueMantissa() != null) {
            this.pValueMantissa = association.getPvalueMantissa();
        }
        if (association.getPvalueExponent() != null) {
            this.pValueExponent = association.getPvalueExponent();
        }

        snpInteraction = association.getSnpInteraction();
        multiSnpHaplotype = association.getMultiSnpHaplotype();

        this.region = new LinkedHashSet<>();
        this.chromosomeNames = new LinkedHashSet<>();
        this.chromosomePositions = new LinkedHashSet<>();
        this.chromLocations = new LinkedHashSet<>();
        this.positionLinks = new LinkedHashSet<>();

        this.entrezMappedGenes = new LinkedHashSet<>();
        this.entrezMappedGeneLinks = new LinkedHashSet<>();
        //        this.ensemblMappedGenes = new LinkedHashSet<>();
        //        this.ensemblMappedGeneLinks = new LinkedHashSet<>();
        this.reportedGenes = new LinkedHashSet<>();
        this.reportedGeneLinks = new LinkedHashSet<>();
        this.studyIds = new HashSet<>();
        embedGeneticData(association);

        this.traitNames = new LinkedHashSet<>();

        this.mappedLabels = new LinkedHashSet<>();
        this.mappedUris = new LinkedHashSet<>();

        this.ancestralGroups = new LinkedHashSet<>();
        this.countriesOfOrigin = new LinkedHashSet<>();
        this.countriesOfRecruitment = new LinkedHashSet<>();
        this.numberOfIndividuals = new LinkedHashSet<>();
        this.additionalAncestryDescription = new LinkedHashSet<>();
        this.ancestryLinks = new LinkedHashSet<>();
    }

    public Collection<String> getRegion() {
        return region;
    }

    public Collection<String> getEntrezMappedGenes() {
        return entrezMappedGenes;
    }

    public Collection<String> getEntrezMappedGeneLinks() {
        return entrezMappedGeneLinks;
    }

    //    public Collection<String> getEnsemblMappedGenes() {
    //        return ensemblMappedGenes;
    //    }
    //
    //    public Collection<String> getEnsemblMappedGeneLinks() {
    //        return ensemblMappedGeneLinks;
    //    }

    public String getStrongestAllele() {
        return strongestAllele;
    }

    public String getRiskFrequency() {
        return riskFrequency;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getDescription() {
        return description;
    }

    public String getRange() {
        return range;
    }

    public String getContext() {
        return context;
    }

    public int getpValueMantissa() {
        return pValueMantissa;
    }

    public int getpValueExponent() { return pValueExponent; }

    public Collection<String> getReportedGenes() {
        return reportedGenes;
    }

    public Collection<String> getReportedGeneLinks() {
        return reportedGeneLinks;
    }

    public String getRsId() {
        return rsId;
    }

    public Long getMerged() { return merged; }

    public Set<String> getChromosomeNames() {
        return chromosomeNames;
    }

    public Set<Integer> getChromosomePositions() {
        return chromosomePositions;
    }

    public Collection<String> getPositionLinks() {
        return positionLinks;
    }

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }

/*    public String getOrType() {
        return orType;
    }*/

    public void addPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public void addTitle(String title) {
        this.title = title;
    }

    public void addAuthor(String author) { this.author = author; }

    public void addOrcid(String orcid) { this.orcid = orcid; }

    public void addAuthorAscii(String authorAscii) { this.authorAscii = authorAscii; }

    public void addAuthorsList(Collection<String> authorsList) {this.authorsList = authorsList;}

    public void addPublication(String publication) {
        this.publication = publication;
    }

    public void addPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void addCatalogPublishDate(String catalogPublishDate) {
        this.catalogPublishDate = catalogPublishDate;
    }

    public void addPublicationLink(String publicationLink) {
        this.publicationLink = publicationLink;
    }

    public void addPlatform(String platform) {
        this.platform = platform;
    }

    public void addAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public void addInitialSampleDescription(String initialSampleDescription) {
        this.initialSampleDescription = initialSampleDescription;
    }

    public void addReplicateSampleDescription(String replicateSampleDescription) {
        this.replicateSampleDescription = replicateSampleDescription;
    }

    public void addAncestralGroups(Collection<String> ancestralGroups) {
        this.ancestralGroups.addAll(ancestralGroups);
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

    public void addCountryOfOrigin(String countryOfOrigin) {
        this.countriesOfOrigin.add(countryOfOrigin);
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

    public void addTraitName(String traitName) {
        this.traitNames.add(traitName);
    }

    public void addMappedLabel(String mappedLabel) {
        this.mappedLabels.add(mappedLabel);
    }

    public void addMappedUri(String mappedUri) {
        this.mappedUris.add(mappedUri);
    }

    public void addStudyId(String studyId) {
        this.studyIds.add(studyId);
    }

    private void embedGeneticData(Association association) {
        if (association.getMultiSnpHaplotype() || association.getSnpInteraction()) {
            final String[] entrezMappedGene = new String[1];
            final String[] reportedGeneString = new String[1];
            final String[] regionString = new String[1];
            final String[] contextString = new String[1];
            final String[] positionString = new String[1];

            String primary_delimiter;
            String secondary_delimiter;

            if(association.getSnpInteraction()){
                primary_delimiter = " x ";
                secondary_delimiter = " x ";
            }
            else {
                primary_delimiter = "; ";
                secondary_delimiter = ", ";
            }

            association.getLoci().forEach(
                    locus -> {
                        Set<String> regions = new LinkedHashSet<>();
                        final String[] localGeneString = new String[1];

                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                    strongestAllele =
                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), primary_delimiter);

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    rsId = setOrAppend(rsId, snp.getRsId(), primary_delimiter);

                                    merged = snp.getMerged();

                                    String current;
                                    if(snp.getCurrentSnp() != null){
                                        current = snp.getCurrentSnp().getRsId();
                                    }
                                    else{
                                        current = snp.getRsId();
                                    }
                                    currentSnp = setOrAppend(currentSnp, current, primary_delimiter);

                                    snp.getLocations().forEach(
                                        location -> {
                                            if (!regions.contains(location.getRegion().getName())) {
                                                regions.add(location.getRegion().getName());
                                                regionString[0] = setOrAppend(regionString[0], location.getRegion().getName(), secondary_delimiter);
                                            }
                                        });


                                    entrezMappedGene[0] = setOrAppend(entrezMappedGene[0], getMappedGeneString(association, snp, "NCBI"), primary_delimiter);

                                    // and add entrez links for each entrez mapped gene
                                    entrezMappedGeneLinks.addAll(createMappedGeneLinks(snp, "NCBI"));

                                    contextString[0]= setOrAppend(contextString[0], snp.getFunctionalClass(), primary_delimiter);
                                    Collection<Location> snpLocations = snp.getLocations();
                                    for (Location snpLocation : snpLocations) {
                                        String chromName= snpLocation.getChromosomeName();

                                        chromosomeNames.add(chromName);

                                        if (snpLocation.getChromosomePosition() != null) {
                                            chromosomePositions.add(snpLocation.getChromosomePosition());
                                        }
                                        positionLinks.add(createPositionLink(snpLocation));

                                        String pattern = "^\\d+$";

                                        Pattern p = Pattern.compile(pattern);

                                        Matcher m = p.matcher(chromName);

                                        if (m.find() || chromName.equals("X") || chromName.equals("Y")) {
                                            positionString[0] = setOrAppend(positionString[0], getPositionString(snpLocation), primary_delimiter);
                                        }
                                    }

                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
//                            reportedGenes.add(gene.getGeneName().trim());
                            localGeneString[0] = setOrAppend(localGeneString[0], gene.getGeneName().trim(), ", ");
                            String reportedGeneLink = createReportedGeneLink(gene);
                            if (reportedGeneLink != null) {
                                reportedGeneLinks.add(reportedGeneLink);
                            }
                        });

                        locusDescription = locus.getDescription();
                        reportedGeneString[0] = setOrAppend(reportedGeneString[0], localGeneString[0], secondary_delimiter);
                    }
            );
            entrezMappedGenes.add(entrezMappedGene[0]);
            reportedGenes.add(reportedGeneString[0]);
            region.add(regionString[0]);
            chromLocations.add(positionString[0]);
            context = contextString[0];

        }
        else if (association.getMultiSnpHaplotype()){

        }
        else {
            // this is a single study
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                    strongestAllele =
                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), ", ");

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    rsId = setOrAppend(rsId, snp.getRsId(), ", ");

                                    merged = snp.getMerged();

                                    String current;
                                    if(snp.getCurrentSnp() != null){
                                         current = snp.getCurrentSnp().getRsId();
                                    }
                                    else{
                                        current = snp.getRsId();
                                    }
                                    currentSnp = setOrAppend(currentSnp, current, ", ");

                                    snp.getLocations().forEach(
                                            location -> {
                                                if (!region.contains(location.getRegion().getName())) {
                                                    region.add(location.getRegion().getName());
                                                }
                                            });

                                    if(association.getMultiSnpHaplotype()){
                                        entrezMappedGenes.add(getMappedGeneString(association, snp, "NCBI"));
                                    }
                                    else{
                                        entrezMappedGenes.addAll(getMappedGenes(association, snp, "NCBI"));

                                        // and add entrez links for each entrez mapped gene
                                        entrezMappedGeneLinks.addAll(createMappedGeneLinks(snp, "NCBI"));
                                    }


                                    context = snp.getFunctionalClass();
                                    Collection<Location> snpLocations = snp.getLocations();
                                    for (Location snpLocation : snpLocations) {
                                        String chromName= snpLocation.getChromosomeName();

                                        chromosomeNames.add(chromName);

                                        if (snpLocation.getChromosomePosition() != null) {
                                            chromosomePositions.add(snpLocation.getChromosomePosition());
                                        }
                                        positionLinks.add(createPositionLink(snpLocation));

                                        String pattern = "^\\d+$";

                                        Pattern p = Pattern.compile(pattern);

                                        Matcher m = p.matcher(chromName);

                                        if (m.find() || chromName.equals("X") || chromName.equals("Y")) {
                                            chromLocations.add(getPositionString(snpLocation));
                                        }
                                    }
                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            reportedGenes.add(gene.getGeneName().trim());
                            String reportedGeneLink = createReportedGeneLink(gene);
                            if (reportedGeneLink != null) {
                                reportedGeneLinks.add(reportedGeneLink);
                            }
                        });
                        locusDescription = locus.getDescription();
                    }
            );
        }
    }

    private String getPositionString(Location snpLocation) {
        String chrom = snpLocation.getChromosomeName();
        Integer position = snpLocation.getChromosomePosition();

        String location = chrom.concat(":").concat(position.toString());

        return location;
    }

    private Collection<String> getMappedGenes(Association association,
                                              SingleNucleotidePolymorphism snp,
                                              String source) {
        //        Map<Long, Set<String>> mappedGenesToLocation = new HashMap<>();
//        Map<Long, List<String>> closestUpstreamDownstreamGenesToLocation = new HashMap<>();
        Set<String> closestUpstreamDownstreamGenes = new LinkedHashSet<>();
        Set<String> mappedGeneList = new LinkedHashSet<>();
        Collection<String> mappedGenes = new LinkedHashSet<>();

        snp.getGenomicContexts().forEach(
                context -> {
                    if (context.getGene() != null && context.getGene().getGeneName() != null &&
                            context.getSource() != null) {

                        if (source.equalsIgnoreCase(context.getSource())) {

                            String location = context.getLocation().getChromosomeName();

                            String pattern = "^\\d+$";

                            Pattern p = Pattern.compile(pattern);

                            Matcher m = p.matcher(location);

                            if (m.find() || location.equals("X") || location.equals("Y")) {
                                String geneName = context.getGene().getGeneName().trim();
//                                    Long locationId = context.getLocation().getId();

                                // Get any overlapping genes
                                if (context.getDistance() == 0) {
                                    mappedGeneList.add(geneName);

                                }


                                // else get the closest upstream and downstream
                                // logic here is to create an array with 2 elements, upstream
                                // at first index and downstream at second
                                else {
                                    if (context.getIsClosestGene() != null && context.getIsClosestGene()) {


                                        closestUpstreamDownstreamGenes.add(geneName);
                                    }
                                }
                            }
                        }
                    }
                });


        if (!mappedGeneList.isEmpty()) {
            mappedGenes.addAll(mappedGeneList);
        }
        else if (!closestUpstreamDownstreamGenes.isEmpty()) {
            mappedGenes.addAll(closestUpstreamDownstreamGenes);
        }
        //        else {
        //            mappedGenes.add("No mapped genes");
        //        }

        return mappedGenes;
    }

    private String createPositionLink(Location snpLocation) {

        String positionLink;

        if (snpLocation.getChromosomeName() != null) {

            positionLink = snpLocation.getChromosomeName();
        }
        else {
            positionLink = "NA";
        }
        if (snpLocation.getChromosomePosition() != null) {
            positionLink = positionLink.concat("|").concat(snpLocation.getChromosomePosition().toString());
        }
        else {
            positionLink = positionLink.concat("|NA");
        }
        if (snpLocation.getRegion().getName() != null) {
            positionLink = positionLink.concat("|").concat(snpLocation.getRegion().getName());
        }
        else {
            positionLink = positionLink.concat("|NA");
        }
        return positionLink;
    }

    /**
     * @param gene
     * @return reported gene link or null if one could not be created
     */
    private String createReportedGeneLink(Gene gene) {

        // Create link information for reported gene
        String geneLink = gene.getGeneName().trim();
        List<String> entrezIds = new ArrayList<String>();
        String entrezLinks = "";
        List<String> ensemblIds = new ArrayList<String>();
        String ensemblLinks = "";

        if (gene.getEntrezGeneIds() != null) {
            for (EntrezGene entrezGene : gene.getEntrezGeneIds()) {
                entrezIds.add(entrezGene.getEntrezGeneId());
            }
            entrezLinks = String.join("|", entrezIds);
        }
        if (gene.getEnsemblGeneIds() != null) {
            for (EnsemblGene ensemblGene : gene.getEnsemblGeneIds()) {
                ensemblIds.add(ensemblGene.getEnsemblGeneId());
            }
            ensemblLinks = String.join("|", ensemblIds);
        }

        // Construct link with Ensembl and Entrez IDs for reported gene
        if (!entrezLinks.isEmpty()) {

            if (!ensemblLinks.isEmpty()) {
                geneLink = geneLink.concat("|").concat(entrezLinks).concat("|").concat(ensemblLinks);
            }
            else {
                geneLink = geneLink.concat("|").concat(entrezLinks);
            }
        }
        else if (!ensemblLinks.isEmpty()) {
            geneLink = geneLink.concat("|").concat(ensemblLinks);

        }
        else {
            geneLink = null;
        }

        return geneLink;
    }

    private Collection<String> createMappedGeneLinks(SingleNucleotidePolymorphism snp, String source) {

        Collection<String> mappedGeneLinks = new LinkedHashSet<>();
        snp.getGenomicContexts().forEach(context -> {

            if (source.equalsIgnoreCase(context.getSource())) {

                Gene gene = context.getGene();

                String distance = "";
                if (context.getDistance() != null) {
                    if (context.getDistance() == 0 || context.getIsUpstream()) {
                        distance = String.valueOf(context.getDistance());
                    }
                    else {
                        distance = "-".concat(String.valueOf(context.getDistance()));
                    }
                }

                String location = "";
                if (context.getLocation() != null) {
                    if (context.getLocation().getChromosomeName() != null) {
                        location = context.getLocation().getChromosomeName();
                    }
                }
                else {
                    getLog().warn("SNP: " + snp.getRsId() + " has no location for genomic context: " + context.getId());
                }


                if (source.equalsIgnoreCase("NCBI")) {

                    if (gene.getEntrezGeneIds() != null) {
                        for (EntrezGene entrezGene : gene.getEntrezGeneIds()) {
                            String geneLink =
                                    gene.getGeneName()
                                            .concat("|")
                                            .concat(entrezGene.getEntrezGeneId());
                            if (!distance.equals("")) {
                                geneLink = geneLink.concat("|").concat(distance);
                            }
                            else {
                                geneLink = geneLink.concat("|N/A");
                            }
                            if (!location.equals("")) {
                                geneLink = geneLink.concat("|".concat(location));
                            }
                            else {
                                geneLink = geneLink.concat("|N/A");
                            }
                            mappedGeneLinks.add(geneLink);
                        }
                    }

                }
            }
        });

        return mappedGeneLinks;
    }

    private String getMappedGeneString(Association association, SingleNucleotidePolymorphism snp, String source) {

        // Create maps to handle multiple locations, se set here so we don't get duplicates
        Map<Long, Set<String>> mappedGenesToLocation = new HashMap<>();
        Map<Long, Map<String, String>> closestUpstreamDownstreamGenesToLocation = new HashMap<>();

        List<String> allMappedGenes = new ArrayList<String>();

        if(snp.getGenomicContexts().isEmpty()){
             allMappedGenes.add("No mapped genes");
        }
        else {
            snp.getGenomicContexts().forEach(
                    context -> {
                        if (context.getGene() != null && context.getGene().getGeneName() != null &&
                                context.getSource() != null) {

                            if (source.equalsIgnoreCase(context.getSource())) {


                                String location = context.getLocation().getChromosomeName();

                                String pattern = "^\\d+$";

                                Pattern p = Pattern.compile(pattern);

                                Matcher m = p.matcher(location);

                                if (m.find() || location.equals("X") || location.equals("Y")) {


                                    String geneName = context.getGene().getGeneName().trim();
                                    Long locationId = context.getLocation().getId();

                                    // Get any overlapping genes
                                    if (context.getDistance() == 0) {

                                        if (mappedGenesToLocation.containsKey(locationId)) {
                                            mappedGenesToLocation.get(locationId).add(geneName);
                                        }

                                        else {
                                            Set<String> mappedGenes = new HashSet<>();
                                            mappedGenes.add(geneName);
                                            mappedGenesToLocation.put(locationId, mappedGenes);
                                        }

                                    }

                                    // else get the closest upstream and downstream
                                    // logic here is to create an array with 2 elements, upstream
                                    // at first index and downstream at second
                                    else {
                                        if (context.getIsClosestGene() != null && context.getIsClosestGene()) {

                                            if (closestUpstreamDownstreamGenesToLocation.containsKey(locationId)) {
                                                if (context.getIsUpstream()) {
                                                    closestUpstreamDownstreamGenesToLocation.get(locationId)
                                                            .put("up", geneName);
                                                }

                                                else if (context.getIsDownstream()) {
                                                    closestUpstreamDownstreamGenesToLocation.get(locationId).put("down",
                                                                                                                 geneName);
                                                }
                                            }

                                            else {
                                                Map<String, String> closestUpstreamDownstreamGenes = new HashMap<>();
                                                if (context.getIsUpstream()) {
                                                    closestUpstreamDownstreamGenes.put("up", geneName);
                                                }

                                                else if (context.getIsDownstream()) {
                                                    closestUpstreamDownstreamGenes.put("down", geneName);
                                                }
                                                else {
                                                    getLog().warn(
                                                            "No closest upstream and downstream gene for association: " +
                                                                    association.getId() + ", snp: " + snp.getRsId() +
                                                                    ", for source " + source);
                                                }

                                                if (!closestUpstreamDownstreamGenes.isEmpty()) {
                                                    closestUpstreamDownstreamGenesToLocation.put(locationId,
                                                                                                 closestUpstreamDownstreamGenes);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
        }

        // Create list of all mapped genes for this SNP
        for (Long locationId : mappedGenesToLocation.keySet()) {

            Set<String> mappedGenes = mappedGenesToLocation.get(locationId);

            for (String gene : mappedGenes) {
                allMappedGenes.add(gene);
            }

            // Remove this location from the upstream/downstream gene map as it has a mapped gene
            closestUpstreamDownstreamGenesToLocation.remove(locationId);
        }

        // Map should only contain location where there are no mapped genes
        List<String> allUpstreamAndDownstreamGenes = new ArrayList<String>();
        for (Long locationId : closestUpstreamDownstreamGenesToLocation.keySet()) {

            if (closestUpstreamDownstreamGenesToLocation.get(locationId) != null) {

                Map<String, String> closestUpstreamDownstreamGenes = closestUpstreamDownstreamGenesToLocation.get(locationId);
                String upstreamDownstreamGeneString = "";

                if(closestUpstreamDownstreamGenes.get("up") != null){
                    upstreamDownstreamGeneString = upstreamDownstreamGeneString.concat(closestUpstreamDownstreamGenes.get("up"));
                }
                else{
                    upstreamDownstreamGeneString = upstreamDownstreamGeneString.concat("N/A");
                }

                if(closestUpstreamDownstreamGenes.get("down") != null){
                    upstreamDownstreamGeneString = upstreamDownstreamGeneString.concat(" - ").concat(closestUpstreamDownstreamGenes.get("down"));
                }
                else{
                    upstreamDownstreamGeneString = upstreamDownstreamGeneString.concat(" - N/A");

                }

                allUpstreamAndDownstreamGenes.add(upstreamDownstreamGeneString);
            }

        }

        String geneString = "";

        if (!allMappedGenes.isEmpty()) {
            if(association.getMultiSnpHaplotype()){
                geneString = String.join("; ", allMappedGenes);

            }
            else{
                geneString = String.join(", ", allMappedGenes);
            }
        }

        else if (!allUpstreamAndDownstreamGenes.isEmpty()) {
            if(association.getMultiSnpHaplotype()){
                geneString = String.join("; ", allUpstreamAndDownstreamGenes);
            }
            else{
                geneString = String.join(", ", allUpstreamAndDownstreamGenes);
            }
        }

        return geneString;
    }

    private String setOrAppend(String current, String toAppend, String delim) {
        if (toAppend != null && !toAppend.isEmpty()) {
            if (current == null || current.isEmpty()) {
                current = toAppend;
            }
            else {
                current = current.concat(delim).concat(toAppend);
            }
        }
        return current;
    }

    private StringBuilder setOrAppend(StringBuilder current, String toAppend, String delim) {
        if (toAppend != null && !toAppend.isEmpty()) {
            if (current.length() == 0) {
                current.append(toAppend);
            }
            else {
                current.append(delim).append(toAppend);
            }
        }
        return current;
    }

    public String getLocusDescription() {
        return locusDescription;
    }

    public void setLocusDescription(String locusDescription) {
        this.locusDescription = locusDescription;
    }

    public Float getBetaNum() {
        return betaNum;
    }

    public String getBetaUnit() {
        return betaUnit;
    }

    public String getBetaDirection() {
        return betaDirection;
    }

    public Boolean getSnpInteraction() {
        return snpInteraction;
    }

    public void setSnpInteraction(Boolean snpInteraction) {
        this.snpInteraction = snpInteraction;
    }

    public Boolean getMultiSnpHaplotype() {
        return multiSnpHaplotype;
    }

    public void setMultiSnpHaplotype(Boolean multiSnpHaplotype) {
        this.multiSnpHaplotype = multiSnpHaplotype;
    }

    public Set<String> getChromLocations() {
        return chromLocations;
    }

    public String getCurrentSnp() {
        return currentSnp;
    }
}
