package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.MappedGeneDocument;
import uk.ac.ebi.spot.goci.model.ReportedGeneDocument;
import uk.ac.ebi.spot.goci.model.SnpDocument;
import uk.ac.ebi.spot.goci.model.StudyDocument;
import uk.ac.ebi.spot.goci.model.TraitDocument;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 13/02/15
 */
@Service
public class AssociationEnrichmentService implements DocumentEnrichmentService<AssociationDocument> {
    private StudyService studyService;
    private SingleNucleotidePolymorphismService snpService;
    private GeneService geneService;
    private TraitService traitService;

    @Autowired
    public AssociationEnrichmentService(StudyService studyService,
                                        SingleNucleotidePolymorphismService snpService,
                                        GeneService geneService,
                                        TraitService traitService) {
        this.studyService = studyService;
        this.snpService = snpService;
        this.geneService = geneService;
        this.traitService = traitService;
    }

    @Override public int getPriority() {
        return 1;
    }

    @Override public void doEnrichment(AssociationDocument document) {
        long id = Long.valueOf(document.getId().split(":")[1]);

        studyService.findByAssociationId(id).forEach(
                study -> document.embed(new StudyDocument(study)));
        snpService.findByAssociationId(id).forEach(
                snp -> document.embed(new SnpDocument(snp)));
        geneService.findReportedGenesByAssociationId(id).forEach(
                gene -> document.embed(new ReportedGeneDocument(gene)));
        geneService.findMappedGenesByAssociationId(id).forEach(
                gene -> document.embed(new MappedGeneDocument(gene)));
        traitService.findMappedTraitByAssociationId(id).forEach(
                trait -> document.embed(new EfoDocument(trait)));
    }

//    private void addGenes(Association association) {
//        this.reportedGenes = new HashSet<>();
//        if (association.getLoci().size() > 1) {
//            // if this association has multiple loci, this is a SNP x SNP study
//            association.getLoci().forEach(
//                    locus -> {
//                        locus.getStrongestRiskAlleles().forEach(
//                                riskAllele -> {
//                                    strongestAllele =
//                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), " x ");
//
//                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
//                                    rsId = setOrAppend(rsId, snp.getRsId(), " x ");
//
//                                    final StringBuilder regionBuilder = new StringBuilder();
//                                    snp.getRegions().forEach(
//                                            region -> setOrAppend(regionBuilder, region.getName(), " / "));
//                                    region = setOrAppend(region, regionBuilder.toString(), " : ");
//
//                                    final StringBuilder mappedGeneBuilder = new StringBuilder();
//                                    snp.getGenomicContexts().forEach(
//                                            context -> setOrAppend(mappedGeneBuilder,
//                                                                   context.getGene().getGeneName(),
//                                                                   " / "));
//                                    mappedGene = setOrAppend(mappedGene, mappedGeneBuilder.toString(), " : ");
//
//                                    context = snp.getFunctionalClass();
//                                    chromosomeNames.add(snp.getChromosomeName());
//                                    if (snp.getChromosomePosition() != null) {
//                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
//                                    }
//                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                    if (snp.getLastUpdateDate() != null) {
//                                        lastModifiedDates.add(df.format(snp.getLastUpdateDate()));
//                                    }
//                                }
//                        );
//                        locus.getAuthorReportedGenes().forEach(gene -> reportedGenes.add(gene.getGeneName()));
//                    }
//            );
//        }
//        else {
//            // this is a single study or a haplotype
//            association.getLoci().forEach(
//                    locus -> {
//                        locus.getStrongestRiskAlleles().forEach(
//                                riskAllele -> {
//                                    strongestAllele =
//                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), ", ");
//
//                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
//                                    rsId = setOrAppend(rsId, snp.getRsId(), ", ");
//
//                                    final StringBuilder regionBuilder = new StringBuilder();
//                                    snp.getRegions().forEach(
//                                            region -> setOrAppend(regionBuilder, region.getName(), " / "));
//                                    region = setOrAppend(region, regionBuilder.toString(), ", ");
//
//                                    final StringBuilder mappedGeneBuilder = new StringBuilder();
//                                    snp.getGenomicContexts().forEach(
//                                            context -> setOrAppend(mappedGeneBuilder,
//                                                                   context.getGene().getGeneName(),
//                                                                   " / "));
//                                    mappedGene = setOrAppend(mappedGene, mappedGeneBuilder.toString(), ", ");
//
//                                    context = snp.getFunctionalClass();
//                                    chromosomeNames.add(snp.getChromosomeName());
//                                    if (snp.getChromosomePosition() != null) {
//                                        chromosomePositions.add(Integer.parseInt(snp.getChromosomePosition()));
//                                    }
//                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                    if (snp.getLastUpdateDate() != null) {
//                                        lastModifiedDates.add(df.format(snp.getLastUpdateDate()));
//                                    }
//                                }
//                        );
//                        locus.getAuthorReportedGenes().forEach(gene -> reportedGenes.add(gene.getGeneName()));
//                    }
//            );
//        }
//
//    }

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
