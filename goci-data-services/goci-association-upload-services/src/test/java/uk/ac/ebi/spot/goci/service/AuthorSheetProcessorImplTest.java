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
public class AuthorSheetProcessorImplTest {

    private UploadSheetProcessor uploadSheetProcessor;

    private XSSFSheet sheet;

    @Mock
    private TranslateAuthorUploadHeaders translateAuthorUploadHeaders;

    @Before
    public void setUp() throws Exception {
        uploadSheetProcessor = new AuthorSheetProcessorImpl(translateAuthorUploadHeaders);

        // Create spreadsheet for testing
        XSSFWorkbook workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("test");

        // Create header row
        XSSFRow row1 = sheet.createRow(0);
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
        XSSFRow row2 = sheet.createRow(1);
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

        when(translateAuthorUploadHeaders.translateToEnumValue("SNP ID (ideally rsID) (see below) (mandatory)")).thenReturn(
                UploadFileHeader.SNP_ID);
        when(translateAuthorUploadHeaders.translateToEnumValue("Chr(Optional)")).thenReturn(UploadFileHeader.CHR);
        when(translateAuthorUploadHeaders.translateToEnumValue("Bp(Optional)")).thenReturn(UploadFileHeader.BP);
        when(translateAuthorUploadHeaders.translateToEnumValue("Genome Build(Optional)")).thenReturn(UploadFileHeader.GENOME_BUILD);
        when(translateAuthorUploadHeaders.translateToEnumValue("Effect Allele(Optional)")).thenReturn(UploadFileHeader.EFFECT_ALLELE);
        when(translateAuthorUploadHeaders.translateToEnumValue("Other Alleles(Optional)")).thenReturn(UploadFileHeader.OTHER_ALLELES);
        when(translateAuthorUploadHeaders.translateToEnumValue("Effect Allele Frequency in Controls(Optional)")).thenReturn(
                UploadFileHeader.EFFECT_ALLELE_FREQUENCY_IN_CONTROLS);
        when(translateAuthorUploadHeaders.translateToEnumValue("p-value mantissa(Mandatory)")).thenReturn(UploadFileHeader.PVALUE_MANTISSA);
        when(translateAuthorUploadHeaders.translateToEnumValue("p-value exponent(Mandatory)")).thenReturn(UploadFileHeader.PVALUE_EXPONENT);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR(Optional)")).thenReturn(UploadFileHeader.OR);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta(Optional)")).thenReturn(UploadFileHeader.BETA);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta Unit(mandatory if beta is entered)")).thenReturn(
                UploadFileHeader.BETA_UNIT);
        when(translateAuthorUploadHeaders.translateToEnumValue("Beta Direction(mandatory if beta is entered)")).thenReturn(
                UploadFileHeader.BETA_DIRECTION);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR/Beta SE(Optional)")).thenReturn(UploadFileHeader.STANDARD_ERROR);
        when(translateAuthorUploadHeaders.translateToEnumValue("OR/Beta Range(95% confidence intervals)(Optional)")).thenReturn(
                UploadFileHeader.RANGE);
        when(translateAuthorUploadHeaders.translateToEnumValue("Association Description(Optional)")).thenReturn(
                UploadFileHeader.PVALUE_DESCRIPTION);
    }

    @Test
    public void testReadSheetRows() throws Exception {

        Collection<AssociationUploadRow> rows = uploadSheetProcessor.readSheetRows(sheet);
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
                                       null, (float) 0.01, null, null, null, null, null, null));
    }

    @Test
    public void testCreateHeaderMap() throws Exception {

        Map<Integer, UploadFileHeader> headerRowMap = uploadSheetProcessor.createHeaderMap(sheet.getRow(0));
        assertThat(headerRowMap).isNotEmpty().hasSize(16);
        assertThat(headerRowMap).containsValues(UploadFileHeader.SNP_ID,
                                                UploadFileHeader.CHR,
                                                UploadFileHeader.BP,
                                                UploadFileHeader.GENOME_BUILD,
                                                UploadFileHeader.EFFECT_ALLELE,
                                                UploadFileHeader.OTHER_ALLELES,
                                                UploadFileHeader.EFFECT_ALLELE_FREQUENCY_IN_CONTROLS,
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
}