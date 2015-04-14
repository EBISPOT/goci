package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by dwelter on 09/04/15.
 *
 * This is a service class to process a set of associations for a given study and output the result to a tsv file
 *
 */


@Service
public class AssociationDownloadService {

    public AssociationDownloadService() {

    }


    public void createDownloadFile(OutputStream outputStream, Collection<Association> associations)
            throws IOException {

        String file = processAssociations(associations);

        InputStream in = new ByteArrayInputStream(file.getBytes("UTF-8"));

        byte[] outputByte = new byte[4096];
        //copy binary contect to output stream
        while (in.read(outputByte, 0, 4096) != -1) {
            outputStream.write(outputByte, 0, 4096);
        }
        in.close();
        outputStream.flush();

    }

    private String processAssociations(Collection<Association> associations) {

        String header =
                "Gene\tStrongest SNP-Risk Allele\tSNP\tRisk Allele Frequency in Controls\tP-value mantissa\tP-value exponent\tP-value (Text)\tOR per copy or beta (Num)\tOR entered (reciprocal)\tOR-type? (Y/N)\tMulti-SNP Haplotype?\tConfidence Interval\tBeta unit and direction\tStandard Error\tSNP type (novel/known)\tEFO traits\r\n";

        StringBuilder output = new StringBuilder();
        output.append(header);

        for (Association association : associations) {
            StringBuilder line = new StringBuilder();

            extractGeneticData(association, line);

            if(association.getRiskFrequency() == null){
                line.append("");
            }
            else {
                line.append(association.getRiskFrequency());
            }
            line.append("\t");
            if(association.getPvalueMantissa() == null){
                line.append("");
            }
            else {
                line.append(association.getPvalueMantissa());
            }
            line.append("\t");
            if(association.getPvalueExponent() == null){
                line.append("");
            }
            else {
                line.append(association.getPvalueExponent());
            }
            line.append("\t");
            if(association.getPvalueText() == null){
                line.append("");
            }
            else {
                line.append(association.getPvalueText());
            }
            line.append("\t");
            if(association.getOrPerCopyNum() == null){
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyNum());
            }
            line.append("\t");
            if(association.getOrPerCopyRecip() == null){
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRecip());
            }
            line.append("\t");
            if (association.getOrType()) {
                line.append("Y");
            }
            else {
                line.append("N");
            }
            line.append("\t");
            if (association.getMultiSnpHaplotype()) {
                line.append("Y");
            }
            else {
                line.append("N");
            }
            line.append("\t");
            if(association.getOrPerCopyRange() == null){
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyRange());
            }
            line.append("\t");

            if(association.getOrPerCopyUnitDescr() == null){
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyUnitDescr());
            }

            line.append("\t");

            if(association.getOrPerCopyStdError() == null){
                line.append("");
            }
            else {
                line.append(association.getOrPerCopyStdError());
            }

            line.append("\t");
            line.append(association.getSnpType());
            line.append("\t");

            if(association.getEfoTraits() == null){
                line.append("");
            }
            else {
                extractEfoTraits(association.getEfoTraits(), line);
            }
            line.append("\r\n");

            output.append(line.toString());
        }

        return output.toString();
    }

    private void extractEfoTraits(Collection<EfoTrait> efoTraits, StringBuilder line) {
        StringBuilder traits = new StringBuilder();
        for(EfoTrait efoTrait : efoTraits){
            String uri = efoTrait.getUri();
            String[] elements = uri.split("/");

            String id = elements[elements.length-1];
            setOrAppend(traits, id.trim(), ",");
        }

        line.append(traits.toString());
    }

    private void extractGeneticData(Association association, StringBuilder line) {
        final StringBuilder strongestAllele = new StringBuilder();
        final StringBuilder reportedGenes = new StringBuilder();
        final StringBuilder rsId = new StringBuilder();

        if (association.getLoci().size() > 1) {
            // if this association has multiple loci, this is a SNP x SNP study
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                            setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), " x ");

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    setOrAppend(rsId, snp.getRsId(), " x ");

                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
                             setOrAppend(reportedGenes, gene.getGeneName().trim(), ", ");
                        });
                    }
            );
        }
        else {
            // this is a single study or a haplotype
            association.getLoci().forEach(
                    locus -> {
                        locus.getStrongestRiskAlleles().forEach(
                                riskAllele -> {
                                    setOrAppend(strongestAllele, riskAllele.getRiskAlleleName(), ", ");

                                    SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                                    setOrAppend(rsId, snp.getRsId(), ", ");

                                }
                        );
                        locus.getAuthorReportedGenes().forEach(gene -> {
                            setOrAppend(reportedGenes, gene.getGeneName().trim(), ", ");
                        });
                    }
            );
        }
        line.append(reportedGenes.toString());
        line.append("\t");
        line.append(strongestAllele.toString());
        line.append("\t");
        line.append(rsId.toString());
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