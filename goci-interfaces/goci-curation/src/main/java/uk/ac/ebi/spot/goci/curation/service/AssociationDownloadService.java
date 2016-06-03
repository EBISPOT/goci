package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dwelter on 09/04/15. Updated by emma
 * <p>
 * This is a service class to process a set of associations for a given study and output the result to a tsv file
 */
@Service
public class AssociationDownloadService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void createDownloadFile(OutputStream outputStream, Collection<Association> associations)
            throws IOException {

        getLog().info("Creating association download file..");
        Future<String> file = processAssociations(associations);

        // Write file
        if(file.isDone()){
            try {
                outputStream.write(file.get().getBytes("UTF-8"));
                outputStream.flush();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        outputStream.close();
    }

    @Async
    private Future<String> processAssociations(Collection<Association> associations) {

        String header =
                "Gene(s)\tStrongest SNP-Risk Allele\tSNP\tProxy SNP" +
                        "\tIndependent SNP risk allele frequency in controls\tRisk element (allele, haplotype or SNPxSNP interaction) frequency in controls" +
                        "\tP-value mantissa\tP-value exponent\tP-value description" +
                        "\tEffect type\tOR\tOR reciprocal" +
                        "\tBeta\tBeta unit\tBeta direction" +
                        "\tRange\tOR reciprocal range" +
                        "\tStandard Error\tOR/Beta description" +
                        "\tMulti-SNP Haplotype?\tSNP:SNP interaction?\tSNP Status\tSNP type (novel/known)\tEFO traits\r\n";


        StringBuilder output = new StringBuilder();
        output.append(header);

        getLog().info("Processing associations...");

        for (Association association : associations) {
            StringBuilder line = new StringBuilder();

            extractGeneticData(association, line);

            if (association.getRiskFrequency() == null) {
                line.append("");
            }
            else {
                line.append(association.getRiskFrequency());

            }
            line.append("\t");

            if (association.getPvalueMantissa() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueMantissa());

            }
            line.append("\t");

            if (association.getPvalueExponent() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueExponent());
            }
            line.append("\t");

            if (association.getPvalueDescription() == null) {
                line.append("");
            }
            else {
                line.append(association.getPvalueDescription());
            }
            line.append("\t");

            // Determine effect type
            if (association.getOrPerCopyNum() == null && association.getBetaNum() == null) {
                line.append("NR");
            }
            else if (association.getOrPerCopyNum() == null && association.getBetaNum() != null) {
                line.append("Beta");
            }
            else if (association.getOrPerCopyNum() != null && association.getBetaNum() == null) {
                line.append("OR");
            }
            else {
                line.append("");
            }
            line.append("\t");

            // OR
            if (association.getOrPerCopyNum() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyNum());
            }
            line.append("\t");

            // OR reciprocal
            if (association.getOrPerCopyRecip() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRecip());
            }
            line.append("\t");

            // Beta num
            if (association.getBetaNum() == null) {
                line.append("");
            }
            else {
                line.append(association.getBetaNum());
            }
            line.append("\t");

            // Beta unit
            if (association.getBetaUnit() == null) {
                line.append("");
            }
            else {
                line.append(association.getBetaUnit());
            }
            line.append("\t");

            // Beta direction
            if (association.getBetaDirection() == null) {
                line.append("");
            }
            else {
                line.append(association.getBetaDirection());
            }
            line.append("\t");

            // Range
            if (association.getRange() == null) {
                line.append("");
            }
            else {
                line.append(association.getRange());
            }
            line.append("\t");

            // OR recip range
            if (association.getOrPerCopyRecipRange() == null) {
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRecipRange());
            }
            line.append("\t");

            // Standard error
            if (association.getStandardError() == null) {
                line.append("");
            }
            else {
                line.append(association.getStandardError());
            }
            line.append("\t");

            // Description
            if (association.getDescription() == null) {
                line.append("");
            }
            else {
                line.append(association.getDescription());
            }
            line.append("\t");

            if (association.getMultiSnpHaplotype() == null) {
                line.append("");
            }
            else {
                if (association.getMultiSnpHaplotype()) {
                    line.append("Y");
                }
                else {
                    line.append("N");
                }
            }
            line.append("\t");

            if (association.getSnpInteraction() == null) {
                line.append("");
            }
            else {
                if (association.getSnpInteraction()) {
                    line.append("Y");
                }
                else {
                    line.append("N");
                }
            }
            line.append("\t");

            // SNP Status
            extractSNPStatus(association, line);

            if (association.getSnpType() == null) {
                line.append("");
            }
            else {
                line.append(association.getSnpType().toLowerCase());
            }
            line.append("\t");


            if (association.getEfoTraits() == null) {
                line.append("");
            }
            else {
                extractEfoTraits(association.getEfoTraits(), line);
            }
            line.append("\r\n");

            output.append(line.toString());

        }
        getLog().info("Processing done...");
        return new AsyncResult<String>(output.toString());
    }

    private void extractSNPStatus(Association association, StringBuilder line) {

        final StringBuilder snpStatuses = new StringBuilder();

        // Only applies to SNP interaction studies, delimiter used is 'x'
        if (association.getSnpInteraction() != null && association.getSnpInteraction()) {
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {

                                    // Genome wide Vs Limited List,
                                    // create a comma separated list per
                                    // risk allele
                                    Collection<String> snpStatus = new ArrayList<>();
                                    String commaSeparatedSnpStatus = "";
                                    if (riskAllele.getLimitedList() != null) {
                                        if (riskAllele.getLimitedList()) {
                                            snpStatus.add("LL");
                                        }
                                    }
                                    if (riskAllele.getGenomeWide() != null) {
                                        if (riskAllele.getGenomeWide()) {
                                            snpStatus.add("GW");
                                        }
                                    }
                                    if (!snpStatus.isEmpty()) {
                                        commaSeparatedSnpStatus = String.join(", ", snpStatus);
                                    }
                                    else { commaSeparatedSnpStatus = "NR";}

                                    setOrAppend(snpStatuses, commaSeparatedSnpStatus, " x ");
                                }
                        );
                    }
            );
        }

        line.append(snpStatuses.toString());
        line.append("\t");
    }

    private void extractEfoTraits(Collection<EfoTrait> efoTraits, StringBuilder line) {
        StringBuilder traits = new StringBuilder();
        for (EfoTrait efoTrait : efoTraits) {
            String uri = efoTrait.getUri();
            String[] elements = uri.split("/");

            String id = elements[elements.length - 1];
            setOrAppend(traits, id.trim(), ",");
        }

        line.append(traits.toString());
    }

    private void extractGeneticData(Association association, StringBuilder line) {
        final StringBuilder strongestAllele = new StringBuilder();
        final StringBuilder reportedGenes = new StringBuilder();
        final StringBuilder rsId = new StringBuilder();
        final StringBuilder proxySnpsRsIds = new StringBuilder();
        final StringBuilder riskAlleleFrequency = new StringBuilder();

        // Set our delimiter for download spreadsheet
        final String delimiter;
        if (association.getSnpInteraction() != null && association.getSnpInteraction()) {
            delimiter = " x ";
        }
        else {
            delimiter = "; ";
        }

        // Interaction specific values
        if (association.getSnpInteraction() != null && association.getSnpInteraction()) {

            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {

                                    // Set Risk allele frequency
                                    if (riskAllele.getRiskFrequency() != null &&
                                            !riskAllele.getRiskFrequency().isEmpty()) {
                                        String frequency = riskAllele.getRiskFrequency();
                                        setOrAppend(riskAlleleFrequency, frequency, delimiter);
                                    }
                                    else {
                                        setOrAppend(riskAlleleFrequency, "NR", delimiter);
                                    }

                                }
                        );

                        // Handle locus genes for SNP interaction studies.
                        // This is so it clear in the download which group
                        // of genes belong to which interaction
                        Collection<String> currentLocusGenes = new ArrayList<>();
                        String commaSeparatedGenes = "";
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            currentLocusGenes.add(gene.getGeneName().trim());
                        });
                        if (!currentLocusGenes.isEmpty()) {
                            commaSeparatedGenes = String.join(", ", currentLocusGenes);
                            setOrAppend(reportedGenes, commaSeparatedGenes, delimiter);
                        }
                        else {
                            setOrAppend(reportedGenes, "NR", delimiter);
                        }
                    }
            );
        }
        else {
            // Single study or a haplotype
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {

                                    // Set Risk allele frequency to blank as its not recorded by curators
                                    // for standard or multi-SNP haplotypes
                                    setOrAppend(riskAlleleFrequency, "", "");

                                }
                        );
                        // For a haplotype all genes are separated by a comma
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            setOrAppend(reportedGenes, gene.getGeneName().trim(), ", ");
                        });
                    }
            );
        }

        // Set attributes common to all associations
        association.getLoci().forEach(
                locus -> {
                    locus.getStrongestRiskAlleles().forEach(
                            riskAllele -> {
                                setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), delimiter);

                                SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                setOrAppend(rsId, snp.getRsId(), delimiter);

                                // Set proxies or 'NR' if non available
                                Collection<String> currentLocusProxies = new ArrayList<>();
                                String colonSeparatedProxies = "";
                                if (riskAllele.getProxySnps() != null) {
                                    for (SingleNucleotidePolymorphism proxySnp : riskAllele.getProxySnps()) {
                                        currentLocusProxies.add(proxySnp.getRsId());
                                    }
                                }

                                // Separate multiple proxies linked by comma
                                if (!currentLocusProxies.isEmpty()) {
                                    colonSeparatedProxies = String.join(", ", currentLocusProxies);
                                    setOrAppend(proxySnpsRsIds, colonSeparatedProxies, delimiter);

                                }
                                else {
                                    setOrAppend(proxySnpsRsIds, "NR", delimiter);
                                }


                            }
                    );

                }
        );


        line.append(reportedGenes.toString());
        line.append("\t");
        line.append(strongestAllele.toString());
        line.append("\t");
        line.append(rsId.toString());
        line.append("\t");
        line.append(proxySnpsRsIds.toString());
        line.append("\t");
        line.append(riskAlleleFrequency.toString());
        line.append("\t");
    }

    private void setOrAppend(StringBuilder current, String toAppend, String delim) {
        if (toAppend != null && !toAppend.isEmpty()) {
            if (current.length() == 0) {
                current.append(toAppend);
            }
            else {
                current.append(delim).append(toAppend);
            }
        }
    }
}