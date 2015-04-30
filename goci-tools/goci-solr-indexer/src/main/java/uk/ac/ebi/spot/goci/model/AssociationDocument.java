package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
    @Field @NonEmbeddableField private String orPerCopyUnitDescr;
    @Field @NonEmbeddableField private String orPerCopyRange;
    @Field @NonEmbeddableField private String orType;

    // additional included genetic data...
    // capture loci/risk alleles for association;
    // if many, collapse risk allele and snp into a single field and use
    // 'x' or ',' to separate SNP x SNP and haplotype associations respectively
    @Field private String rsId;
    @Field private String strongestAllele;
    @Field private String context;
    @Field private String region;
    // mapped genes and reported genes must be per snp -
    // if multiple, separate mapped genes with a hyphen (downstream-upstream) and reported genes with a slash,
    // and then include 'x' or ',' as designated by multiple loci/risk alleles
    @Field("mappedGene") private String mappedGene;
    @Field("mappedGeneLinks") private Collection<String> mappedGeneLinks;
    @Field("reportedGene") private Collection<String> reportedGenes;
    @Field("reportedGeneLinks") private Collection<String> reportedGeneLinks;
    @Field @NonEmbeddableField private Long merged;

    @Field("studyId") @NonEmbeddableField private Collection<String> studyIds;

    // pluralise all other information, but retain order
    @Field("chromosomeName") private Set<String> chromosomeNames;
    @Field("chromosomePosition") private Set<Integer> chromosomePositions;

    @Field("locusDescription") private String locusDescription;

    // embedded study info
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

    // embedded DiseaseTrait info
    @Field("traitName") private Collection<String> traitNames;

    // embedded EfoTrait info
    @Field("mappedLabel") private Collection<String> mappedLabels;
    @Field("mappedUri") private Collection<String> mappedUris;

    public AssociationDocument(Association association) {
        super(association);
        this.riskFrequency = association.getRiskFrequency();
        this.qualifier = association.getPvalueText();
        this.orPerCopyUnitDescr = association.getOrPerCopyUnitDescr();
        this.orType = String.valueOf(association.getOrType());
        this.orPerCopyRange = association.getOrPerCopyRange();

        if (association.getOrPerCopyNum() != null) {
            this.orPerCopyNum = association.getOrPerCopyNum();
        }
        if (association.getPvalueMantissa() != null) {
            this.pValueMantissa = association.getPvalueMantissa();
        }
        if(association.getPvalueExponent() != null){
            this.pValueExponent = association.getPvalueExponent();
        }

        this.chromosomeNames = new LinkedHashSet<>();
        this.chromosomePositions = new LinkedHashSet<>();

        this.mappedGeneLinks = new LinkedHashSet<>();
        this.reportedGenes = new LinkedHashSet<>();
        this.reportedGeneLinks = new LinkedHashSet<>();
        this.studyIds = new HashSet<>();
        embedGeneticData(association);

        this.traitNames = new LinkedHashSet<>();

        this.mappedLabels = new LinkedHashSet<>();
        this.mappedUris = new LinkedHashSet<>();
    }

    public String getRegion() {
        return region;
    }

    public String getMappedGene() {
        return mappedGene;
    }

    public Collection<String> getMappedGeneLinks() {
        return mappedGeneLinks;
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

    public String getOrPerCopyRange() {
        return orPerCopyRange;
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

    public Long getMerged() { return  merged; }

    public Set<String> getChromosomeNames() {
        return chromosomeNames;
    }

    public Set<Integer> getChromosomePositions() {
        return chromosomePositions;
    }

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public String getOrType() {
        return orType;
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

    public void addPublicationLink(String publicationLink) {
        this.publicationLink = publicationLink;
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
        if (association.getLoci().size() > 1) {
            // if this association has multiple loci, this is a SNP x SNP study
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                    strongestAllele =
                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), " x ");

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    rsId = setOrAppend(rsId, snp.getRsId(), " x ");

                                    merged = snp.getMerged();

                                    final Set<String> regionNames = new HashSet<>();
                                    final StringBuilder regionBuilder = new StringBuilder();
                                    snp.getRegions().forEach(
                                            region -> {
                                                if (!regionNames.contains(region.getName())) {
                                                    regionNames.add(region.getName());
                                                    setOrAppend(regionBuilder, region.getName(), " / ");
                                                }
                                            });
                                    region = setOrAppend(region, regionBuilder.toString(), " : ");
                                    mappedGene = setOrAppend(mappedGene, getMappedGeneString(association, snp), " : ");
                                    // and add entrez links for each mapped gene
                                    snp.getGenomicContexts().forEach(context -> {
                                        Gene gene = context.getGene();

                                        String distance = "";
                                        if(context.getDistance() != null) {
                                            distance = String.valueOf(context.getDistance());
                                        }
                                        if (gene.getEntrezGeneId() != null) {
                                            String geneLink =
                                                    gene.getGeneName().concat("|").concat(gene.getEntrezGeneId());
                                            if(!distance.equals("")) {
                                                geneLink = geneLink.concat("|").concat(distance);
                                            }
                                                mappedGeneLinks.add(geneLink);
                                        }
                                    });

                                    context = snp.getFunctionalClass();
                                    chromosomeNames.add(snp.getChromosomeName());
                                    if (snp.getChromosomePosition() != null) {
                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
                                    }
                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            reportedGenes.add(gene.getGeneName().trim());
                            if (gene.getEntrezGeneId() != null) {
                                String geneLink = gene.getGeneName().concat("|").concat(gene.getEntrezGeneId());
                                reportedGeneLinks.add(geneLink);
                            }
                        });

                        locusDescription = locus.getDescription();
                    }
            );
        }
        else {
            // this is a single study or a haplotype
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                    strongestAllele =
                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), ", ");

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    rsId = setOrAppend(rsId, snp.getRsId(), ", ");

                                    final Set<String> regionNames = new HashSet<>();
                                    final StringBuilder regionBuilder = new StringBuilder();
                                    snp.getRegions().forEach(
                                            region -> {
                                                if (!regionNames.contains(region.getName())) {
                                                    regionNames.add(region.getName());
                                                    setOrAppend(regionBuilder, region.getName(), " / ");
                                                }
                                            });
                                    region = setOrAppend(region, regionBuilder.toString(), ", ");
                                    mappedGene = setOrAppend(mappedGene, getMappedGeneString(association, snp), ", ");
                                    // and add entrez links for each mapped gene
                                    snp.getGenomicContexts().forEach(context -> {
                                        if (context.getGene() != null) {
                                            Gene gene = context.getGene();

                                            String distance = "";
                                            if(context.getDistance() != null) {
                                                distance = String.valueOf(context.getDistance());
                                            }
                                            if (gene.getEntrezGeneId() != null) {
                                                String geneLink =
                                                        gene.getGeneName().concat("|").concat(gene.getEntrezGeneId());
                                                if(!distance.equals("")) {
                                                    geneLink = geneLink.concat("|").concat(distance);
                                                }
                                                mappedGeneLinks.add(geneLink);
                                            }
                                        }
                                    });

                                    context = snp.getFunctionalClass();
                                    chromosomeNames.add(snp.getChromosomeName());
                                    if (snp.getChromosomePosition() != null) {
                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
                                    }
                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            reportedGenes.add(gene.getGeneName().trim());
                            if (gene.getEntrezGeneId() != null) {
                                String geneLink = gene.getGeneName().concat("|").concat(gene.getEntrezGeneId());
                                reportedGeneLinks.add(geneLink);
                            }
                        });
                        locusDescription = locus.getDescription();
                    }
            );
        }
    }

    private String getMappedGeneString(Association association, SingleNucleotidePolymorphism snp) {
        AtomicBoolean intragenic = new AtomicBoolean(false);
        List<String> genes = new ArrayList<>();
        snp.getGenomicContexts().forEach(
                context -> {
                    if (context.getGene() != null && context.getGene().getGeneName() != null) {
                        String geneName = context.getGene().getGeneName().trim();
                        if (!genes.contains(geneName)) {
                            if (context.isUpstream()) {
                                genes.add(0, geneName);
                                intragenic.set(true);
                            }
                            else {
                                if (context.isDownstream()) {
                                    intragenic.set(true);
                                }
                                genes.add(geneName);
                            }
                        }
                    }
                });
        String geneString = "";
        if (intragenic.get()) {
            // should only be 2 genes - one upstream and one downstream
            if (genes.size() == 2) {
                // todo - in this case, also add upstream and downstream distances
                geneString = genes.get(0).concat(" - ").concat(genes.get(1));
            }
            else {
                // this should probably be an exception, but just logging error to enable indexes to build
                getLog().warn("Indexing bad genetic data for association " +
                                      "'" + association.getId() + "': wrong number of mapped genes " +
                                      "(expected 2, got " + genes.size() + ": " + genes + ")");
                if (genes.size() == 1) {
                    geneString = genes.get(0);
                }
                else {
                    geneString = "N/A";
                }
            }
        }
        else {
            if (!genes.isEmpty()) {
                StringBuilder gsBuilder = new StringBuilder();
                Iterator<String> genesIt = genes.iterator();
                while (genesIt.hasNext()) {
                    gsBuilder.append(genesIt.next());
                    if (genesIt.hasNext()) {
                        gsBuilder.append(", ");
                    }
                }
                geneString = gsBuilder.toString();
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
}
