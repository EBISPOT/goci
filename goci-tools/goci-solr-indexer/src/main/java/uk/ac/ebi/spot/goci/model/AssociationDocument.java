package uk.ac.ebi.spot.goci.model;

import org.apache.solr.client.solrj.beans.Field;

import java.beans.Introspector;
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
public class AssociationDocument extends OntologyEnabledDocument<Association> {
    @Field private String region;
    @Field private String mappedGene;
    @Field private String strongestAllele;
    @Field private String context;
    @Field("reportedGene") private Collection<String> reportedGenes;

    @Field private String riskFrequency;
    @Field private String qualifier;

    @Field private float pValue;
    @Field private float orPerCopyNum;
    @Field private String orPerCopyUnitDescr;
    @Field private String orPerCopyRange;
    @Field private String orType;

    @Field private String platform;
    @Field private boolean cnv;

    @Field("trait") private Collection<String> traits;
    @Field("traitUri") private Collection<String> traitUris;

    // additional fields from study
    @Field private String studyId;
    @Field private String pubmedId;
    @Field private String title;
    @Field private String author;
    @Field private String publication;

    // additional search fields from snps
    @Field private String rsId;
    @Field("chromosomeName") private Set<String> chromosomeNames;
    @Field("chromosomePosition") private Set<Integer> chromosomePositions;
    @Field("last_modified") private Set<String> lastModifiedDates;


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
        if (association.getPvalueFloat() != null) {
            this.pValue = association.getPvalueFloat();
        }

        this.traits = new HashSet<>();
        this.traitUris = new HashSet<>();
        association.getEfoTraits().forEach(trait -> {
            traits.add(trait.getTrait());
            traitUris.add(trait.getUri());
        });

        Study study = association.getStudy();
        this.studyId = Introspector.decapitalize(Study.class.getSimpleName())
                .concat(":")
                .concat(String.valueOf(study.getId()));
        this.pubmedId = study.getPubmedId();
        this.title = study.getTitle();
        this.author = study.getAuthor();
        this.publication = study.getPublication();
        this.platform = study.getPlatform();
        this.cnv = study.getCnv();
        if (study.getDiseaseTrait() != null) {
            traits.add(study.getDiseaseTrait().getTrait());
        }
        this.traitUris = new ArrayList<>();
        study.getEfoTraits().forEach(efoTrait -> traitUris.add(efoTrait.getUri()));

        this.chromosomeNames = new HashSet<>();
        this.chromosomePositions = new HashSet<>();
        this.lastModifiedDates = new HashSet<>();

        this.reportedGenes = new HashSet<>();
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

                                    final StringBuilder regionBuilder = new StringBuilder();
                                    snp.getRegions().forEach(
                                            region -> setOrAppend(regionBuilder, region.getName(), " / "));
                                    region = setOrAppend(region, regionBuilder.toString(), " : ");

                                    final StringBuilder mappedGeneBuilder = new StringBuilder();
                                    snp.getGenomicContexts().forEach(
                                            context -> setOrAppend(mappedGeneBuilder,
                                                                   context.getGene().getGeneName(),
                                                                   " / "));
                                    mappedGene = setOrAppend(mappedGene, mappedGeneBuilder.toString(), " : ");

                                    context = snp.getFunctionalClass();
                                    chromosomeNames.add(snp.getChromosomeName());
                                    if (snp.getChromosomePosition() != null) {
                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
                                    }
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    if (snp.getLastUpdateDate() != null) {
                                        lastModifiedDates.add(df.format(snp.getLastUpdateDate()));
                                    }
                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> reportedGenes.add(gene.getGeneName()));
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

                                    final StringBuilder regionBuilder = new StringBuilder();
                                    snp.getRegions().forEach(
                                            region -> setOrAppend(regionBuilder, region.getName(), " / "));
                                    region = setOrAppend(region, regionBuilder.toString(), ", ");

                                    final StringBuilder mappedGeneBuilder = new StringBuilder();
                                    snp.getGenomicContexts().forEach(
                                            context -> setOrAppend(mappedGeneBuilder,
                                                                   context.getGene().getGeneName(),
                                                                   " / "));
                                    mappedGene = setOrAppend(mappedGene, mappedGeneBuilder.toString(), ", ");

                                    context = snp.getFunctionalClass();
                                    chromosomeNames.add(snp.getChromosomeName());
                                    if (snp.getChromosomePosition() != null) {
                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
                                    }
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    if (snp.getLastUpdateDate() != null) {
                                        lastModifiedDates.add(df.format(snp.getLastUpdateDate()));
                                    }
                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> reportedGenes.add(gene.getGeneName()));
                    }
            );
        }
    }

    public String getRegion() {
        return region;
    }

    public String getMappedGene() {
        return mappedGene;
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

    public float getpValue() {
        return pValue;
    }

    public Collection<String> getReportedGenes() {
        return reportedGenes;
    }

    public Collection<String> getTraits() {
        return traits;
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

    public String getPlatform() {
        return platform;
    }

    public boolean isCnv() {
        return cnv;
    }

    public String getRsId() {
        return rsId;
    }

    public Set<String> getChromosomeNames() {
        return chromosomeNames;
    }

    public Set<Integer> getChromosomePositions() {
        return chromosomePositions;
    }

    public Set<String> getLastModifiedDates() {
        return lastModifiedDates;
    }

    public float getOrPerCopyNum() {
        return orPerCopyNum;
    }

    public String getOrType() {
        return orType;
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
