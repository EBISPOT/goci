package uk.ac.ebi.spot.goci.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.utils.TranslateAuthorUploadHeaders;
import uk.ac.ebi.spot.goci.utils.UploadFileHeader;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

/**
 * Created by emma on 13/04/2016.
 *
 * @author emma
 *         <p>
 *         Test SheetProcessor class
 */
@RunWith(MockitoJUnitRunner.class)
public class SheetProcessorImplTest {

    private UploadSheetProcessor uploadSheetProcessor;

    private XSSFSheet authorDepositedSheet;

    private XSSFSheet curatorSheet;

    @Mock
    private TranslateAuthorUploadHeaders translateAuthorUploadHeaders;

    @Before
    public void setUp() throws Exception {
        uploadSheetProcessor = new SheetProcessorImpl(translateAuthorUploadHeaders);

        // Create spreadsheet for testing
        XSSFWorkbook authorWorkbook = new XSSFWorkbook();
        authorDepositedSheet = authorWorkbook.createSheet("test");

        // Create header row
        XSSFRow row1 = authorDepositedSheet.createRow(0);
        row1.createCell(0).setCellValue("SNP ID (ideally rsID) (see below) (mandatory)");
        row1.createCell(1).setCellValue("Chr(Optional)");
        row1.createCell(2).setCellValue("Bp(Optional)");
        row1.createCell(3).setCellValue("Genome Build(Optional)");
        row1.createCell(4).setCellValue("Effect Allele(Optional)");
        row1.createCell(5).setCellValue("Other Alleles(Optional)");
        row1.createCell(6).setCellValue("Effect Allele Frequency in Controls(Optional)");
        row1.createCell(7).setCellValue("p-value mantissa(Mandatory)");
        row1.createCell(8).setCellValue("p-value exponent(Mandatory)");
        row1.createCell(9).setCellValue("OR(Optional)");
        row1.createCell(10).setCellValue("Beta(Optional)");
        row1.createCell(11).setCellValue("Beta Unit(mandatory if beta is entered)");
        row1.createCell(12).setCellValue("Beta Direction(mandatory if beta is entered)");
        row1.createCell(13).setCellValue("OR/Beta SE(Optional)");
        row1.createCell(14).setCellValue("OR/Beta Range(95% confidence intervals)(Optional)");
        row1.createCell(15).setCellValue("Association Description(Optional)");

        //Create second row
        XSSFRow row2 = authorDepositedSheet.createRow(1);
        row2.createCell(0).setCellValue("rs123"); // SNP
        row2.createCell(1);
        row2.createCell(2);
        row2.createCell(3);
        row2.createCell(4).setCellValue("rs123-?"); // Strongest SNP-Risk Allele
        row2.createCell(5).setCellValue("rs222-T"); // Other Allele
        row2.createCell(6).setCellValue("0.6"); // Effect allele frequency in controls
        row2.createCell(7).setCellValue(1); // P-value mantissa
        row2.createCell(8).setCellValue(-9); // P-value exponent
        row2.createCell(9).setCellValue(2.48); //  OR
        row2.createCell(10); // Beta
        row2.createCell(11); // Beta unit
        row2.createCell(12); // Beta direction
        row2.createCell(13).setCellValue(0.01); // Standard Error
        row2.createCell(14).setCellValue("[1.22-1.43]"); // Range
        row2.createCell(15).setCellValue("This is a description"); // Description

        // Create spreadsheet for testing
        XSSFWorkbook curatorWorkbook = new XSSFWorkbook();
        curatorSheet = curatorWorkbook.createSheet("curator_test");

        // Create header row
        XSSFRow rowc1 = curatorSheet.createRow(0);
        rowc1.createCell(0).setCellValue("Gene(s)");
        rowc1.createCell(1).setCellValue("Strongest SNP-Risk Allele");
        rowc1.createCell(2).setCellValue("SNP");
        rowc1.createCell(3).setCellValue("Proxy SNP");
        rowc1.createCell(4).setCellValue("Independent SNP risk allele frequency in controls");
        rowc1.createCell(5)
                .setCellValue("Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls");
        rowc1.createCell(6).setCellValue("P-value mantissa");
        rowc1.createCell(7).setCellValue("P-value exponent");
        rowc1.createCell(8).setCellValue("P-value description");
        rowc1.createCell(9).setCellValue("OR");
        rowc1.createCell(10).setCellValue("OR reciprocal");
        rowc1.createCell(11).setCellValue("Beta");
        rowc1.createCell(12).setCellValue("Beta Unit");
        rowc1.createCell(13).setCellValue("Beta direction");
        rowc1.createCell(14).setCellValue("Range");
        rowc1.createCell(15).setCellValue("OR reciprocal range");
        rowc1.createCell(16).setCellValue("Standard Error");
        rowc1.createCell(17).setCellValue("OR/Beta description");
        rowc1.createCell(18).setCellValue("Multi-SNP Haplotype");
        rowc1.createCell(19).setCellValue("SNP:SNP interaction");
        rowc1.createCell(20).setCellValue("SNP Status");
        rowc1.createCell(21).setCellValue("SNP type (novel/known)");
        rowc1.createCell(22).setCellValue("EFO traits");

        //Create second row
        XSSFRow rowc2 = curatorSheet.createRow(1);
        rowc2.createCell(0).setCellValue("HIBCH, INPP1, STAT1, PMS1");
        rowc2.createCell(1).setCellValue("rs9845942-?");
        rowc2.createCell(2).setCellValue("rs9845942");
        rowc2.createCell(3);
        rowc2.createCell(4);
        rowc2.createCell(5);
        rowc2.createCell(6).setCellValue(1); // P-value mantissa
        rowc2.createCell(7).setCellValue(-9); // P-value exponent
        rowc2.createCell(8).setCellValue("test"); // P-value exponent
        rowc2.createCell(9).setCellValue(2.48); //  OR
        rowc2.createCell(10);
        rowc2.createCell(11); // Beta
        rowc2.createCell(12); // Beta unit
        rowc2.createCell(13); // Beta direction
        rowc2.createCell(14).setCellValue("[NR]");
        rowc2.createCell(15);
        rowc2.createCell(16).setCellValue(0.56);
        rowc2.createCell(17);
        rowc2.createCell(18);
        rowc2.createCell(19);
        rowc2.createCell(20);
        rowc2.createCell(21).setCellValue("novel"); // Range
        rowc2.createCell(22);

        // Translate headers
        when(translateAuthorUploadHeaders.translateToEnumValue("Chr(Optional)")).thenReturn(UploadFileHeader.CHR);
        when(translateAuthorUploadHeaders.translateToEnumValue("Bp(Optional)")).thenReturn(UploadFileHeader.BP);
        when(translateAuthorUploadHeaders.translateToEnumValue("Genome Build(Optional)")).thenReturn(UploadFileHeader.GENOME_BUILD);
        when(translateAuthorUploadHeaders.translateToEnumValue("Other Alleles(Optional)")).thenReturn(UploadFileHeader.OTHER_ALLELES);

        when(translateAuthorUploadHeaders.translateToEnumValue("Gene(s)")).thenReturn(
                UploadFileHeader.GENES);
        when(translateAuthorUploadHeaders.translateToEnumValue("Effect Allele(Optional)")).thenReturn(UploadFileHeader.EFFECT_ALLELE);
        when(translateAuthorUploadHeaders.translateToEnumValue("Strongest SNP-Risk Allele")).thenReturn(UploadFileHeader.EFFECT_ALLELE);
        when(translateAuthorUploadHeaders.translateToEnumValue("SNP ID (ideally rsID) (see below) (mandatory)")).thenReturn(
                UploadFileHeader.SNP);
        when(translateAuthorUploadHeaders.translateToEnumValue("SNP")).thenReturn(UploadFileHeader.SNP);
        when(translateAuthorUploadHeaders.translateToEnumValue("Proxy SNP")).thenReturn(UploadFileHeader.PROXY_SNP);

        when(translateAuthorUploadHeaders.translateToEnumValue("Effect Allele Frequency in Controls(Optional)")).thenReturn(
                UploadFileHeader.EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS);
        when(translateAuthorUploadHeaders.translateToEnumValue(
                "Risk element (allele, haplotype or SNPxSNP interaction) frequency in controls")).thenReturn(
                UploadFileHeader.EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS);
        when(translateAuthorUploadHeaders.translateToEnumValue("Independent SNP risk allele frequency in controls")).thenReturn(
                UploadFileHeader.INDEPENDENT_SNP_EFFECT_ALLELE_FREQUENCY_IN_CONTROLS);

        when(translateAuthorUploadHeaders.translateToEnumValue("p-value mantissa(Mandatory)")).thenReturn(
                UploadFileHeader.PVALUE_MANTISSA);
        when(translateAuthorUploadHeaders.translateToEnumValue("P-value mantissa")).thenReturn(
                UploadFileHeader.PVALUE_MANTISSA);
        when(translateAuthorUploadHeaders.translateToEnumValue("p-value exponent(Mandatory)")).thenReturn(
                UploadFileHeader.PVALUE_EXPONENT);
        when(translateAuthorUploadHeaders.translateToEnumValue("P-value exponent")).thenReturn(
                UploadFileHeader.PVALUE_EXPONENT);
        when(translateAuthorUploadHeaders.translateToEnumValue("Association Description(Optional)")).thenReturn(
                UploadFileHeader.PVALUE_DESCRIPTION);
        when(translateAuthorUploadHeaders.translateToEnumValue("P-value description")).thenReturn(
                UploadFileHeader.PVALUE_DESCRIPTION);

        when(translateAuthorUploadHeaders.translateToEnumValue("OR(Optional)")).thenReturn(UploadFileHeader.OR);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR")).thenReturn(UploadFileHeader.OR);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR reciprocal")).thenReturn(UploadFileHeader.OR_RECIPROCAL);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta(Optional)")).thenReturn(UploadFileHeader.BETA);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta")).thenReturn(UploadFileHeader.BETA);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta Unit(mandatory if beta is entered)")).thenReturn(
                UploadFileHeader.BETA_UNIT);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta Unit")).thenReturn(
                UploadFileHeader.BETA_UNIT);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta Direction(mandatory if beta is entered)")).thenReturn(
                UploadFileHeader.BETA_DIRECTION);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta direction")).thenReturn(
                UploadFileHeader.BETA_DIRECTION);

        when(translateAuthorUploadHeaders.translateToEnumValue("OR/Beta Range(95% confidence intervals)(Optional)")).thenReturn(
                UploadFileHeader.RANGE);
        when(translateAuthorUploadHeaders.translateToEnumValue("Range")).thenReturn(UploadFileHeader.RANGE);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR reciprocal range")).thenReturn(UploadFileHeader.OR_RECIPROCAL_RANGE);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR/Beta SE(Optional)")).thenReturn(UploadFileHeader.STANDARD_ERROR);
        when(translateAuthorUploadHeaders.translateToEnumValue("Standard Error")).thenReturn(UploadFileHeader.STANDARD_ERROR);

        when(translateAuthorUploadHeaders.translateToEnumValue("OR/Beta description")).thenReturn(UploadFileHeader.DESCRIPTION);
        when(translateAuthorUploadHeaders.translateToEnumValue("Multi-SNP Haplotype")).thenReturn(UploadFileHeader.MULTI_SNP_HAPLOTYPE);
        when(translateAuthorUploadHeaders.translateToEnumValue("SNP:SNP interaction")).thenReturn(UploadFileHeader.SNP_INTERACTION);
        when(translateAuthorUploadHeaders.translateToEnumValue("SNP Status")).thenReturn(UploadFileHeader.SNP_STATUS);
        when(translateAuthorUploadHeaders.translateToEnumValue("SNP type (novel/known)")).thenReturn(UploadFileHeader.SNP_TYPE);
        when(translateAuthorUploadHeaders.translateToEnumValue("EFO traits")).thenReturn(UploadFileHeader.EFO_TRAITS);
    }

    @Test
    public void testReadSheetRowsAuthorSheet() throws Exception {

        Collection<AssociationUploadRow> rows = uploadSheetProcessor.readSheetRows(authorDepositedSheet);
        assertThat(rows).isInstanceOf(Collection.class);
        assertThat(rows).hasOnlyElementsOfType(AssociationUploadRow.class);
        assertThat(rows).hasSize(1);

        // Check first row
        assertThat(rows).extracting("rowNumber",
                                    "authorReportedGene",
                                    "strongestAllele",
                                    "snp",
                                    "proxy",
                                    "riskFrequency",
                                    "associationRiskFrequency",
                                    "pvalueMantissa",
                                    "pvalueExponent",
                                    "pvalueDescription",
                                    "orPerCopyNum",
                                    "orPerCopyRecip",
                                    "betaNum",
                                    "betaUnit",
                                    "betaDirection",
                                    "range",
                                    "orPerCopyRecipRange",
                                    "standardError",
                                    "description",
                                    "multiSnpHaplotype",
                                    "snpInteraction",
                                    "snpStatus",
                                    "snpType",
                                    "efoTrait")
                .containsExactly(tuple(1,
                                       null,
                                       "rs123-?",
                                       "rs123",
                                       null,
                                       null,
                                       "0.6",
                                       1,
                                       -9,
                                       "This is a description", (float) 2.48, null, null, null, null, "[1.22-1.43]",
                                       null, (float) 0.01, null, "N", "N", null, null, null));
    }

    @Test
    public void testReadSheetRowsCuratorSheet() throws Exception {

        Collection<AssociationUploadRow> rows = uploadSheetProcessor.readSheetRows(curatorSheet);
        assertThat(rows).isInstanceOf(Collection.class);
        assertThat(rows).hasOnlyElementsOfType(AssociationUploadRow.class);
        assertThat(rows).hasSize(1);

        // Check first row
        assertThat(rows).extracting("rowNumber",
                                    "authorReportedGene",
                                    "strongestAllele",
                                    "snp",
                                    "proxy",
                                    "riskFrequency",
                                    "associationRiskFrequency",
                                    "pvalueMantissa",
                                    "pvalueExponent",
                                    "pvalueDescription",
                                    "orPerCopyNum",
                                    "orPerCopyRecip",
                                    "betaNum",
                                    "betaUnit",
                                    "betaDirection",
                                    "range",
                                    "orPerCopyRecipRange",
                                    "standardError",
                                    "description",
                                    "multiSnpHaplotype",
                                    "snpInteraction",
                                    "snpStatus",
                                    "snpType",
                                    "efoTrait")
                .containsExactly(tuple(1,
                                       "HIBCH, INPP1, STAT1, PMS1",
                                       "rs9845942-?",
                                       "rs9845942",
                                       null,
                                       null,
                                       "NR",
                                       1,
                                       -9,
                                       "test", (float) 2.48, null, null, null, null, "[NR]",
                                       null, (float) 0.56, null, "N", "N", null, "novel", null));
    }

    @Test
    public void testCreateHeaderMapWithAuthorSheet() throws Exception {

        Map<Integer, UploadFileHeader> headerRowMap =
                uploadSheetProcessor.createHeaderMap(authorDepositedSheet.getRow(0));
        assertThat(headerRowMap).isNotEmpty().hasSize(16);
        assertThat(headerRowMap).containsValues(UploadFileHeader.SNP,
                                                UploadFileHeader.CHR,
                                                UploadFileHeader.BP,
                                                UploadFileHeader.GENOME_BUILD,
                                                UploadFileHeader.EFFECT_ALLELE,
                                                UploadFileHeader.OTHER_ALLELES,
                                                UploadFileHeader.EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS,
                                                UploadFileHeader.PVALUE_MANTISSA,
                                                UploadFileHeader.PVALUE_EXPONENT,
                                                UploadFileHeader.OR,
                                                UploadFileHeader.BETA,
                                                UploadFileHeader.BETA_UNIT,
                                                UploadFileHeader.BETA_DIRECTION,
                                                UploadFileHeader.STANDARD_ERROR,
                                                UploadFileHeader.RANGE,
                                                UploadFileHeader.PVALUE_DESCRIPTION);
    }

    @Test
    public void testCreateHeaderMapWithCuratorSheet() throws Exception {

        Map<Integer, UploadFileHeader> headerRowMap =
                uploadSheetProcessor.createHeaderMap(curatorSheet.getRow(0));
        assertThat(headerRowMap).isNotEmpty().hasSize(23);
        assertThat(headerRowMap).containsValues(UploadFileHeader.GENES,
                                                UploadFileHeader.EFFECT_ALLELE,
                                                UploadFileHeader.SNP,
                                                UploadFileHeader.PROXY_SNP,
                                                UploadFileHeader.INDEPENDENT_SNP_EFFECT_ALLELE_FREQUENCY_IN_CONTROLS,
                                                UploadFileHeader.EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS,
                                                UploadFileHeader.PVALUE_MANTISSA,
                                                UploadFileHeader.PVALUE_EXPONENT,
                                                UploadFileHeader.PVALUE_DESCRIPTION,
                                                UploadFileHeader.OR,
                                                UploadFileHeader.OR_RECIPROCAL,
                                                UploadFileHeader.BETA,
                                                UploadFileHeader.BETA_UNIT,
                                                UploadFileHeader.BETA_DIRECTION,
                                                UploadFileHeader.RANGE,
                                                UploadFileHeader.OR_RECIPROCAL_RANGE,
                                                UploadFileHeader.STANDARD_ERROR,
                                                UploadFileHeader.DESCRIPTION,
                                                UploadFileHeader.MULTI_SNP_HAPLOTYPE,
                                                UploadFileHeader.SNP_INTERACTION,
                                                UploadFileHeader.SNP_STATUS,
                                                UploadFileHeader.SNP_TYPE, UploadFileHeader.EFO_TRAITS);
    }
}